#ifndef PARTITION_H
#define PARTITION_H

/**
 * Partition function Z_N(beta) = sum_{n=1}^{N} n^{-beta}.
 * Paper Eq. 2.8.
 *
 * Computed by direct summation with Kahan compensation.
 * For beta <= 1, Z_N diverges as N^{1-beta}/(1-beta) (Paper Eq. 2.24).
 * This is expected; L_N = eta_N / Z_N is well-defined at finite N.
 *
 * @param beta the inverse temperature
 * @param N truncation parameter
 * @param weights precomputed n^{-beta}, size N
 * @return Z_N(beta)
 */
double partition_function(double beta, int N, const double* weights);

#endif // PARTITION_H
