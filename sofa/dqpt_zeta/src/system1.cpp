#include "system1.h"
#include "partition.h"
#include "euler_maclaurin.h"
#include "kahan.h"
#include "config.h"
#include <cmath>
#include <vector>

Complex system1_L(double /*beta*/, double t, int N,
                  const double* weights, const double* log_n) {
    Complex eta = partial_eta(0.0, t, N, weights, log_n);
    double Z = partition_function(0.0, N, weights);
    return {eta.re / Z, eta.im / Z};
}

Complex system1_L_borwein(double beta, double t, int N) {
    Complex eta = borwein_eta(beta, t);

    std::vector<double> weights(N);
    precompute_weights(beta, N, weights.data());
    double Z = partition_function(0.0, N, weights.data());

    return {eta.re / Z, eta.im / Z};
}

Complex system1_L_em(double beta, double t, int N, int p) {
    std::vector<double> weights(N), log_n(N);
    precompute_weights(beta, N, weights.data());
    precompute_log_n(N, log_n.data());

    // zeta(s) ~ S_N(s) + em_tail(s, N, p)
    Complex S = partial_dirichlet(0.0, t, N, weights.data(), log_n.data());
    Complex tail = em_tail_correction(beta, t, N, p);
    Complex zeta_approx = {S.re + tail.re, S.im + tail.im};

    // eta(s) = (1 - 2^{1-s}) * zeta(s)  (Paper Eq. 2.16)
    double mag_2 = std::pow(2.0, 1.0 - beta);
    double arg_2 = -t * std::log(2.0);
    Complex two_1ms = {mag_2 * std::cos(arg_2), mag_2 * std::sin(arg_2)};
    Complex prefactor = {1.0 - two_1ms.re, -two_1ms.im};
    Complex eta = {prefactor.re * zeta_approx.re - prefactor.im * zeta_approx.im,
                   prefactor.re * zeta_approx.im + prefactor.im * zeta_approx.re};

    double Z = partition_function(0.0, N, weights.data());
    return {eta.re / Z, eta.im / Z};
}
