package field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implements a simplified LLL (Lenstra-Lenstra-Lovasz) lattice basis reduction
 * algorithm to search for integer relations between mathematical constants,
 * specifically targeting the residual r = 1 - (1/phi - 1/pi)(1/phi + e).
 *
 * <p>The program uses a PSLQ-style embedding: given constants x1..xn, it builds
 * a lattice matrix whose short vectors (after LLL reduction) correspond to
 * integer relations m1*x1 + ... + mn*xn ~= 0.
 *
 * <p>Groups searched:
 * <ul>
 *   <li>Group A (basic):   r, e, pi, phi, 1</li>
 *   <li>Group B (products): e*pi, e*phi, pi*phi, e*pi*phi</li>
 *   <li>Group C (powers):  e^2, pi^2, phi^2, sqrt(pi), sqrt(e)</li>
 *   <li>Group D (logs):    ln(2), ln(pi), ln(phi)</li>
 * </ul>
 */
public class LatticeRelationSearch {

    // -------------------------------------------------------------------------
    // Mathematical constants (double precision)
    // -------------------------------------------------------------------------

    /** Golden ratio phi = (1 + sqrt(5)) / 2. */
    private static final double PHI = (1.0 + Math.sqrt(5.0)) / 2.0;

    /** Archimedes' constant pi. */
    private static final double PI = Math.PI;

    /** Euler's number e. */
    private static final double E = Math.E;

    /**
     * Residual r = 1 - (1/phi - 1/pi)(1/phi + e).
     * Computed directly from PHI, PI, E.
     */
    private static final double R = 1.0 - (1.0 / PHI - 1.0 / PI) * (1.0 / PHI + E);

    /** Scaling constant C used in the lattice embedding (10^15). */
    private static final double C = 1.0e15;

    /** LLL delta parameter (Lovasz condition). Standard value 0.75. */
    private static final double DELTA = 0.75;

    /**
     * Threshold for considering a numerical relation valid.
     * Relations with |error| below this value are reported.
     */
    private static final double ERROR_THRESHOLD = 1.0e-8;

    // -------------------------------------------------------------------------
    // Entry point
    // -------------------------------------------------------------------------

