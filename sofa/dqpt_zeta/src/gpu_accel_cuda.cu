/**
 * CUDA GPU acceleration for DQPT-Zeta.
 * Paper Section 3.5.
 *
 * Implements the same algorithms as the CPU path:
 * - Borwein eta for t < 200 (Paper Section 3.1, Eq. 3.11-3.13)
 * - Riemann-Siegel for t >= 200 (Paper Section 3.2.1, Eq. 3.20)
 * - Kahan summation with __dadd_rn/__dsub_rn (Paper Remark 3.2)
 * - Dynamic batch size from VRAM (Paper Section 3.5.4)
 */

#include "gpu_accel.h"
#include <cuda_runtime.h>
#include <cstdio>
#include <cmath>
#include <vector>
#include <algorithm>

static constexpr double GPU_PI = 3.14159265358979323846;
static constexpr double GPU_TWO_PI = 2.0 * GPU_PI;
static constexpr int GPU_BLOCK_SIZE = 256;

// ---- Device functions ----

/**
 * Device-side theta via Stirling (Paper Eq. 3.18).
 */
__device__ static double theta_dev(double t) {
    double t2 = t * t, t3 = t2 * t, t5 = t3 * t2, t7 = t5 * t2;
    return (t / 2.0) * log(t / GPU_TWO_PI) - t / 2.0 - GPU_PI / 8.0
           + 1.0 / (48.0 * t) + 7.0 / (5760.0 * t3)
           + 31.0 / (80640.0 * t5) - 127.0 / (430080.0 * t7);
}

/**
 * Device-side Riemann-Siegel Z_H (Paper Eq. 3.20).
 */
__device__ static double hardy_Z_rs_dev(double t) {
    double tau = t / GPU_TWO_PI;
    int M = (int)sqrt(tau);
    if (M < 1) M = 1;
    double th = theta_dev(t);

    double sum = 0.0, comp = 0.0;
    for (int n = 1; n <= M; ++n) {
        double ln_n = log((double)n);
        double arg = fmod(th - t * ln_n, GPU_TWO_PI);
        double val = pow((double)n, -0.5) * cos(arg);
        double y = __dsub_rn(val, comp);
        double tt = __dadd_rn(sum, y);
        comp = __dsub_rn(__dsub_rn(tt, sum), y);
        sum = tt;
    }
    double main_sum = 2.0 * sum;

    double p = sqrt(tau) - (double)M;
    double pd = cos(GPU_TWO_PI * p);
    double R0 = 0.0;
    if (fabs(pd) > 1e-15) {
        double pa = GPU_TWO_PI * (p * p - p - 1.0 / 16.0);
        double psi = cos(pa) / pd;
        double sg = ((M - 1) % 2 == 0) ? 1.0 : -1.0;
        R0 = sg * pow(tau, -0.25) * psi;
    }
    return main_sum + R0;
}

/**
 * Device-side Borwein Z_H for t < 200 (Paper Eq. 3.11-3.13, Section 3.2.1).
 */
__device__ static double hardy_Z_borwein_dev(double t) {
    int n = 40;
    int n_needed = (int)(fabs(t) * 1.5) + 40;
    if (n_needed > n) n = n_needed;
    if (n > 500) n = 500;

    // Paper Eq. 3.12: factorial recurrence
    double a = 1.0, d_n = 1.0;
    for (int j = 1; j <= n; ++j) {
        a *= 4.0 * (double)(n + j - 1) * (double)(n - j + 1)
             / ((double)(2 * j) * (double)(2 * j - 1));
        d_n += a;
    }

    double eta_re = 0.0, eta_im = 0.0;
    a = 1.0;
    double d_k = 1.0;
    for (int k = 0; k < n; ++k) {
        double coeff = d_k - d_n;
        double sign = (k % 2 == 0) ? 1.0 : -1.0;
        double w = pow((double)(k + 1), -0.5);
        double ln_k1 = log((double)(k + 1));
        double arg = fmod(t * ln_k1, GPU_TWO_PI);
        eta_re += sign * coeff * w * cos(arg);
        eta_im += sign * coeff * (-w) * sin(arg);
        if (k + 1 <= n) {
            int j = k + 1;
            a *= 4.0 * (double)(n + j - 1) * (double)(n - j + 1)
                 / ((double)(2 * j) * (double)(2 * j - 1));
            d_k += a;
        }
    }
    double inv_dn = -1.0 / d_n;
    eta_re *= inv_dn;
    eta_im *= inv_dn;

    // zeta = eta / (1 - 2^{1-s})
    double mag2 = sqrt(2.0);
    double arg2 = -t * log(2.0);
    double d_re = 1.0 - mag2 * cos(arg2);
    double d_im = -mag2 * sin(arg2);
    double dsq = d_re * d_re + d_im * d_im;
    double z_re = (eta_re * d_re + eta_im * d_im) / dsq;
    double z_im = (eta_im * d_re - eta_re * d_im) / dsq;

    double th = theta_dev(t);
    return cos(th) * z_re - sin(th) * z_im;
}

/**
 * Device-side hardy_Z: Borwein for t < 200, RS for t >= 200.
 * Paper Section 3.2.1.
 */
__device__ static double hardy_Z_dev(double t) {
    if (t < 200.0) return hardy_Z_borwein_dev(t);
    return hardy_Z_rs_dev(t);
}

// ---- Kernels ----

