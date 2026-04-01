#ifndef EULER_MACLAURIN_H
#define EULER_MACLAURIN_H

#include "dirichlet.h"

/**
 * Bernoulli numbers B_{2k} for k = 1..5.
 * BERNOULLI[k-1] = B_{2k}.
 */
extern const double BERNOULLI[5];

/**
 * Euler-Maclaurin tail correction for sum_{n=N+1}^{inf} n^{-s}.
 * Paper Eq. 3.9, error bound Eq. 3.10.
 *
 * Applied to the NON-ALTERNATING series S_N(s).
 * To get eta(s) from EM-corrected zeta:
 *   zeta(s) ~ S_N(s) + em_tail(s, N, p)
 *   eta(s)  = (1 - 2^{1-s}) * zeta(s)   (Paper Eq. 2.16)
 *
 * Error: O(|s|^{2p-1} * N^{-(beta+2p-1)}) (Paper Eq. 3.10).
 * Computes N^{-s} internally from beta, t, N.
 *
 * @param beta real part of s
 * @param t imaginary part of s
 * @param N truncation point
 * @param p number of correction terms (1..5)
 * @return the tail correction value
 */
Complex em_tail_correction(double beta, double t, int N, int p);

#endif // EULER_MACLAURIN_H
