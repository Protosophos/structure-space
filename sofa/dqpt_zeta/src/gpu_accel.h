#ifndef GPU_ACCEL_H
#define GPU_ACCEL_H

/**
 * GPU acceleration interface.
 * Paper Section 3.5, 9.3.
 *
 * When compiled with CUDA, these functions offload computation to the GPU.
 * When compiled without CUDA, they fall back to CPU implementations.
 *
 * Paper Section 9.3: "Optional; the code compiles and runs in CPU-only
 * mode without CUDA."
 */

#include "dirichlet.h"
#include <vector>

/**
 * Returns true if CUDA GPU acceleration is available at runtime.
 *
 * @return true if GPU is available and initialized
 */
bool gpu_available();

/**
 * GPU-accelerated evaluation of Z_H(t) at multiple t values.
 * Paper Section 3.5.1: "Each CUDA thread computes one (beta, t) point."
 *
 * Uses Borwein for t < 200, Riemann-Siegel for t >= 200 (Paper Section 3.2.1).
 * Batch size determined dynamically from available VRAM (Paper Section 3.5.4).
 *
 * Falls back to CPU if GPU is not available.
 *
 * @param ts input t values
 * @param out_ZH output Z_H values (same size as ts)
 */
void gpu_eval_ZH(const std::vector<double>& ts, std::vector<double>& out_ZH);

/**
 * GPU-accelerated evaluation of |L_N(beta, t)| at multiple t values
 * for a fixed beta. Paper Section 3.5.1.
 *
 * Falls back to CPU if GPU is not available.
 *
 * @param beta the inverse temperature
 * @param ts input t values
 * @param N truncation parameter
 * @param out_abs_L output |L_N| values (same size as ts)
 */
void gpu_eval_L(double beta, const std::vector<double>& ts, int N,
                std::vector<double>& out_abs_L);

#endif // GPU_ACCEL_H
