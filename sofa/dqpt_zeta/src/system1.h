#ifndef SYSTEM1_H
#define SYSTEM1_H

#include "dirichlet.h"

/**
 * System 1: Accumulated phase factor L_N(beta, t).
 * Paper Section 2.2.1, Eq. 2.22.
 *
 * L_N = [sum_{n=1}^{N} (-1)^{n+1} n^{-(beta+it)}] / [sum_{n=1}^{N} n^{-beta}]
 *
 * Used for zero detection at ALL beta values.
 */

/**
 * Computes L_N(beta, t) by direct summation with Kahan compensation.
 * Both numerator (eta_N) and denominator (Z_N) share precomputed weights.
 *
 * @param beta inverse temperature
 * @param t time parameter
 * @param N truncation parameter
 * @param weights precomputed n^{-beta}, size N
 * @param log_n precomputed ln(n), size N
 * @return L_N(beta + it)
 */
Complex system1_L(double beta, double t, int N,
                  const double* weights, const double* log_n);

/**
 * Computes L using Borwein-accelerated eta(s) / Z_N(beta).
 * Bypasses finite-N physics (Paper Remark 3.1).
 * Use for high-precision zero detection, not for finite-N scaling tests.
 * Precomputes weights internally.
 *
 * @param beta inverse temperature
 * @param t time parameter
 * @param N truncation for Z_N only
 * @return eta(s) / Z_N(beta)
 */
Complex system1_L_borwein(double beta, double t, int N);

/**
 * Computes L using Euler-Maclaurin corrected zeta.
 * eta(s) = (1 - 2^{1-s}) * [S_N(s) + em_tail(s,N,p)]
 * Then L = eta(s) / Z_N(beta).
 * Precomputes weights and log_n internally.
 *
 * @param beta inverse temperature
 * @param t time parameter
 * @param N truncation parameter
 * @param p number of EM correction terms
 * @return EM-corrected L
 */
Complex system1_L_em(double beta, double t, int N, int p);

#endif // SYSTEM1_H
