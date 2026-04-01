#ifndef DIRICHLET_H
#define DIRICHLET_H

/**
 * Partial Dirichlet series and eta function computation.
 * Paper Section 3.1.
 */

struct Complex {
    double re = 0.0;
    double im = 0.0;

    double abs() const;
};

/**
 * Reduces t * ln(n) modulo 2*pi for precision at large t.
 * Paper Section 3.2.1, implementation notes.
 *
 * @param t the time parameter
 * @param ln_n precomputed ln(n)
 * @return the reduced argument in [0, 2*pi)
 */
double reduce_arg(double t, double ln_n);

/**
 * Precomputes n^{-beta} for n = 1..N.
 *
 * @param beta the inverse temperature
 * @param N truncation parameter
 * @param weights output array of size N
 */
void precompute_weights(double beta, int N, double* weights);

/**
 * Precomputes ln(n) for n = 1..N.
 *
 * @param N truncation parameter
 * @param log_n output array of size N
 */
void precompute_log_n(int N, double* log_n);

/**
 * Partial Dirichlet series S_N(s) = sum_{n=1}^{N} n^{-s}.
 * Paper Eq. 3.1, decomposition Eq. 3.2-3.4.
 *
 * @param beta real part of s
 * @param t imaginary part of s
 * @param N truncation parameter
 * @param weights precomputed n^{-beta}, size N
 * @param log_n precomputed ln(n), size N
 * @return S_N(beta + it)
 */
Complex partial_dirichlet(double beta, double t, int N,
                          const double* weights, const double* log_n);

/**
 * Alternating partial sum eta_N(s) = sum_{n=1}^{N} (-1)^{n+1} n^{-s}.
 * Paper Eq. 3.5. This is the numerator of L_N (Paper Eq. 2.22).
 *
 * @param beta real part of s
 * @param t imaginary part of s
 * @param N truncation parameter
 * @param weights precomputed n^{-beta}, size N
 * @param log_n precomputed ln(n), size N
 * @return eta_N(beta + it)
 */
Complex partial_eta(double beta, double t, int N,
                    const double* weights, const double* log_n);

/**
 * Borwein-accelerated eta(s).
 * Paper Eq. 3.11-3.15, Borwein [12].
 * Error: |error| < 3 * (3+sqrt(8))^{-40} ~ 10^{-30}.
 * Valid for Re(s) > 0.
 *
 * @param beta real part of s
 * @param t imaginary part of s
 * @return eta(beta + it) to ~30 digits
 */
Complex borwein_eta(double beta, double t);

#endif // DIRICHLET_H