    /**
     * Program entry point. Runs all lattice searches and prints results.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        System.out.println("=============================================================");
        System.out.println("  Lattice Relation Search (LLL/PSLQ)");
        System.out.println("  Target: r = 1 - (1/phi - 1/pi)(1/phi + e)");
        System.out.println("=============================================================\n");

        printConstantValues();

        List<RelationResult> allResults = new ArrayList<>();

        // --- Group A: basic constants ---
        runGroupSearch("Group A - basic: {r, e, pi, phi, 1}",
                new double[]{R, E, PI, PHI, 1.0},
                new String[]{"r", "e", "pi", "phi", "1"},
                allResults);

        // --- Group A without r (to independently verify constants) ---
        runGroupSearch("Group A sans r: {e, pi, phi, 1}",
                new double[]{E, PI, PHI, 1.0},
                new String[]{"e", "pi", "phi", "1"},
                allResults);

        // --- Group B: products ---
        runGroupSearch("Group B - products: {r, e*pi, e*phi, pi*phi, e*pi*phi}",
                new double[]{R, E * PI, E * PHI, PI * PHI, E * PI * PHI},
                new String[]{"r", "e*pi", "e*phi", "pi*phi", "e*pi*phi"},
                allResults);

        runGroupSearch("Group B with r and basics: {r, e, pi, e*pi, pi*phi}",
                new double[]{R, E, PI, E * PI, PI * PHI},
                new String[]{"r", "e", "pi", "e*pi", "pi*phi"},
                allResults);

        runGroupSearch("Group B with r and basics: {r, e, phi, e*phi, e*pi*phi}",
                new double[]{R, E, PHI, E * PHI, E * PI * PHI},
                new String[]{"r", "e", "phi", "e*phi", "e*pi*phi"},
                allResults);

        // --- Group C: powers ---
        runGroupSearch("Group C - powers: {r, e^2, pi^2, phi^2, 1}",
                new double[]{R, E * E, PI * PI, PHI * PHI, 1.0},
                new String[]{"r", "e^2", "pi^2", "phi^2", "1"},
                allResults);

        runGroupSearch("Group C - sqrt: {r, sqrt(pi), sqrt(e), phi, 1}",
                new double[]{R, Math.sqrt(PI), Math.sqrt(E), PHI, 1.0},
                new String[]{"r", "sqrt(pi)", "sqrt(e)", "phi", "1"},
                allResults);

        runGroupSearch("Group C mixed: {r, e, pi^2, phi^2, 1}",
                new double[]{R, E, PI * PI, PHI * PHI, 1.0},
                new String[]{"r", "e", "pi^2", "phi^2", "1"},
                allResults);

        runGroupSearch("Group C mixed: {r, e^2, pi, phi^2, 1}",
                new double[]{R, E * E, PI, PHI * PHI, 1.0},
                new String[]{"r", "e^2", "pi", "phi^2", "1"},
                allResults);

        // --- Group D: logarithms ---
        runGroupSearch("Group D - logs: {r, ln(2), ln(pi), ln(phi), 1}",
                new double[]{R, Math.log(2), Math.log(PI), Math.log(PHI), 1.0},
                new String[]{"r", "ln(2)", "ln(pi)", "ln(phi)", "1"},
                allResults);

        runGroupSearch("Group D with e: {r, e, ln(2), ln(pi), ln(phi)}",
                new double[]{R, E, Math.log(2), Math.log(PI), Math.log(PHI)},
                new String[]{"r", "e", "ln(2)", "ln(pi)", "ln(phi)"},
                allResults);

        // --- Cross-group searches ---
        runGroupSearch("Cross AB: {r, e, pi, phi, e*pi, e*phi}",
                new double[]{R, E, PI, PHI, E * PI, E * PHI},
                new String[]{"r", "e", "pi", "phi", "e*pi", "e*phi"},
                allResults);

        runGroupSearch("Cross ABC: {r, e, pi, phi^2, sqrt(pi), 1}",
                new double[]{R, E, PI, PHI * PHI, Math.sqrt(PI), 1.0},
                new String[]{"r", "e", "pi", "phi^2", "sqrt(pi)", "1"},
                allResults);

        runGroupSearch("Cross ACD: {r, e, pi, ln(2), phi, 1}",
                new double[]{R, E, PI, Math.log(2), PHI, 1.0},
                new String[]{"r", "e", "pi", "ln(2)", "phi", "1"},
                allResults);

        // --- Search on F components directly (1/phi, 1/pi, e, and 1) ---
        System.out.println("=============================================================");
        System.out.println("  Component Analysis: F = (1/phi - 1/pi)(1/phi + e)");
        System.out.println("=============================================================\n");

        runGroupSearch("Components: {1/phi, 1/pi, e, 1}",
                new double[]{1.0 / PHI, 1.0 / PI, E, 1.0},
                new String[]{"1/phi", "1/pi", "e", "1"},
                allResults);

        runGroupSearch("Components + r: {r, 1/phi, 1/pi, e, 1}",
                new double[]{R, 1.0 / PHI, 1.0 / PI, E, 1.0},
                new String[]{"r", "1/phi", "1/pi", "e", "1"},
                allResults);

        runGroupSearch("Components expanded: {1/phi, 1/pi, e, phi, pi}",
                new double[]{1.0 / PHI, 1.0 / PI, E, PHI, PI},
                new String[]{"1/phi", "1/pi", "e", "phi", "pi"},
                allResults);

        runGroupSearch("F structure: {(1/phi)^2, 1/phi*e, 1/pi*1/phi, e/pi, 1}",
                new double[]{1.0 / (PHI * PHI), E / PHI, 1.0 / (PI * PHI), E / PI, 1.0},
                new String[]{"1/phi^2", "e/phi", "1/(pi*phi)", "e/pi", "1"},
                allResults);

        // --- Print all results sorted by quality ---
        printSortedResults(allResults);
    }

    // -------------------------------------------------------------------------
    // LLL Algorithm
    // -------------------------------------------------------------------------

    /**
     * Performs LLL basis reduction on the given integer matrix.
     *
     * <p>The matrix B is given as B[row][col]. Each row is a basis vector.
     * The algorithm modifies B in place and returns the reduced basis.
     *
     * <p>The Lovasz condition uses delta = 0.75 (classic LLL).
     *
     * @param B     the n-by-m integer basis matrix (rows are basis vectors)
     * @param n     number of basis vectors (rows)
     * @param m     dimension of the ambient space (cols)
     * @return the LLL-reduced basis (same array, modified in place)
     */
    public static long[][] lllReduce(long[][] B, int n, int m) {
        // Gram-Schmidt coefficients mu[i][j] and squared norms Bstar[i]
        double[][] Bstar = new double[n][m]; // orthogonal basis vectors (as doubles)
        double[][] mu = new double[n][n];    // Gram-Schmidt coefficients

        // Initialise Bstar[0] = B[0]
        for (int j = 0; j < m; j++) {
            Bstar[0][j] = B[0][j];
        }

        int k = 1;
        while (k < n) {
            // Recompute Gram-Schmidt for row k
            gramSchmidtRow(B, Bstar, mu, k, m);

            // Size-reduce step: for j = k-1 down to 0
            for (int j = k - 1; j >= 0; j--) {
                long q = Math.round(mu[k][j]);
                if (q != 0) {
                    for (int l = 0; l < m; l++) {
                        B[k][l] -= q * B[j][l];
                    }
                    // Recompute Gram-Schmidt after the change
                    gramSchmidtRow(B, Bstar, mu, k, m);
                }
            }

            // Lovasz condition: ||Bstar[k]||^2 >= (delta - mu[k][k-1]^2) * ||Bstar[k-1]||^2
            double normK = dotProduct(Bstar[k], Bstar[k], m);
            double normKm1 = dotProduct(Bstar[k - 1], Bstar[k - 1], m);
            double muKKm1 = mu[k][k - 1];

            if (normK >= (DELTA - muKKm1 * muKKm1) * normKm1) {
                k++;
            } else {
                // Swap rows k and k-1
                long[] tmp = B[k];
                B[k] = B[k - 1];
                B[k - 1] = tmp;

                // Recompute Gram-Schmidt for rows k-1 and k
                gramSchmidtRow(B, Bstar, mu, k - 1, m);
                gramSchmidtRow(B, Bstar, mu, k, m);

                k = Math.max(k - 1, 1);
            }
        }
        return B;
    }

