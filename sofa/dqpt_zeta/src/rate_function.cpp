#include "rate_function.h"
#include "config.h"
#include <cmath>

double rate_function(double abs_L, int N) {
    double clamped = (abs_L > UNDERFLOW_GUARD) ? abs_L : UNDERFLOW_GUARD;
    return -std::log(clamped) / std::log(static_cast<double>(N));
}

bool is_sentinel(double abs_L) {
    return abs_L == 0.0 || abs_L < UNDERFLOW_GUARD;
}
