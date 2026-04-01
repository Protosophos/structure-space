#include "../src/system1.h"
#include "../src/system2.h"
#include "../src/zeros.h"
#include "../src/config.h"
#include "reference_values.h"
#include <cstdio>
#include <cmath>
#include <vector>

/**
 * V1: |L(0.5, t_k)| < 1e-4 for first 100 zeros with N=10^4 + EM.
 * V2: G(0.5, t_k) sign change width < 1e-3 for first 100 zeros.
 * Paper Section 10.1.
 *
 * Uses first 10 zeros from reference_values.h for now.
 */

static int failures = 0;

int main() {
    std::printf("=== test_known_zeros ===\n\n");

    constexpr int N = N_DEFAULT; // 10000
    std::vector<double> weights(N), log_n(N);
    precompute_weights(0.5, N, weights.data());
    precompute_log_n(N, log_n.data());

    // V1: System 1 at known zeros
    std::printf("--- V1: |L(0.5, t_k)| < 1e-4 ---\n");
    for (int i = 0; i < NUM_ZETA_ZEROS_10; ++i) {
        double t_k = ZETA_ZEROS_10[i];
        Complex L = system1_L(0.5, t_k, N, weights.data(), log_n.data());
        double abs_L = L.abs();
        if (abs_L < 1e-4) {
            std::printf("PASS V1 zero[%d] t=%.6f: |L| = %.2e\n", i+1, t_k, abs_L);
        } else {
            std::printf("FAIL V1 zero[%d] t=%.6f: |L| = %.2e (> 1e-4)\n", i+1, t_k, abs_L);
            ++failures;
        }
    }

    // V2: System 2 sign changes at known zeros
    std::printf("\n--- V2: G(0.5, t_k) sign change ---\n");
    for (int i = 0; i < NUM_ZETA_ZEROS_10; ++i) {
        double t_k = ZETA_ZEROS_10[i];
        double delta = 5e-4; // half-width for sign change search
        double G_lo = hardy_Z(t_k - delta);
        double G_hi = hardy_Z(t_k + delta);

        bool sign_change = (G_lo > 0.0 && G_hi < 0.0) || (G_lo < 0.0 && G_hi > 0.0);
        if (sign_change) {
            std::printf("PASS V2 zero[%d] t=%.6f: sign change in [%.6f, %.6f]\n",
                        i+1, t_k, t_k - delta, t_k + delta);
        } else {
            std::printf("FAIL V2 zero[%d] t=%.6f: no sign change (G_lo=%.6e, G_hi=%.6e)\n",
                        i+1, t_k, G_lo, G_hi);
            ++failures;
        }
    }

    // V3: Zero count check
    std::printf("\n--- V3: Zero count at T=100 ---\n");
    {
        auto zeros = detect_zeros(1.0, 100.0);
        int detected = static_cast<int>(zeros.size());
        bool pass = turing_check(detected, 100.0);
        int predicted = predicted_zero_count(100.0);
        std::printf("Detected: %d, Predicted: %d, |diff|=%d -> %s\n",
                    detected, predicted, std::abs(detected - predicted),
                    pass ? "PASS" : "FAIL");
        if (!pass) ++failures;
    }

    std::printf("\n=== %d failures ===\n", failures);
    return failures > 0 ? 1 : 0;
}
