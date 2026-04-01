#include "../src/dirichlet.h"
#include "../src/config.h"
#include "reference_values.h"
#include <cstdio>
#include <cstdlib>
#include <cmath>
#include <vector>

/**
 * Tests partial Dirichlet series, partial eta, and Borwein eta
 * against mpmath reference values.
 * Paper Section 4.4.1: agreement to 10 significant digits.
 */

static int failures = 0;

/**
 * Checks complex value agreement to the given number of significant digits.
 *
 * @param label description of the test
 * @param computed the computed complex value
 * @param expected the reference complex value
 * @param digits required significant digits
 */
static void check_complex(const char* label, Complex computed, Complex expected, int digits) {
    double mag = std::sqrt(expected.re * expected.re + expected.im * expected.im);
    double err_re = std::abs(computed.re - expected.re);
    double err_im = std::abs(computed.im - expected.im);
    double err = std::sqrt(err_re * err_re + err_im * err_im);

    if (mag == 0.0) {
        if (err > std::pow(10.0, -digits)) {
            std::printf("FAIL %s: err=%.2e\n", label, err);
            ++failures;
        } else {
            std::printf("PASS %s: err=%.2e\n", label, err);
        }
        return;
    }

    double rel = err / mag;
    if (rel > std::pow(10.0, -digits)) {
        std::printf("FAIL %s: rel=%.2e (re: %.16e vs %.16e, im: %.16e vs %.16e)\n",
                    label, rel, computed.re, expected.re, computed.im, expected.im);
        ++failures;
    } else {
        std::printf("PASS %s: rel=%.2e\n", label, rel);
    }
}

int main() {
    std::printf("=== test_dirichlet ===\n\n");

    // Borwein eta vs mpmath (Paper Eq. 3.14: error < 10^{-30})
    std::printf("--- Borwein eta vs mpmath (10-digit agreement) ---\n");
    for (int i = 0; i < NUM_ETA_TEST_POINTS; ++i) {
        const auto& tp = ETA_TEST_POINTS[i];
        Complex result = borwein_eta(tp.beta, tp.t);
        char label[128];
        std::snprintf(label, sizeof(label),
                      "borwein_eta(%.1f + %.6fi)", tp.beta, tp.t);
        double mag = std::sqrt(tp.eta_ref.re * tp.eta_ref.re
                             + tp.eta_ref.im * tp.eta_ref.im);
        // Near zeta zeros, |eta| ~ 1e-7, so relative error is dominated
        // by FP64 absolute precision (~1e-15). Use absolute check instead.
        if (mag < 1e-4) {
            double err = std::sqrt((result.re - tp.eta_ref.re) * (result.re - tp.eta_ref.re)
                                 + (result.im - tp.eta_ref.im) * (result.im - tp.eta_ref.im));
            if (err < 1e-13) {
                std::printf("PASS %s: abs_err=%.2e (near-zero point)\n", label, err);
            } else {
                std::printf("FAIL %s: abs_err=%.2e (near-zero, limit 1e-13)\n", label, err);
                ++failures;
            }
        } else {
            check_complex(label, result, tp.eta_ref, 10);
        }
    }

    // Partial eta convergence toward Borwein reference
    std::printf("\n--- partial_eta convergence (N=10000, beta=0.5, t=50) ---\n");
    {
        constexpr int N = 10000;
        std::vector<double> weights(N), log_n(N);
        precompute_weights(0.5, N, weights.data());
        precompute_log_n(N, log_n.data());

        Complex eta_N = partial_eta(0.5, 50.0, N, weights.data(), log_n.data());
        Complex eta_ref = ETA_TEST_POINTS[2].eta_ref; // s = 0.5 + 50i
        double mag_ref = std::sqrt(eta_ref.re * eta_ref.re + eta_ref.im * eta_ref.im);
        double err = std::sqrt((eta_N.re - eta_ref.re) * (eta_N.re - eta_ref.re)
                             + (eta_N.im - eta_ref.im) * (eta_N.im - eta_ref.im));
        double rel = err / mag_ref;
        // O(N^{-beta}) = O(10000^{-0.5}) = O(0.01), so ~2 digits
        std::printf("partial_eta(N=%d): rel=%.2e (expected ~1e-2)\n", N, rel);
        if (rel > 0.1) {
            std::printf("FAIL: relative error too large\n");
            ++failures;
        } else {
            std::printf("PASS: within expected convergence rate\n");
        }
    }

    // Partial dirichlet vs partial eta consistency
    std::printf("\n--- partial_dirichlet vs partial_eta consistency ---\n");
    {
        constexpr int N = 1000;
        std::vector<double> weights(N), log_n(N);
        precompute_weights(0.5, N, weights.data());
        precompute_log_n(N, log_n.data());

        Complex S = partial_dirichlet(0.5, 50.0, N, weights.data(), log_n.data());
        Complex eta = partial_eta(0.5, 50.0, N, weights.data(), log_n.data());

        // eta_N should NOT equal S_N (different series). Just check they are
        // different and both finite.
        if (std::isfinite(S.re) && std::isfinite(S.im)
            && std::isfinite(eta.re) && std::isfinite(eta.im)
            && (std::abs(S.re - eta.re) > 1e-15 || std::abs(S.im - eta.im) > 1e-15)) {
            std::printf("PASS: S_N and eta_N are distinct and finite\n");
        } else {
            std::printf("FAIL: S_N and eta_N should be distinct\n");
            ++failures;
        }
    }

    std::printf("\n=== %d failures ===\n", failures);
    return failures > 0 ? 1 : 0;
}
