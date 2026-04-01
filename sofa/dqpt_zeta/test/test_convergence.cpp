#include "../src/dirichlet.h"
#include "../src/config.h"
#include "reference_values.h"
#include <cstdio>
#include <cmath>
#include <vector>

/**
 * V7(a): |eta_{10N}|/|eta_N| in [0.25, 0.40] at s=0.5+14.134725i.
 *        Fail outside [0.20, 0.50].
 * V7(b): |eta_N(s) - eta(s)|/|eta(s)| < 1e-2 at s=0.5+50i for N=10000.
 *        Fail if > 1e-1.
 * Paper Section 10.1.
 *
 * NOTE: V7 measures |eta_N|, NOT |L_N| (which divides by Z_N).
 */

static int failures = 0;

int main() {
    std::printf("=== test_convergence ===\n\n");

    // V7(a): Convergence rate at a zero (s = 0.5 + 14.134725i)
    std::printf("--- V7(a): eta_N convergence at first zero ---\n");
    {
        double beta = 0.5, t = 14.134725;
        int Ns[] = {100, 1000, 10000};

        double abs_eta[3];
        for (int i = 0; i < 3; ++i) {
            std::vector<double> weights(Ns[i]), log_n(Ns[i]);
            precompute_weights(beta, Ns[i], weights.data());
            precompute_log_n(Ns[i], log_n.data());
            Complex eta = partial_eta(beta, t, Ns[i], weights.data(), log_n.data());
            abs_eta[i] = eta.abs();
            std::printf("  N=%5d: |eta_N| = %.6e\n", Ns[i], abs_eta[i]);
        }

        // Check ratio |eta_{10N}| / |eta_N| ~ 1/sqrt(10) ~ 0.316
        for (int i = 0; i < 2; ++i) {
            double ratio = abs_eta[i + 1] / abs_eta[i];
            bool pass = (ratio >= 0.25 && ratio <= 0.40);
            bool hard_fail = (ratio < 0.20 || ratio > 0.50);
            std::printf("  ratio N=%d->%d: %.4f -> %s\n",
                        Ns[i], Ns[i+1], ratio,
                        pass ? "PASS" : (hard_fail ? "FAIL" : "MARGINAL"));
            if (hard_fail) ++failures;
        }
    }

    // V7(b): Convergence at a non-zero point (s = 0.5 + 50i)
    std::printf("\n--- V7(b): eta_N convergence at s=0.5+50i ---\n");
    {
        double beta = 0.5, t = 50.0;
        Complex eta_ref = ETA_TEST_POINTS[2].eta_ref; // s = 0.5 + 50i
        double mag_ref = std::sqrt(eta_ref.re * eta_ref.re + eta_ref.im * eta_ref.im);

        int Ns[] = {100, 1000, 10000};
        for (int i = 0; i < 3; ++i) {
            std::vector<double> weights(Ns[i]), log_n(Ns[i]);
            precompute_weights(beta, Ns[i], weights.data());
            precompute_log_n(Ns[i], log_n.data());
            Complex eta = partial_eta(beta, t, Ns[i], weights.data(), log_n.data());

            double err = std::sqrt((eta.re - eta_ref.re) * (eta.re - eta_ref.re)
                                 + (eta.im - eta_ref.im) * (eta.im - eta_ref.im));
            double rel = err / mag_ref;
            std::printf("  N=%5d: rel_error = %.6e\n", Ns[i], rel);
        }

        // Final check at N=10000
        std::vector<double> weights(10000), log_n(10000);
        precompute_weights(beta, 10000, weights.data());
        precompute_log_n(10000, log_n.data());
        Complex eta = partial_eta(beta, t, 10000, weights.data(), log_n.data());
        double err = std::sqrt((eta.re - eta_ref.re) * (eta.re - eta_ref.re)
                             + (eta.im - eta_ref.im) * (eta.im - eta_ref.im));
        double rel = err / mag_ref;

        if (rel < 1e-2) {
            std::printf("  PASS: rel_error at N=10000 = %.2e (< 1e-2)\n", rel);
        } else if (rel > 1e-1) {
            std::printf("  FAIL: rel_error at N=10000 = %.2e (> 1e-1)\n", rel);
            ++failures;
        } else {
            std::printf("  MARGINAL: rel_error at N=10000 = %.2e\n", rel);
        }
    }

    std::printf("\n=== %d failures ===\n", failures);
    return failures > 0 ? 1 : 0;
}
