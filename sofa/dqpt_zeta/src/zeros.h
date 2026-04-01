#ifndef ZEROS_H
#define ZEROS_H

#include <vector>

/**
 * Zero detection algorithms.
 * Paper Section 3.3.
 */

struct ZeroInfo {
    double t;                ///< Zero position (imaginary part)
    double residual;         ///< |L| or |G| at the zero
    int bisect_iters;        ///< Number of bisection iterations used
    bool from_sign_change;   ///< true = sign change, false = local minimum
};

/**
 * Detects zeros of Z_H(t) by sign changes and local minima.
 * Paper Section 3.3.1, Remark 2.6.
 *
 * Grid spacing is adaptive: dt(T) = [2*pi / log(T/(2*pi))] / 4.
 * Where Gram's law fails (same sign at consecutive Gram points),
 * finer subdivision is applied (Paper Section 3.3.2).
 *
 * @param t_start start of scan range
 * @param t_end end of scan range
 * @return detected zeros sorted by t
 */
std::vector<ZeroInfo> detect_zeros(double t_start, double t_end);

/**
 * Computes the n-th Gram point g_n where theta(g_n) = n*pi.
 * Paper Section 3.3.2.
 *
 * @param n Gram index (n >= 0)
 * @return g_n
 */
double gram_point(int n);

/**
 * Predicted zero count using smooth Riemann-von Mangoldt approximation.
 * Paper Eq. 3.28 (without S(T) term).
 *
 * N_smooth(T) = (T/(2*pi))*log(T/(2*pi)) - T/(2*pi) + 7/8
 *
 * @param T the height
 * @return predicted number of zeros with 0 < Im(s) <= T
 */
int predicted_zero_count(double T);

/**
 * V3 criterion: |N_detected - N_predicted| <= 1.
 * Paper Eq. 10.3.
 *
 * @param detected number of detected zeros
 * @param T the scan height
 * @return true if count matches within tolerance
 */
bool turing_check(int detected, double T);

#endif // ZEROS_H
