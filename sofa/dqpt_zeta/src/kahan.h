#ifndef KAHAN_H
#define KAHAN_H

/**
 * Kahan compensated summation (Paper Section 3.1, Listing 1).
 *
 * Reduces accumulated floating-point rounding error from O(N * eps_mach)
 * to O(eps_mach), independent of N.
 *
 * On GPU: compile with --fmad=false or use __dadd_rn() / __dsub_rn()
 * intrinsics to prevent FMA from defeating compensation (Paper Remark 3.2).
 */
struct KahanAccumulator {
    double sum = 0.0;
    double comp = 0.0;

    /**
     * Adds a value to the running sum with error compensation.
     *
     * @param x the value to add
     */
    void add(double x) {
        double y = x - comp;
        double t = sum + y;
        comp = (t - sum) - y;
        sum = t;
    }

    /**
     * Returns the compensated sum.
     *
     * @return the accumulated sum
     */
    double result() const {
        return sum;
    }

    /**
     * Resets the accumulator to zero.
     */
    void reset() {
        sum = 0.0;
        comp = 0.0;
    }
};

#endif // KAHAN_H
