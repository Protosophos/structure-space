#ifndef SYSTEM2_H
#define SYSTEM2_H

#include "dirichlet.h"

/**
 * System 2: Generalized Loschmidt amplitude G_N(beta, t).
 * Paper Section 2.2.2, Eq. 2.30-2.32.
 *
 * G = (1/Z_N) * Re[e^{i*theta(t)} * S_N(beta+it)]
 *
 * CRITICAL: Reliable for zero detection ONLY at beta = 1/2.
 * For beta != 1/2, phase-alignment false positives occur (Paper Remark 2.7).
 */

/**
 * Computes G_N(beta, t) from direct partial sum S_N.
 * Works for any beta, but only meaningful for zero detection at beta = 1/2.
 *
 * @param beta inverse temperature
 * @param t time parameter
 * @param N truncation parameter
 * @param weights precomputed n^{-beta}, size N
 * @param log_n precomputed ln(n), size N
 * @return G_N(beta, t) (real-valued)
 */
double system2_G(double beta, double t, int N,
                 const double* weights, const double* log_n);

/**
 * Hardy Z-function (beta = 1/2 ONLY). Paper Section 3.2.1.
 *
 * For t < 200: direct computation via Borwein-accelerated zeta.
 *   Z_H(t) = Re[e^{i*theta(t)} * zeta(1/2+it)]
 *   where zeta = eta(s) / (1 - 2^{1-s}).
 *
 * For t >= 200: Riemann-Siegel formula (Paper Eq. 3.20).
 *   Z_H(t) = 2 * sum_{n=1}^{M} n^{-1/2} * cos(theta(t) - t*ln(n)) + R_0(t)
 *   M = floor(sqrt(t/(2*pi))).
 *
 * @param t the height parameter (t > 0)
 * @return Z_H(t)
 */
double hardy_Z(double t);

/**
 * Riemann-Siegel remainder R_0. Paper Eq. 3.21-3.22.
 *
 * R_0(t) = (-1)^{M-1} * (t/(2*pi))^{-1/4} * Psi(p)
 * Psi(p) = cos(2*pi*(p^2 - p - 1/16)) / cos(2*pi*p)
 * p = sqrt(t/(2*pi)) - M
 *
 * @param t the height parameter
 * @param M the main sum cutoff floor(sqrt(t/(2*pi)))
 * @return R_0(t)
 */
double rs_remainder_R0(double t, int M);

#endif // SYSTEM2_H
