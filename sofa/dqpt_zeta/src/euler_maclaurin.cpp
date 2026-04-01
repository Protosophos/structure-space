#include "euler_maclaurin.h"
#include "config.h"
#include <cmath>

const double BERNOULLI[5] = {
    1.0 / 6.0,       // B_2
    -1.0 / 30.0,     // B_4
    1.0 / 42.0,      // B_6
    -1.0 / 30.0,     // B_8
    5.0 / 66.0       // B_10
};

/**
 * Computes N^{-s} = exp(-s * ln(N)) = N^{-beta} * exp(-i*t*ln(N)).
 */
static Complex complex_pow_neg(double beta, double t, double N_val) {
    double ln_N = std::log(N_val);
    double mag = std::pow(N_val, -beta);
    double arg = -t * ln_N;
    return {mag * std::cos(arg), mag * std::sin(arg)};
}

/**
 * Multiplies two complex numbers.
 */
static Complex cmul(Complex a, Complex b) {
    return {a.re * b.re - a.im * b.im,
            a.re * b.im + a.im * b.re};
}

/**
 * Computes the rising factorial s(s+1)(s+2)...(s+m-1) as a complex number.
 */
static Complex rising_factorial(double beta, double t, int m) {
    Complex result = {1.0, 0.0};
    for (int j = 0; j < m; ++j) {
        Complex factor = {beta + static_cast<double>(j), t};
        result = cmul(result, factor);
    }
    return result;
}

Complex em_tail_correction(double beta, double t, int N, int p) {
    double N_d = static_cast<double>(N);

    // Integral term: integral_N^inf x^{-s} dx = N^{1-s} / (s-1)
    // (s-1) = (beta-1) + it
    Complex N_pow_1ms = complex_pow_neg(beta - 1.0, t, N_d); // N^{-(s-1)} = N^{1-s}
    double denom_re = beta - 1.0;
    double denom_im = t;
    double denom_sq = denom_re * denom_re + denom_im * denom_im;
    Complex integral = {
        (N_pow_1ms.re * denom_re + N_pow_1ms.im * denom_im) / denom_sq,
        (N_pow_1ms.im * denom_re - N_pow_1ms.re * denom_im) / denom_sq
    };

    // Boundary term: (1/2) * N^{-s}
    Complex N_pow_ms = complex_pow_neg(beta, t, N_d);
    Complex boundary = {0.5 * N_pow_ms.re, 0.5 * N_pow_ms.im};

    // Bernoulli correction terms:
    // sum_{k=1}^{p} B_{2k}/(2k)! * s(s+1)...(s+2k-2) * N^{-(s+2k-1)}
    Complex correction = {0.0, 0.0};
    for (int k = 1; k <= p && k <= 5; ++k) {
        int m = 2 * k - 1; // number of rising factorial terms: s(s+1)...(s+2k-2) has 2k-1 factors

        // Compute (2k)!
        double factorial_2k = 1.0;
        for (int j = 1; j <= 2 * k; ++j) {
            factorial_2k *= static_cast<double>(j);
        }

        double coeff = BERNOULLI[k - 1] / factorial_2k;
        Complex rf = rising_factorial(beta, t, m);
        Complex N_pow = complex_pow_neg(beta + static_cast<double>(m), t, N_d);

        // term = coeff * rf * N^{-(s+2k-1)}
        Complex term = cmul(rf, N_pow);
        correction.re += coeff * term.re;
        correction.im += coeff * term.im;
    }

    return {integral.re + boundary.re + correction.re,
            integral.im + boundary.im + correction.im};
}
