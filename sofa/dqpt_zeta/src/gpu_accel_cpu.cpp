/**
 * CPU fallback for GPU acceleration functions.
 * Used when CUDA is not available.
 * Paper Section 9.3: "the code compiles and runs in CPU-only mode without CUDA."
 */

#include "gpu_accel.h"
#include "system2.h"
#include "system1.h"
#include "partition.h"
#include "config.h"
#include <vector>

bool gpu_available() {
    return false;
}

void gpu_eval_ZH(const std::vector<double>& ts, std::vector<double>& out_ZH) {
    out_ZH.resize(ts.size());
    for (size_t i = 0; i < ts.size(); ++i) {
        out_ZH[i] = hardy_Z(ts[i]);
    }
}

void gpu_eval_L(double beta, const std::vector<double>& ts, int N,
                std::vector<double>& out_abs_L) {
    out_abs_L.resize(ts.size());

    std::vector<double> weights(N), log_n(N);
    precompute_weights(beta, N, weights.data());
    precompute_log_n(N, log_n.data());

    double Z = partition_function(0.0, N, weights.data());

    for (size_t i = 0; i < ts.size(); ++i) {
        Complex eta = partial_eta(0.0, ts[i], N, weights.data(), log_n.data());
        out_abs_L[i] = eta.abs() / Z;
    }
}
