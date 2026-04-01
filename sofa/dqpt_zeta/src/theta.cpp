#include "theta.h"
#include "config.h"
#include <cmath>

/**
 * Lanczos approximation for log|Gamma(z)| and arg(Gamma(z)).
 * Used for t < 10 where Stirling is not precise enough.
 * Returns {log|Gamma(z)|, arg(Gamma(z))}.
 */
static void log_gamma_complex(double re_z, double im_z,
                              double* out_log_abs, double* out_arg) {
    // Lanczos coefficients (g=7, n=9) from Numerical Recipes
    static const double coeff[] = {
        0.99999999999980993,
        676.5203681218851,
        -1259.1392167224028,
        771.32342877765313,
        -176.61502916214059,
        12.507343278686905,
        -0.13857109526572012,
        9.9843695780195716e-6,
        1.5056327351493116e-7
    };
    constexpr double g = 7.0;
    constexpr double log_sqrt_2pi = 0.9189385332046727;

    // Shift z by -1 for Lanczos (Gamma(z) = Gamma(z-1+1))
    double x_re = re_z - 1.0;
    double x_im = im_z;

    // Compute the Lanczos sum: sum_{k=0}^{8} coeff[k] / (z - 1 + k)
    double sum_re = coeff[0];
    double sum_im = 0.0;
    for (int k = 1; k <= 8; ++k) {
        double denom_re = x_re + static_cast<double>(k);
        double denom_im = x_im;
        double denom_sq = denom_re * denom_re + denom_im * denom_im;
        sum_re += coeff[k] * denom_re / denom_sq;
        sum_im -= coeff[k] * denom_im / denom_sq;
    }

    // t = z - 1 + g + 0.5
    double t_re = x_re + g + 0.5;
    double t_im = x_im;

    // log(Gamma(z)) = log(sqrt(2*pi)) + log(sum)
    //               + (z - 0.5) * log(t) - t
    double log_sum = 0.5 * std::log(sum_re * sum_re + sum_im * sum_im);
    double arg_sum = std::atan2(sum_im, sum_re);

    double log_t = 0.5 * std::log(t_re * t_re + t_im * t_im);
    double arg_t = std::atan2(t_im, t_re);

    // (z - 0.5) * log(t):
    // real part: (re_z - 0.5) * log_t - im_z * arg_t
    // imag part: im_z * log_t + (re_z - 0.5) * arg_t
    double zh_re = re_z - 0.5;
    double prod_re = zh_re * log_t - im_z * arg_t;
    double prod_im = im_z * log_t + zh_re * arg_t;

    // Full result:
    // log(Gamma(z)) = log_sqrt_2pi + log_sum + i*arg_sum + prod - t
    *out_log_abs = log_sqrt_2pi + log_sum + prod_re - t_re;
    *out_arg = arg_sum + prod_im - t_im;
}

double theta(double t) {
    if (t >= 10.0) {
        // Stirling asymptotic expansion (Paper Eq. 3.18)
        // theta(t) = (t/2)*ln(t/(2*pi)) - t/2 - pi/8
        //          + 1/(48*t) + 7/(5760*t^3)
        //          + 31/(80640*t^5) - 127/(430080*t^7)
        double t2 = t * t;
        double t3 = t2 * t;
        double t5 = t3 * t2;
        double t7 = t5 * t2;

        return (t / 2.0) * std::log(t / (2.0 * PI))
               - t / 2.0
               - PI / 8.0
               + 1.0 / (48.0 * t)
               + 7.0 / (5760.0 * t3)
               + 31.0 / (80640.0 * t5)
               - 127.0 / (430080.0 * t7);
    }

    // For t < 10: Lanczos approximation (Paper Remark 3.3)
    // theta(t) = Im(log Gamma(1/4 + it/2)) - (t/2)*log(pi)
    double log_abs, arg_gamma;
    log_gamma_complex(0.25, t / 2.0, &log_abs, &arg_gamma);
    return arg_gamma - (t / 2.0) * std::log(PI);
}
