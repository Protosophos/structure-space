#include "dirichlet.h"
#include "kahan.h"
#include "config.h"
#include <cmath>
#include <vector>

double Complex::abs() const {
    return std::sqrt(re * re + im * im);
}

double reduce_arg(double t, double ln_n) {
    double arg = t * ln_n;
    arg = std::fmod(arg, TWO_PI);
    if (arg < 0.0) arg += TWO_PI;
    return arg;
}

void precompute_weights(double beta, int N, double* weights) {
    for (int n = 1; n <= N; ++n) {
        weights[n - 1] = std::pow(static_cast<double>(n), -beta);
    }
}

void precompute_log_n(int N, double* log_n) {
    for (int n = 1; n <= N; ++n) {
        log_n[n - 1] = std::log(static_cast<double>(n));
    }
}

Complex partial_dirichlet(double /*beta*/, double t, int N,
                          const double* weights, const double* log_n) {
    KahanAccumulator sum_re, sum_im;

    for (int n = 0; n < N; ++n) {
        double arg = reduce_arg(t, log_n[n]);
        sum_re.add(weights[n] * std::cos(arg));
        sum_im.add(-weights[n] * std::sin(arg));
    }

    return {sum_re.result(), sum_im.result()};
}

Complex partial_eta(double /*beta*/, double t, int N,
                    const double* weights, const double* log_n) {
    KahanAccumulator sum_re, sum_im;

    for (int n = 0; n < N; ++n) {
        double sign = ((n + 1) % 2 == 1) ? 1.0 : -1.0;
        double arg = reduce_arg(t, log_n[n]);
        sum_re.add(sign * weights[n] * std::cos(arg));
        sum_im.add(sign * (-weights[n]) * std::sin(arg));
    }

    return {sum_re.result(), sum_im.result()};
}

Complex borwein_eta(double beta, double t) {
    // Select n: max(BORWEIN_N_MIN, ceil(1.5*|t|) + 40)
    // n=40 suffices for small |t| (Paper Section 3.1).
    // Larger |t| needs more terms due to oscillation of (k+1)^{-it}.
    // Borwein is only used for t < T_RS_THRESHOLD (200), so max n ~ 340.
    int n = BORWEIN_N_MIN;
    int n_needed = static_cast<int>(std::abs(t) * 1.5) + 40;
    if (n_needed > n) n = n_needed;
    if (n > 1000) n = 1000; // FP64 limit for factorial ratios

    // Paper Eq. 3.12: factorial-based Borwein coefficients.
    // d_k = n * sum_{j=0}^{k} (n+j-1)! * 4^j / ((n-j)! * (2j)!)
    //
    // The j-th term of the inner sum is:
    //   a_j = n * (n+j-1)! * 4^j / ((n-j)! * (2j)!)
    //
    // a_0 = n * (n-1)! / (n! * 1) = 1
    //
    // Recurrence: a_j / a_{j-1} = 4 * (n+j-1) * (n-j+1) / ((2j) * (2j-1))
    // Derived from the ratio of consecutive factorial terms.
    //
    // At j=n: a_n = n * (2n-1)! * 4^n / (0! * (2n)!) = 4^n / (2n) * n = ...
    // The recurrence works up to j=n-1. At j=n, (n-j+1)=1, so no division
    // by zero; the recurrence is valid for all j = 1..n.

    std::vector<double> d(n + 1);

    double a = 1.0; // a_0 = 1
    d[0] = a;

    for (int j = 1; j <= n; ++j) {
        a *= 4.0 * static_cast<double>(n + j - 1) * static_cast<double>(n - j + 1)
             / (static_cast<double>(2 * j) * static_cast<double>(2 * j - 1));
        d[j] = d[j - 1] + a;
    }

    double d_n = d[n];

    // eta(s) = -(1/d_n) * sum_{k=0}^{n-1} (-1)^k * (d_k - d_n) * (k+1)^{-s}
    // Paper Eq. 3.11, stable form Eq. 3.13
    KahanAccumulator sum_re, sum_im;

    for (int k = 0; k < n; ++k) {
        double coeff = d[k] - d_n;
        double sign = (k % 2 == 0) ? 1.0 : -1.0;
        double w = std::pow(static_cast<double>(k + 1), -beta);
        double ln_k1 = std::log(static_cast<double>(k + 1));
        double arg = reduce_arg(t, ln_k1);

        sum_re.add(sign * coeff * w * std::cos(arg));
        sum_im.add(sign * coeff * (-w) * std::sin(arg));
    }

    double inv_dn = -1.0 / d_n;
    return {sum_re.result() * inv_dn, sum_im.result() * inv_dn};
}
