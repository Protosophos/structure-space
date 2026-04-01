#include "../src/dirichlet.h"
#include "../src/config.h"
#include <cstdio>
#include <cmath>

/**
 * R1: min_t |L(beta,t)| > 1e-3 for all off-line beta.
 * Paper Section 10.2.
 * Uses Borwein-accelerated eta (System 1 only, Paper Remark 2.7).
 *
 * Scans beta in [0.1, 0.9] with step 0.005, excluding [0.4975, 0.5025].
 * Scans t in [1, 100] with step 0.5 (reduced range for test speed).
 */

static int failures = 0;

int main() {
    std::printf("=== test_off_line (R1) ===\n\n");
    std::printf("Scanning beta in [%.2f, %.2f], step %.3f, t in [1, 100]\n\n",
                BETA_MIN, BETA_MAX, DBETA_COARSE);

    int flagged = 0;

    for (double beta = BETA_MIN; beta <= BETA_MAX; beta += DBETA_COARSE) {
        if (beta >= BETA_EXCL_LO && beta <= BETA_EXCL_HI) continue;

        double min_abs = 1e30;
        for (double t = 1.0; t <= 100.0; t += 0.5) {
            Complex eta = borwein_eta(beta, t);
            double a = eta.abs();
            if (a < min_abs) min_abs = a;
        }

        if (min_abs < EPSILON_FLAG) {
            std::printf("FLAGGED beta=%.4f: min|eta|=%.6e\n", beta, min_abs);
            ++flagged;
        }
    }

    if (flagged == 0) {
        std::printf("PASS R1: no off-line zeros detected\n");
    } else {
        std::printf("FAIL R1: %d candidates flagged\n", flagged);
        ++failures;
    }

    std::printf("\n=== %d failures ===\n", failures);
    return failures > 0 ? 1 : 0;
}
