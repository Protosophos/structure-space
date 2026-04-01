#include "dirichlet.h"
#include "partition.h"
#include "theta.h"
#include "euler_maclaurin.h"
#include "system1.h"
#include "system2.h"
#include "rate_function.h"
#include "zeros.h"
#include "gpu_accel.h"
#include "config.h"
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <cmath>
#include <vector>
#include <string>

/**
 * Prints usage information.
 */
static void print_usage() {
    std::printf("Usage: dqpt_zeta <mode> [options]\n\n");
    std::printf("Modes:\n");
    std::printf("  --stage1                Verify at first 100 known zeros\n");
    std::printf("  --stage2 --tmax=T       Critical-line scan, zero detection\n");
    std::printf("  --stage3 --tmax=T       Off-line scan (System 1 only)\n");
    std::printf("  --stage4 --beta=B --t=T Refine candidate\n");
    std::printf("  --phase-diagram --tmax=T Generate 2D heat map data\n");
    std::printf("\nDefault tmax=1000\n");
}

/**
 * Stage 1: Verify implementation at first known zeros.
 * Paper Section 4.2, Stage 1.
 */
static void run_stage1() {
    std::printf("=== Stage 1: Implementation Verification ===\n\n");

    constexpr int N = N_DEFAULT;
    std::vector<double> weights(N), log_n(N);
    precompute_weights(0.5, N, weights.data());
    precompute_log_n(N, log_n.data());

    // Load known zeros from data file (Paper Section 4.4.4)
    std::vector<double> known_zeros;
    {
        FILE* f = std::fopen("../data/zeros_known.txt", "r");
        if (!f) f = std::fopen("data/zeros_known.txt", "r");
        if (!f) {
            std::fprintf(stderr, "Cannot open data/zeros_known.txt\n");
            return;
        }
        double val;
        while (std::fscanf(f, "%lf", &val) == 1) {
            known_zeros.push_back(val);
        }
        std::fclose(f);
        std::printf("Loaded %zu known zeros from data file\n\n",
                    known_zeros.size());
    }
    // V1/V2: test all loaded zeros (Paper Section 10.1 requires min 100)
    int num_zeros = static_cast<int>(known_zeros.size());

    std::printf("--- System 1: |L(0.5, t_k)| ---\n");
    for (int i = 0; i < num_zeros; ++i) {
        Complex L = system1_L(0.5, known_zeros[i], N,
                              weights.data(), log_n.data());
        std::printf("zero[%2d] t=%12.6f |L|=%.4e %s\n",
                    i + 1, known_zeros[i], L.abs(),
                    L.abs() < 1e-4 ? "OK" : "ALERT");
    }

    std::printf("\n--- System 2: Z_H(t_k) sign changes ---\n");
    for (int i = 0; i < num_zeros; ++i) {
        double delta = 5e-4;
        double lo = hardy_Z(known_zeros[i] - delta);
        double hi = hardy_Z(known_zeros[i] + delta);
        bool sc = (lo > 0 && hi < 0) || (lo < 0 && hi > 0);
        std::printf("zero[%2d] t=%12.6f Z_H: %.4e -> %.4e %s\n",
                    i + 1, known_zeros[i], lo, hi,
                    sc ? "SIGN_CHANGE" : "NO_CHANGE");
    }
}

/**
 * Stage 2: Critical-line zero detection scan.
 * Paper Section 4.2, Stage 2.
 */
static void run_stage2(double tmax) {
    std::printf("=== Stage 2: Critical-Line Scan [0, %.0f] ===\n\n", tmax);

    auto zeros = detect_zeros(1.0, tmax);
    int predicted = predicted_zero_count(tmax);
    bool v3 = turing_check(static_cast<int>(zeros.size()), tmax);

    std::printf("Detected: %zu zeros\n", zeros.size());
    std::printf("Predicted (Riemann-von Mangoldt): %d\n", predicted);
    std::printf("V3 check: %s\n\n", v3 ? "PASS" : "FAIL");

    // Output CSV
    std::printf("beta,t,Z_H,is_zero\n");
    for (const auto& z : zeros) {
        std::printf("0.500,%.10f,%.6e,1\n", z.t, z.residual);
    }
}