    /**
     * Recomputes the Gram-Schmidt orthogonalization for row k.
     *
     * <p>Sets Bstar[k] = B[k] - sum_{j=0}^{k-1} mu[k][j] * Bstar[j]
     * and updates mu[k][j] for all j &lt; k.
     *
     * @param B     integer basis matrix
     * @param Bstar orthogonal vectors (doubles)
     * @param mu    Gram-Schmidt coefficients
     * @param k     the row index to recompute
     * @param m     dimension of the ambient space
     */
    private static void gramSchmidtRow(long[][] B, double[][] Bstar, double[][] mu, int k, int m) {
        // Bstar[k] = B[k] initially
        for (int j = 0; j < m; j++) {
            Bstar[k][j] = B[k][j];
        }
        for (int j = 0; j < k; j++) {
            double bstarNormSq = dotProduct(Bstar[j], Bstar[j], m);
            if (bstarNormSq < 1e-20) {
                mu[k][j] = 0.0;
                continue;
            }
            // Compute dot product of B[k] (as double) with Bstar[j]
            double dot = 0.0;
            for (int l = 0; l < m; l++) {
                dot += B[k][l] * Bstar[j][l];
            }
            mu[k][j] = dot / bstarNormSq;
            for (int l = 0; l < m; l++) {
                Bstar[k][l] -= mu[k][j] * Bstar[j][l];
            }
        }
    }

    /**
     * Computes the dot product of two double-precision vectors.
     *
     * @param a first vector
     * @param b second vector
     * @param m length
     * @return dot product
     */
    private static double dotProduct(double[] a, double[] b, int m) {
        double s = 0.0;
        for (int i = 0; i < m; i++) {
            s += a[i] * b[i];
        }
        return s;
    }

    // -------------------------------------------------------------------------
    // PSLQ-style lattice embedding
    // -------------------------------------------------------------------------

    /**
     * Constructs the PSLQ-style lattice matrix for the given constants.
     *
     * <p>The matrix is (n+1) x (n+1):
     * <pre>
     *   [ I_n         | 0 ]
     *   [ round(C*x)  | C ]
     * </pre>
     * where I_n is the n-by-n identity and the last row encodes the constants.
     *
     * @param x the array of n constants
     * @return the (n+1) x (n+1) lattice matrix as a long array
     */
    public static long[][] buildLatticeMatrix(double[] x) {
        int n = x.length;
        int dim = n + 1;
        long[][] B = new long[dim][dim];

        // Identity block
        for (int i = 0; i < n; i++) {
            B[i][i] = 1L;
        }

        // Last row: scaled constants
        for (int j = 0; j < n; j++) {
            B[n][j] = Math.round(C * x[j]);
        }
        B[n][n] = Math.round(C); // = (long) C

        return B;
    }