__global__ void kernel_eval_ZH(const double* __restrict__ ts, int count,
                               double* __restrict__ out) {
    int idx = blockIdx.x * blockDim.x + threadIdx.x;
    if (idx >= count) return;
    out[idx] = hardy_Z_dev(ts[idx]);
}

__device__ static void kahan_add_dev(double& sum, double& comp, double x) {
    double y = __dsub_rn(x, comp);
    double t = __dadd_rn(sum, y);
    comp = __dsub_rn(__dsub_rn(t, sum), y);
    sum = t;
}

__global__ void kernel_eval_L(const double* __restrict__ ts, int count,
                              int N,
                              const double* __restrict__ weights,
                              const double* __restrict__ log_n,
                              double Z,
                              double* __restrict__ out) {
    int idx = blockIdx.x * blockDim.x + threadIdx.x;
    if (idx >= count) return;

    double t = ts[idx];
    double sum_re = 0.0, comp_re = 0.0;
    double sum_im = 0.0, comp_im = 0.0;

    for (int n = 0; n < N; ++n) {
        double sign = ((n + 1) % 2 == 1) ? 1.0 : -1.0;
        double arg = fmod(t * log_n[n], GPU_TWO_PI);
        kahan_add_dev(sum_re, comp_re, sign * weights[n] * cos(arg));
        kahan_add_dev(sum_im, comp_im, sign * (-weights[n]) * sin(arg));
    }

    double abs_eta = sqrt(sum_re * sum_re + sum_im * sum_im);
    out[idx] = abs_eta / Z;
}

// ---- Host interface ----

bool gpu_available() {
    int count = 0;
    cudaError_t err = cudaGetDeviceCount(&count);
    return (err == cudaSuccess && count > 0);
}

/**
 * Determines batch size from available VRAM.
 * Paper Section 3.5.4: batch size adapted to hardware.
 */
static int get_batch_size(int total_points) {
    size_t free_mem = 0, total_mem = 0;
    cudaMemGetInfo(&free_mem, &total_mem);
    // Each point needs 2 doubles (input t + output Z_H). Use 50% of free VRAM.
    int batch = static_cast<int>(free_mem / (2 * sizeof(double) * 2));
    if (batch < 1000) batch = 1000;
    if (batch > total_points) batch = total_points;
    return batch;
}

void gpu_eval_ZH(const std::vector<double>& ts, std::vector<double>& out_ZH) {
    int total = static_cast<int>(ts.size());
    out_ZH.resize(total);

    if (!gpu_available()) {
        // CPU fallback - should not happen if caller checks, but safety
        for (int i = 0; i < total; ++i) {
            out_ZH[i] = 0.0; // caller should use CPU path
        }
        return;
    }

    int batch = get_batch_size(total);

    double *d_ts, *d_ZH;
    cudaMalloc(&d_ts, batch * sizeof(double));
    cudaMalloc(&d_ZH, batch * sizeof(double));

    for (int offset = 0; offset < total; offset += batch) {
        int count = std::min(batch, total - offset);
        cudaMemcpy(d_ts, ts.data() + offset, count * sizeof(double),
                   cudaMemcpyHostToDevice);
        int grid = (count + GPU_BLOCK_SIZE - 1) / GPU_BLOCK_SIZE;
        kernel_eval_ZH<<<grid, GPU_BLOCK_SIZE>>>(d_ts, count, d_ZH);
        cudaDeviceSynchronize();
        cudaMemcpy(out_ZH.data() + offset, d_ZH, count * sizeof(double),
                   cudaMemcpyDeviceToHost);
    }

    cudaFree(d_ts);
    cudaFree(d_ZH);
}

void gpu_eval_L(double beta, const std::vector<double>& ts, int N,
                std::vector<double>& out_abs_L) {
    int total = static_cast<int>(ts.size());
    out_abs_L.resize(total);

    if (!gpu_available()) {
        return;
    }

    // Precompute weights and log_n on host
    std::vector<double> weights_h(N), log_n_h(N);
    double Z = 0.0;
    for (int n = 1; n <= N; ++n) {
        double w = std::pow((double)n, -beta);
        weights_h[n - 1] = w;
        log_n_h[n - 1] = std::log((double)n);
        Z += w;
    }

    int batch = get_batch_size(total);

    double *d_ts, *d_out, *d_weights, *d_log_n;
    cudaMalloc(&d_ts, batch * sizeof(double));
    cudaMalloc(&d_out, batch * sizeof(double));
    cudaMalloc(&d_weights, N * sizeof(double));
    cudaMalloc(&d_log_n, N * sizeof(double));

    cudaMemcpy(d_weights, weights_h.data(), N * sizeof(double), cudaMemcpyHostToDevice);
    cudaMemcpy(d_log_n, log_n_h.data(), N * sizeof(double), cudaMemcpyHostToDevice);

    for (int offset = 0; offset < total; offset += batch) {
        int count = std::min(batch, total - offset);
        cudaMemcpy(d_ts, ts.data() + offset, count * sizeof(double),
                   cudaMemcpyHostToDevice);
        int grid = (count + GPU_BLOCK_SIZE - 1) / GPU_BLOCK_SIZE;
        kernel_eval_L<<<grid, GPU_BLOCK_SIZE>>>(d_ts, count, N, d_weights, d_log_n, Z, d_out);
        cudaDeviceSynchronize();
        cudaMemcpy(out_abs_L.data() + offset, d_out, count * sizeof(double),
                   cudaMemcpyDeviceToHost);
    }

    cudaFree(d_ts);
    cudaFree(d_out);
    cudaFree(d_weights);
    cudaFree(d_log_n);
}