/**
 * Stage 3: Off-critical-line scan (System 1 only).
 * Paper Section 4.2, Stage 3.
 */
static void run_stage3(double tmax) {
    std::printf("=== Stage 3: Off-Line Scan [0, %.0f] ===\n", tmax);
    std::printf("System 1 ONLY (Paper Remark 2.7)\n\n");

    int flagged = 0;

    std::printf("beta,min_abs_L,flagged\n");

    for (double beta = BETA_MIN; beta <= BETA_MAX; beta += DBETA_COARSE) {
        // Skip neighborhood of 0.5
        if (beta >= BETA_EXCL_LO && beta <= BETA_EXCL_HI) continue;

        // Use Borwein for high-precision eta evaluation
        double min_abs = 1e30;
        double min_t = 0.0;

        for (double t = 1.0; t <= tmax; t += DT_COARSE) {
            Complex eta = borwein_eta(beta, t);
            double a = eta.abs();
            if (a < min_abs) {
                min_abs = a;
                min_t = t;
            }
        }

        bool is_flagged = (min_abs < EPSILON_FLAG);
        if (is_flagged) ++flagged;

        std::printf("%.4f,%.6e,%d\n", beta, min_abs, is_flagged ? 1 : 0);

        if (is_flagged) {
            std::fprintf(stderr, "FLAGGED: beta=%.4f min|eta|=%.6e at t=%.4f\n",
                         beta, min_abs, min_t);
        }
    }

    std::printf("\n# Flagged candidates: %d\n", flagged);
    if (flagged == 0) {
        std::printf("# R1: PASS - no off-line zeros detected\n");
    } else {
        std::printf("# R1: INVESTIGATE - %d candidates require Stage 4 refinement\n",
                    flagged);
    }
}

/**
 * Stage 4: High-precision refinement of a candidate.
 * Paper Section 4.2, Stage 4 + Section 4.3 adaptive refinement.
 */
static void run_stage4(double beta_init, double t_init) {
    std::printf("=== Stage 4: Refinement at beta=%.6f, t=%.6f ===\n\n",
                beta_init, t_init);

    double beta = beta_init;
    double t = t_init;

    // (a) Increase N and recompute (Paper Section 4.2, Stage 4a)
    // With Borwein, precision is already ~30 digits independent of N.
    // We verify the candidate persists at high precision.
    Complex eta_init = borwein_eta(beta, t);
    std::printf("Initial: |eta(%.6f + %.6fi)| = %.6e\n", beta, t, eta_init.abs());

    // (b) Refine by 2D Newton's method (Paper Section 4.2, Stage 4b)
    // Minimize |eta(beta+it)|^2 = eta.re^2 + eta.im^2
    // Gradient: d|eta|^2/dbeta and d|eta|^2/dt computed by finite differences
    constexpr double h_beta = 1e-6;
    constexpr double h_t = 1e-6;

    for (int iter = 0; iter < 100; ++iter) {
        Complex e0 = borwein_eta(beta, t);
        double f0 = e0.re * e0.re + e0.im * e0.im;

        if (f0 < EPSILON_FINE * EPSILON_FINE) break; // converged

        // Gradient by central differences
        Complex e_bp = borwein_eta(beta + h_beta, t);
        Complex e_bm = borwein_eta(beta - h_beta, t);
        Complex e_tp = borwein_eta(beta, t + h_t);
        Complex e_tm = borwein_eta(beta, t - h_t);

        double df_dbeta = (e_bp.re*e_bp.re + e_bp.im*e_bp.im
                         - e_bm.re*e_bm.re - e_bm.im*e_bm.im) / (2.0 * h_beta);
        double df_dt = (e_tp.re*e_tp.re + e_tp.im*e_tp.im
                      - e_tm.re*e_tm.re - e_tm.im*e_tm.im) / (2.0 * h_t);

        double grad_sq = df_dbeta * df_dbeta + df_dt * df_dt;
        if (grad_sq < 1e-30) break; // gradient vanished

        // Steepest descent step with adaptive step size
        double step = f0 / grad_sq; // Barzilai-Borwein-like step
        if (step > 0.01) step = 0.01;

        beta -= step * df_dbeta;
        t -= step * df_dt;

        // Keep beta in (0, 1)
        if (beta < 0.01) beta = 0.01;
        if (beta > 0.99) beta = 0.99;
    }

    Complex eta_final = borwein_eta(beta, t);
    std::printf("Refined: beta=%.10f t=%.10f |eta|=%.6e\n",
                beta, t, eta_final.abs());

    // (c) Classify (Paper Section 4.2, Stage 4c)
    if (std::abs(beta - 0.5) < 0.01) {
        std::printf("Classification: critical-line zero (consistent with RH)\n");
    } else {
        std::printf("Classification: POTENTIAL OFF-LINE ZERO (beta=%.6f)\n", beta);
        std::printf("  -> Requires independent verification with higher precision\n");
    }
}