    // -------------------------------------------------------------------------
    // Relation search and verification
    // -------------------------------------------------------------------------

    /**
     * Searches for integer relations among the given constants using LLL.
     *
     * <p>Builds the lattice matrix, reduces it with LLL, then inspects each
     * short row vector. The first n components of a short vector are candidate
     * integer coefficients m1..mn; the relation is m1*x1 + ... + mn*xn ~= 0.
     *
     * @param label      human-readable label for this search group
     * @param x          the constants to search
     * @param names      human-readable names for each constant
     * @param collector  list to which found results are added
     */
    public static void runGroupSearch(String label, double[] x, String[] names,
                                      List<RelationResult> collector) {
        System.out.println("--- " + label + " ---");

        int n = x.length;
        long[][] B = buildLatticeMatrix(x);
        lllReduce(B, n + 1, n + 1);

        List<RelationResult> groupResults = new ArrayList<>();

        for (int row = 0; row < n + 1; row++) {
            // Extract integer coefficients from the first n columns
            long[] coeffs = new long[n];
            for (int j = 0; j < n; j++) {
                coeffs[j] = B[row][j];
            }

            // Skip zero or trivially-zero rows
            boolean allZero = true;
            for (long c : coeffs) {
                if (c != 0) {
                    allZero = false;
                    break;
                }
            }
            if (allZero) continue;

            // Evaluate the relation: sum(coeffs[j] * x[j])
            double sum = 0.0;
            for (int j = 0; j < n; j++) {
                sum += coeffs[j] * x[j];
            }

            double error = Math.abs(sum);
            if (error < ERROR_THRESHOLD) {
                long maxCoeff = 0;
                for (long c : coeffs) {
                    if (Math.abs(c) > maxCoeff) maxCoeff = Math.abs(c);
                }
                double quality = error * maxCoeff;
                RelationResult result = new RelationResult(
                        label, coeffs, names, x, sum, error, maxCoeff, quality);
                groupResults.add(result);
                collector.add(result);
            }
        }

        if (groupResults.isEmpty()) {
            System.out.println("  No relations found below threshold " + ERROR_THRESHOLD);
        } else {
            Collections.sort(groupResults);
            for (RelationResult r : groupResults) {
                r.print("  ");
            }
        }
        System.out.println();
    }

    // -------------------------------------------------------------------------
    // Output helpers
    // -------------------------------------------------------------------------

    /**
     * Prints the values of all mathematical constants used in the search.
     */
    private static void printConstantValues() {
        System.out.println("--- Constant Values ---");
        System.out.printf("  phi       = %.15f%n", PHI);
        System.out.printf("  pi        = %.15f%n", PI);
        System.out.printf("  e         = %.15f%n", E);
        System.out.printf("  1/phi     = %.15f%n", 1.0 / PHI);
        System.out.printf("  1/pi      = %.15f%n", 1.0 / PI);
        System.out.printf("  r         = %.15f%n", R);
        System.out.printf("  F=1-r     = %.15f%n", 1.0 - R);
        System.out.printf("  C         = %.3e%n", C);
        System.out.printf("  threshold = %.3e%n", ERROR_THRESHOLD);
        System.out.println();
    }

    /**
     * Prints all collected results sorted by quality (ascending error * max-coeff).
     *
     * @param results the list of all found relations
     */
    private static void printSortedResults(List<RelationResult> results) {
        System.out.println("=============================================================");
        System.out.println("  All Relations Found (sorted by quality)");
        System.out.println("=============================================================\n");

        if (results.isEmpty()) {
            System.out.println("  No integer relations found below threshold " + ERROR_THRESHOLD);
            return;
        }

        // Deduplicate: keep only distinct (formula, error) pairs
        List<RelationResult> unique = deduplicateResults(results);
        Collections.sort(unique);

        System.out.printf("  Found %d unique relation(s):%n%n", unique.size());
        int rank = 1;
        for (RelationResult r : unique) {
            System.out.printf("  [%d] ", rank++);
            r.print("      ");
        }

        System.out.println();
        System.out.println("=============================================================");
        System.out.println("  Search Complete");
        System.out.println("=============================================================");
    }

