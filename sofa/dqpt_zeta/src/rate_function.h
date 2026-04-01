#ifndef RATE_FUNCTION_H
#define RATE_FUNCTION_H

/**
 * Rate function F_1(beta, t) for System 1.
 * Paper Eq. 2.23, underflow guard Eq. 5.5.
 *
 * F_1 = -(1/ln(N)) * ln(max(|L_N|, UNDERFLOW_GUARD))
 *
 * Expected values (Paper Eq. 2.34-2.36):
 *   Away from zeros: F_1 = 1 - beta
 *   At a zero with Re(s_0) = beta_0: F_1 = beta_0 + 1 - beta
 *   At beta = beta_0 = 1/2: F_1 = 1 at zeros, 1/2 away. Jump = 1/2.
 */

/**
 * Computes the rate function from |L_N|.
 *
 * @param abs_L magnitude of L_N = sqrt(L.re^2 + L.im^2)
 * @param N truncation parameter
 * @return F_1 value, or a large sentinel if underflow occurred
 */
double rate_function(double abs_L, int N);

/**
 * Returns true if |L_N| has underflowed to exact zero in FP64.
 * Such points must be recorded separately and excluded from rate
 * function analysis (Paper Section 5.3).
 *
 * @param abs_L magnitude of L_N
 * @return true if abs_L is a sentinel value
 */
bool is_sentinel(double abs_L);

#endif // RATE_FUNCTION_H