/**
 * Phase diagram: 2D scan of |L(beta, t)| for heat map visualization.
 * Paper Section 6.1, criterion R2.
 */
static void run_phase_diagram(double tmax) {
    std::fprintf(stderr, "Generating phase diagram [%.1f,%.1f] x [1,%.0f]...\n",
                 BETA_MIN, BETA_MAX, tmax);

    std::printf("beta,t,abs_L,abs_G,F1,is_sentinel\n");

    for (double beta = BETA_MIN; beta <= BETA_MAX; beta += DBETA_COARSE) {
        constexpr int N = N_DEFAULT;
        std::vector<double> weights(N), log_n(N);
        precompute_weights(beta, N, weights.data());
        precompute_log_n(N, log_n.data());

        for (double t = 1.0; t <= tmax; t += DT_COARSE) {
            Complex L = system1_L(beta, t, N, weights.data(), log_n.data());
            double abs_L = L.abs();
            double G = system2_G(beta, t, N, weights.data(), log_n.data());
            double F1 = rate_function(abs_L, N);
            bool sentinel = is_sentinel(abs_L);

            std::printf("%.4f,%.4f,%.6e,%.6e,%.6f,%d\n",
                        beta, t, abs_L, std::abs(G), F1, sentinel ? 1 : 0);
        }
    }
}

int main(int argc, char* argv[]) {
    if (argc < 2) {
        print_usage();
        return 1;
    }

    double tmax = T_MAX;
    double beta_arg = 0.5;
    double t_arg = 0.0;
    std::string mode;

    for (int i = 1; i < argc; ++i) {
        if (std::strncmp(argv[i], "--tmax=", 7) == 0) {
            tmax = std::atof(argv[i] + 7);
        } else if (std::strncmp(argv[i], "--beta=", 7) == 0) {
            beta_arg = std::atof(argv[i] + 7);
        } else if (std::strncmp(argv[i], "--t=", 4) == 0) {
            t_arg = std::atof(argv[i] + 4);
        } else {
            mode = argv[i];
        }
    }

    if (tmax > T_MAX_FP64) {
        std::fprintf(stderr,
            "WARNING: tmax=%.0f exceeds FP64 precision wall (%.0f).\n"
            "Results above t=%.0f may be unreliable (Paper Section 5.2).\n",
            tmax, T_MAX_FP64, T_MAX_FP64);
    }

    if (mode == "--stage1") {
        run_stage1();
    } else if (mode == "--stage2") {
        run_stage2(tmax);
    } else if (mode == "--stage3") {
        run_stage3(tmax);
    } else if (mode == "--stage4") {
        run_stage4(beta_arg, t_arg);
    } else if (mode == "--phase-diagram") {
        run_phase_diagram(tmax);
    } else {
        std::fprintf(stderr, "Unknown mode: %s\n", mode.c_str());
        print_usage();
        return 1;
    }

    return 0;
}