    /**
     * Removes duplicate results based on the formula string.
     *
     * @param results list of all results (may contain duplicates from multiple groups)
     * @return list with duplicates removed
     */
    private static List<RelationResult> deduplicateResults(List<RelationResult> results) {
        List<RelationResult> unique = new ArrayList<>();
        List<String> seen = new ArrayList<>();
        for (RelationResult r : results) {
            String key = r.formulaKey();
            if (!seen.contains(key)) {
                seen.add(key);
                unique.add(r);
            }
        }
        return unique;
    }

    // -------------------------------------------------------------------------
    // Inner class: RelationResult
    // -------------------------------------------------------------------------

    /**
     * Holds one integer relation discovered by the LLL search.
     *
     * <p>Stores the coefficients, the names, the numerical error, and
     * a quality measure for sorting.
     */
    public static class RelationResult implements Comparable<RelationResult> {

        /** Source group label. */
        public final String group;

        /** Integer coefficients m1..mn. */
        public final long[] coeffs;

        /** Human-readable names of the constants. */
        public final String[] names;

        /** Numerical values of the constants. */
        public final double[] values;

        /** The sum m1*x1 + ... + mn*xn (signed). */
        public final double signedSum;

        /** Absolute value of the sum (the error). */
        public final double error;

        /** Maximum absolute coefficient value. */
        public final long maxCoeff;

        /**
         * Quality measure: error * maxCoeff.
         * Lower is better.
         */
        public final double quality;

        /**
         * Constructs a RelationResult.
         *
         * @param group      source group label
         * @param coeffs     integer coefficients
         * @param names      constant names
         * @param values     constant values
         * @param signedSum  evaluated signed sum
         * @param error      absolute error
         * @param maxCoeff   maximum absolute coefficient
         * @param quality    quality measure
         */
        public RelationResult(String group, long[] coeffs, String[] names, double[] values,
                              double signedSum, double error, long maxCoeff, double quality) {
            this.group = group;
            this.coeffs = coeffs;
            this.names = names;
            this.values = values;
            this.signedSum = signedSum;
            this.error = error;
            this.maxCoeff = maxCoeff;
            this.quality = quality;
        }

        /**
         * Builds a canonical formula string of the form
         * "c1*name1 + c2*name2 + ... = 0".
         *
         * @return formula string
         */
        public String formulaString() {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (int i = 0; i < coeffs.length; i++) {
                if (coeffs[i] == 0) continue;
                if (!first && coeffs[i] > 0) sb.append(" + ");
                else if (coeffs[i] < 0) sb.append(first ? "-" : " - ");
                long absC = Math.abs(coeffs[i]);
                if (absC != 1) {
                    sb.append(absC).append("*");
                }
                sb.append(names[i]);
                first = false;
            }
            sb.append(" = 0");
            return sb.toString();
        }

        /**
         * Returns a deduplication key based on the sorted non-zero (coeff, name) pairs.
         *
         * @return key string
         */
        public String formulaKey() {
            // Normalize: if leading coefficient is negative, negate all
            long[] c = coeffs.clone();
            for (long v : c) {
                if (v != 0) {
                    if (v < 0) {
                        for (int i = 0; i < c.length; i++) c[i] = -c[i];
                    }
                    break;
                }
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < c.length; i++) {
                sb.append(c[i]).append(":").append(names[i]).append(";");
            }
            return sb.toString();
        }

        /**
         * Prints the relation with a given indent prefix.
         *
         * @param indent indentation prefix string
         */
        public void print(String indent) {
            System.out.println(indent + "Relation:   " + formulaString());
            System.out.printf("%sError:      %.6e%n", indent, error);
            System.out.printf("%sMax coeff:  %d%n", indent, maxCoeff);
            System.out.printf("%sQuality:    %.6e%n", indent, quality);
            System.out.println(indent + "Group:      " + group);
            // Print individual terms for verification
            StringBuilder verify = new StringBuilder(indent + "Verify:     ");
            for (int i = 0; i < coeffs.length; i++) {
                if (coeffs[i] == 0) continue;
                double contribution = coeffs[i] * values[i];
                verify.append(String.format("(%d * %.6f = %.6f) ", coeffs[i], values[i], contribution));
            }
            System.out.println(verify);
            System.out.println();
        }

        /**
         * Compares two results by quality (ascending).
         *
         * @param other the other result
         * @return negative if this is better quality, positive otherwise
         */
        @Override
        public int compareTo(RelationResult other) {
            return Double.compare(this.quality, other.quality);
        }
    }
}
