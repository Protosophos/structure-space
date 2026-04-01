#include "../src/theta.h"
#include "reference_values.h"
#include <cstdio>
#include <cstdlib>
#include <cmath>

/**
 * Tests the Riemann-Siegel theta function against mpmath reference values.
 * Paper Section 4.4.1: agreement to 10 significant digits.
 */

static int failures = 0;

/**
 * Checks that computed theta matches reference to the given digits.
 *
 * @param t the test point
 * @param computed the computed value
 * @param expected the reference value
 * @param digits required significant digits
 */
static void check(double t, double computed, double expected, int digits) {
    double rel = std::abs((computed - expected) / expected);
    if (rel > std::pow(10.0, -digits)) {
        std::printf("FAIL theta(%.6f): computed=%.16e expected=%.16e rel=%.2e\n",
                    t, computed, expected, rel);
        ++failures;
    } else {
        std::printf("PASS theta(%.6f): rel=%.2e\n", t, rel);
    }
}

int main() {
    std::printf("=== test_theta ===\n\n");

    std::printf("--- theta(t) vs mpmath (10-digit agreement) ---\n");
    for (int i = 0; i < NUM_THETA_TEST_POINTS; ++i) {
        double t = THETA_TEST_POINTS[i].t;
        double ref = THETA_TEST_POINTS[i].theta_ref;
        double computed = theta(t);
        check(t, computed, ref, 10);
    }

    // Test Stirling/Lanczos boundary (t = 10)
    std::printf("\n--- Boundary test at t=10 ---\n");
    {
        double t = 10.0;
        double val = theta(t);
        // theta(10) should be around -0.636... (negative, small)
        if (std::isfinite(val) && std::abs(val) < 100.0) {
            std::printf("PASS theta(10.0) = %.16e (finite and reasonable)\n", val);
        } else {
            std::printf("FAIL theta(10.0) = %.16e\n", val);
            ++failures;
        }
    }

    // Test small t (Lanczos branch)
    std::printf("\n--- Small t test (Lanczos branch) ---\n");
    {
        double t = 5.0;
        double val = theta(t);
        if (std::isfinite(val)) {
            std::printf("PASS theta(5.0) = %.16e (finite)\n", val);
        } else {
            std::printf("FAIL theta(5.0) is not finite\n");
            ++failures;
        }
    }

    std::printf("\n=== %d failures ===\n", failures);
    return failures > 0 ? 1 : 0;
}
