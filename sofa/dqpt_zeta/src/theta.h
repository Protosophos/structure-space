#ifndef THETA_H
#define THETA_H

/**
 * Riemann-Siegel theta function (Paper Eq. 3.16):
 * theta(t) = Im(log Gamma(1/4 + it/2)) - (t/2)*log(pi)
 *
 * For t >= 10: Stirling asymptotic expansion with 4 correction terms
 * through t^{-7}. Error < 10^{-12}. (Paper Eq. 3.17-3.19, Edwards [10].)
 *
 * For t < 10: Lanczos approximation for Gamma, then Im(log(...)).
 * (Paper Remark 3.3.)
 *
 * @param t the height parameter (t > 0)
 * @return theta(t)
 */
double theta(double t);

#endif // THETA_H
