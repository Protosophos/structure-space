#include "partition.h"
#include "kahan.h"

double partition_function(double /*beta*/, int N, const double* weights) {
    KahanAccumulator acc;
    for (int n = 0; n < N; ++n) {
        acc.add(weights[n]);
    }
    return acc.result();
}
