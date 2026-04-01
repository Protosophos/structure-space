#include "zeros.h"
#include "system2.h"
#include "theta.h"
#include "config.h"
#include <cmath>
#include <algorithm>

/**
 * Refines a sign change of Z_H(t) by bisection.
 *
 * @param t_lo left bracket where Z_H has one sign
 * @param t_hi right bracket where Z_H has opposite sign
 * @param z_lo Z_H(t_lo)
 * @return refined zero info
 */
static ZeroInfo refine_bisection(double t_lo, double t_hi, double z_lo) {
    double t_mid = 0.0;
    double z_mid = 0.0;
    int iters = 0;

    for (int i = 0; i < BISECTION_ITERS; ++i) {
        t_mid = 0.5 * (t_lo + t_hi);
        z_mid = hardy_Z(t_mid);
        ++iters;

        if ((z_lo > 0.0 && z_mid > 0.0) || (z_lo < 0.0 && z_mid < 0.0)) {
            t_lo = t_mid;
            z_lo = z_mid;
        } else {
            t_hi = t_mid;
        }
    }

    return {0.5 * (t_lo + t_hi), std::abs(z_mid), iters, true};
}

std::vector<ZeroInfo> detect_zeros(double t_start, double t_end) {
    std::vector<ZeroInfo> zeros;

    // Paper Section 3.3.2: Use Gram points as initial grid.
    // Gram points g_n are defined by theta(g_n) = n*pi.
    // Between consecutive Gram points, Gram's law ((-1)^n * Z_H(g_n) > 0)
    // holds ~73% of the time (Trudgian [17]). Where it fails, subdivide.

    // Find first Gram index: smallest n such that g_n >= t_start
    // Start search at n=-2 to cover early Gram points (g_{-1} ~ 3.44)
    int n_start = -2;
    while (gram_point(n_start) < t_start) ++n_start;

    // Find last Gram index: largest n such that g_n <= t_end
    int n_end = n_start;
    while (gram_point(n_end + 1) <= t_end) ++n_end;

    // Scan consecutive Gram intervals [g_n, g_{n+1}]
    for (int n = n_start; n < n_end; ++n) {
        double g_lo = gram_point(n);
        double g_hi = gram_point(n + 1);

        double z_lo = hardy_Z(g_lo);

        // Always subdivide to find ALL zeros in each Gram interval.
        // A single Gram interval can contain 0, 1, or 2+ zeros.
        // Paper Section 3.3.2: subdivide where Gram's law fails,
        // but we subdivide all intervals to catch closely-spaced pairs.
        {
            int num_sub = 100;
            double dt = (g_hi - g_lo) / static_cast<double>(num_sub);
            double prev_t = g_lo;
            double prev_z = z_lo;
            for (int sub = 1; sub <= num_sub; ++sub) {
                double sub_t = g_lo + sub * dt;
                double sub_z = hardy_Z(sub_t);
                if ((prev_z > 0.0 && sub_z < 0.0) || (prev_z < 0.0 && sub_z > 0.0)) {
                    ZeroInfo zi = refine_bisection(prev_t, sub_t, prev_z);
                    zeros.push_back(zi);
                }
                prev_t = sub_t;
                prev_z = sub_z;
            }
        }

        // Local minimum detection (even-multiplicity zeros, Paper Remark 2.6)
        // Check |L| via System 1 (Borwein eta) at the Gram point
        {
            Complex eta_val = borwein_eta(0.5, g_hi);
            double abs_eta = eta_val.abs();
            if (abs_eta < EPSILON_COARSE) {
                bool already_found = false;
                if (!zeros.empty()) {
                    if (std::abs(zeros.back().t - g_hi) < 0.1) already_found = true;
                }
                if (!already_found) {
                    zeros.push_back({g_hi, abs_eta, 0, false});
                }
            }
        }
    }

    return zeros;
}

double gram_point(int n) {
    // theta(g_n) = n*pi  (Paper Section 3.3.2)
    // Initial guess from asymptotic inversion of theta(t) ~ (t/2)*ln(t/(2*pi))
    double target = static_cast<double>(n) * PI;
    double g;
    if (n < -1) {
        g = 3.0; // g_{-2} and below: very small t
    } else if (n == -1) {
        g = 3.5;  // g_{-1} ~ 3.44 (theta ~ -pi)
    } else if (n == 0) {
        g = 17.8; // g_0 ~ 17.85 (theta = 0)
    } else if (n == 1) {
        g = 23.2; // g_1 ~ 23.17 (theta = pi)
    } else if (n <= 10) {
        g = 17.8 + static_cast<double>(n) * 4.0;
    } else {
        g = TWO_PI * static_cast<double>(n) / std::log(static_cast<double>(n));
    }
    if (g < 1.0) g = 1.0;

    // Newton iteration: theta(g) = target, theta'(g) ~ (1/2)*log(g/(2*pi))
    for (int i = 0; i < 50; ++i) {
        double th = theta(g);
        double err = th - target;
        if (std::abs(err) < 1e-14) break;
        double deriv = 0.5 * std::log(g / TWO_PI); // theta'(t) ~ (1/2)*ln(t/(2*pi))
        if (std::abs(deriv) < 1e-30) break;
        g -= err / deriv;
    }

    return g;
}

int predicted_zero_count(double T) {
    // Paper Eq. 3.28 (smooth part, without S(T))
    // N_smooth(T) = (T/(2*pi))*log(T/(2*pi)) - T/(2*pi) + 7/8
    double tau = T / TWO_PI;
    double count = tau * std::log(tau) - tau + 7.0 / 8.0;
    return static_cast<int>(std::round(count));
}

bool turing_check(int detected, double T) {
    int predicted = predicted_zero_count(T);
    return std::abs(detected - predicted) <= 1;
}
