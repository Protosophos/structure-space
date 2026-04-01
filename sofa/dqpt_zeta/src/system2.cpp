#include "system2.h"
#include "theta.h"
#include "partition.h"
#include "kahan.h"
#include "config.h"
#include <cmath>

double system2_G(double /*beta*/, double t, int N,
                 const double* weights, const double* log_n) {
    Complex S = partial_dirichlet(0.0, t, N, weights, log_n);
    double th = theta(t);
    double numerator = std::cos(th) * S.re - std::sin(th) * S.im;
    double Z = partition_function(0.0, N, weights);
    return numerator / Z;
}

double rs_remainder_R0(double t, int M) {
    double tau = t / TWO_PI;
    double p = std::sqrt(tau) - static_cast<double>(M);

    double psi_denom = std::cos(TWO_PI * p);
    if (std::abs(psi_denom) < 1e-15) {
        return 0.0;
    }

    double psi_arg = TWO_PI * (p * p - p - 1.0 / 16.0);
    double psi = std::cos(psi_arg) / psi_denom;
    double sign = ((M - 1) % 2 == 0) ? 1.0 : -1.0;
    return sign * std::pow(tau, -0.25) * psi;
}

/**
 * Computes Z_H(t) via Borwein-accelerated zeta for small t.
 * Z_H(t) = Re[e^{i*theta(t)} * zeta(1/2 + it)]
 * zeta(s) = eta(s) / (1 - 2^{1-s})
 */
static double hardy_Z_borwein(double t) {
    Complex eta = borwein_eta(0.5, t);

    // zeta(s) = eta(s) / (1 - 2^{1-s})
    // 2^{1-s} = 2^{0.5} * exp(-i*t*ln(2)) = sqrt(2) * (cos - i*sin)
    double mag2 = std::sqrt(2.0);
    double arg2 = -t * std::log(2.0);
    Complex two_1ms = {mag2 * std::cos(arg2), mag2 * std::sin(arg2)};
    Complex denom = {1.0 - two_1ms.re, -two_1ms.im};
    double dsq = denom.re * denom.re + denom.im * denom.im;
    Complex zeta_val = {
        (eta.re * denom.re + eta.im * denom.im) / dsq,
        (eta.im * denom.re - eta.re * denom.im) / dsq
    };

    // Z_H(t) = Re[e^{i*theta(t)} * zeta(1/2+it)]
    double th = theta(t);
    return std::cos(th) * zeta_val.re - std::sin(th) * zeta_val.im;
}

/**
 * Computes Z_H(t) via Riemann-Siegel formula for large t.
 * Paper Eq. 3.20, with R_0 correction.
 */
static double hardy_Z_riemann_siegel(double t) {
    double tau = t / TWO_PI;
    int M = static_cast<int>(std::sqrt(tau));
    if (M < 1) M = 1;

    double th = theta(t);

    KahanAccumulator sum;
    for (int n = 1; n <= M; ++n) {
        double ln_n = std::log(static_cast<double>(n));
        double arg = std::fmod(th - t * ln_n, TWO_PI);
        sum.add(std::pow(static_cast<double>(n), -0.5) * std::cos(arg));
    }

    return 2.0 * sum.result() + rs_remainder_R0(t, M);
}

double hardy_Z(double t) {
    if (t < T_RS_THRESHOLD) {
        return hardy_Z_borwein(t);
    }
    return hardy_Z_riemann_siegel(t);
}
