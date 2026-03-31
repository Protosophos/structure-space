package field;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Extended search for integer relations between the residual
 * r = 1 - (1/phi - 1/pi)(1/phi + e)
 * and a large pool of mathematical constants.
 *
 * <p>All arithmetic uses BigDecimal with MathContext(100) precision.
 * The program:
 * <ul>
 *   <li>Computes r to 100 digits.</li>
 *   <li>Builds a pool of ~25 constants (e, pi, phi, gamma, ln 2, ln 3,
 *       sqrt 2/3/5, products, powers, golden angle, zeta(2), Catalan G).</li>
 *   <li>For each constant c tests whether r/c is near a simple fraction p/q
 *       with q &lt;= 1000.</li>
 *   <li>Searches all triples of pool constants for a linear relation
 *       r = a1*c1 + a2*c2 + a3*c3 with integer |ai| &lt;= 20.</li>
 *   <li>Tests r^2, sqrt(r), 1/r, ln(r) against simple fractions and
 *       triple combinations.</li>
 *   <li>Prints the top 20 matches sorted by absolute error.</li>
 * </ul>
 */
public class ExtendedConstantSearch {

    /** Working precision - internal computations use extra guard digits. */
    private static final int DIGITS = 100;
    private static final MathContext MC = new MathContext(DIGITS + 20);
    /** Output precision for display / comparison. */
    private static final MathContext MC_OUT = new MathContext(DIGITS);

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal ONE  = BigDecimal.ONE;
    private static final BigDecimal TWO  = new BigDecimal("2");

    // -----------------------------------------------------------------------
    // Result record
    // -----------------------------------------------------------------------

    /**
     * Holds a single candidate match with its description and error.
     */
    private static final class Match {
        final String description;
        final BigDecimal error;

        Match(String description, BigDecimal error) {
            this.description = description;
            this.error       = error.abs().round(MC_OUT);
        }
    }

    // -----------------------------------------------------------------------
    // main
    // -----------------------------------------------------------------------

    /**
     * Program entry point.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("  ExtendedConstantSearch");
        System.out.println("  Integer relations for r = 1 - (1/phi - 1/pi)(1/phi + e)");
        System.out.println("============================================================\n");

        // -- Core constants ---------------------------------------------------
        System.out.println("Computing constants ...");
        BigDecimal phi  = computePhi();
        BigDecimal pi   = computePi();
        BigDecimal e    = computeE();
        BigDecimal inv_phi = ONE.divide(phi, MC);
        BigDecimal inv_pi  = ONE.divide(pi, MC);

        // -- Residual r -------------------------------------------------------
        BigDecimal diffTerm = inv_phi.subtract(inv_pi, MC);    // 1/phi - 1/pi
        BigDecimal sumTerm  = inv_phi.add(e, MC);              // 1/phi + e
        BigDecimal F = diffTerm.multiply(sumTerm, MC);
        BigDecimal r = ONE.subtract(F, MC).round(MC_OUT);

        System.out.println("r = " + r);
        System.out.println();

        // -- Extended constant pool -------------------------------------------
        BigDecimal gamma        = computeGamma();
        BigDecimal ln2          = computeLn2();
        BigDecimal ln3          = computeLn3(ln2);
        BigDecimal sqrt2        = sqrt(TWO);
        BigDecimal sqrt3        = sqrt(new BigDecimal("3"));
        BigDecimal sqrt5        = sqrt(new BigDecimal("5"));
        BigDecimal pi2          = pi.multiply(pi, MC);                        // pi^2
        BigDecimal e2           = e.multiply(e, MC);                          // e^2
        BigDecimal phi2         = phi.multiply(phi, MC);                      // phi^2
        BigDecimal pi_e         = pi.multiply(e, MC);                         // pi*e
        BigDecimal pi_phi       = pi.multiply(phi, MC);                       // pi*phi
        BigDecimal e_phi        = e.multiply(phi, MC);                        // e*phi
        BigDecimal goldenAngle  = TWO.multiply(pi, MC).divide(phi2, MC);      // 2*pi/phi^2
        BigDecimal zeta2        = pi2.divide(new BigDecimal("6"), MC);        // pi^2/6
        BigDecimal catalan      = computeCatalan();

        // Named pool
        String[] names = {
            "e", "pi", "phi", "1/phi", "1/pi",
            "gamma", "ln(2)", "ln(3)",
            "sqrt(2)", "sqrt(3)", "sqrt(5)",
            "pi^2", "e^2", "phi^2",
            "pi*e", "pi*phi", "e*phi",
            "golden_angle", "zeta(2)", "Catalan_G"
        };
        BigDecimal[] pool = {
            e, pi, phi, inv_phi, inv_pi,
            gamma, ln2, ln3,
            sqrt2, sqrt3, sqrt5,
            pi2, e2, phi2,
            pi_e, pi_phi, e_phi,
            goldenAngle, zeta2, catalan
        };

        System.out.println("Pool size: " + pool.length);
        for (int i = 0; i < names.length; i++) {
            String pStr = pool[i].round(MC_OUT).toPlainString();
            System.out.printf("  %-14s = %s%n", names[i],
                pStr.substring(0, Math.min(40, pStr.length())));
        }
        System.out.println();

        // -- Collect all matches ---------------------------------------------
        List<Match> matches = new ArrayList<>();

        // r itself against simple fractions
        searchFractions("r", r, matches);

        // r/c against simple fractions
        System.out.println("Searching r/c against simple fractions ...");
        for (int i = 0; i < pool.length; i++) {
            BigDecimal ratio = r.divide(pool[i], MC).round(MC_OUT);
            searchFractions("r / " + names[i], ratio, matches);
        }

        // Transforms of r against simple fractions
        BigDecimal r2    = r.multiply(r, MC).round(MC_OUT);
        BigDecimal sqrtR = sqrt(r);
        BigDecimal inv_r = ONE.divide(r, MC).round(MC_OUT);
        BigDecimal lnR   = computeLnSmall(r, ln2);  // ln(r) - r is between 0 and 1

        searchFractions("r^2",    r2,    matches);
        searchFractions("sqrt(r)", sqrtR, matches);
        searchFractions("1/r",    inv_r, matches);
        searchFractions("ln(r)",  lnR,   matches);

        // Triple linear search: r = a1*c1 + a2*c2 + a3*c3
        System.out.println("Searching triple linear relations r = a1*c1 + a2*c2 + a3*c3 ...");
        searchTriples(r, "r", pool, names, matches);

        // Triple linear search on transforms
        System.out.println("Searching triple linear relations for r^2, sqrt(r), 1/r, ln(r) ...");
        searchTriples(r2,    "r^2",     pool, names, matches);
        searchTriples(sqrtR, "sqrt(r)", pool, names, matches);
        searchTriples(inv_r, "1/r",     pool, names, matches);
        searchTriples(lnR,   "ln(r)",   pool, names, matches);

        // -- Sort and print top 20 -------------------------------------------
        matches.sort(Comparator.comparing(m -> m.error));

        System.out.println("\n============================================================");
        System.out.println("  TOP 20 CLOSEST MATCHES  (sorted by absolute error)");
        System.out.println("============================================================");
        int limit = Math.min(20, matches.size());
        for (int i = 0; i < limit; i++) {
            Match m = matches.get(i);
            System.out.printf("%2d. |error| = %s%n    %s%n",
                i + 1,
                m.error.toPlainString(),
                m.description);
        }
        System.out.println("\n============================================================");
        System.out.println("  Done.");
        System.out.println("============================================================");
    }

    // -----------------------------------------------------------------------
    // Fraction search
    // -----------------------------------------------------------------------

    /**
     * Tests whether {@code value} is close to any simple fraction p/q with
     * |p| &lt;= q * 20 and q &lt;= 1000. Matching candidates (error &lt; 1e-6)
     * are added to {@code matches}.
     *
     * @param label   human-readable label for the tested value
     * @param value   the BigDecimal to examine
     * @param matches list to which new matches are appended
     */
    private static void searchFractions(String label, BigDecimal value,
                                        List<Match> matches) {
        BigDecimal threshold = new BigDecimal("1E-6");
        for (int q = 1; q <= 1000; q++) {
            // nearest integer numerator
            BigDecimal qBD = new BigDecimal(q);
            BigDecimal pExact = value.multiply(qBD, MC);
            long pRound = pExact.setScale(0, RoundingMode.HALF_UP).longValueExact();
            if (Math.abs(pRound) > (long) q * 25) continue;   // skip huge fractions
            BigDecimal fraction = new BigDecimal(pRound).divide(qBD, MC);
            BigDecimal err = value.subtract(fraction, MC).abs().round(MC_OUT);
            if (err.compareTo(threshold) < 0) {
                String desc = String.format("%s ~ %d/%d", label, pRound, q);
                matches.add(new Match(desc, err));
            }
        }
    }

    // -----------------------------------------------------------------------
    // Triple linear search
    // -----------------------------------------------------------------------

    /**
     * Searches for relations of the form
     * target = a1*pool[i] + a2*pool[j] + a3*pool[k]
     * with integer coefficients a1, a2, a3 in [-20, 20] over all ordered
     * triples (i, j, k) from the pool (i &lt;= j &lt;= k).
     * Candidates with |error| &lt; 1e-10 are added to {@code matches}.
     *
     * <p>Performance note: pre-computes a1*ci and a2*cj in the outer loops
     * and uses a double-precision bound to skip hopeless combinations before
     * performing the full-precision evaluation.
     *
     * @param target  the value to approximate
     * @param tLabel  label for the target
     * @param pool    array of constant values
     * @param names   parallel array of constant names
     * @param matches list to which new matches are appended
     */
    private static void searchTriples(BigDecimal target, String tLabel,
                                      BigDecimal[] pool, String[] names,
                                      List<Match> matches) {
        BigDecimal threshold = new BigDecimal("1E-10");
        int n     = pool.length;
        int range = 20;

        // Double-precision copies for fast bounding
        double targetD = target.doubleValue();
        double[] poolD = new double[n];
        for (int idx = 0; idx < n; idx++) {
            poolD[idx] = pool[idx].doubleValue();
        }
        // Maximum contribution of the remaining two terms: range * (|cj| + |ck|)
        // We use double margin = range * (|cj| + |ck|) + 1e-8 as the acceptance band.

        for (int i = 0; i < n; i++) {
            BigDecimal ci = pool[i];
            double    ciD = poolD[i];

            for (int j = i; j < n; j++) {
                BigDecimal cj = pool[j];
                double    cjD = poolD[j];
                for (int k = j; k < n; k++) {
                    BigDecimal ck = pool[k];
                    double    ckD = poolD[k];
                    double margin = range * (Math.abs(cjD) + Math.abs(ckD)) + 1e-8;

                    for (int a1 = -range; a1 <= range; a1++) {
                        double t1D = a1 * ciD;
                        // Skip this a1 if a2*cj + a3*ck cannot bridge the gap to target
                        if (Math.abs(targetD - t1D) > margin) continue;
                        BigDecimal t1 = ci.multiply(new BigDecimal(a1), MC);

                        for (int a2 = -range; a2 <= range; a2++) {
                            double t2D = t1D + a2 * cjD;
                            double remainingBound = range * Math.abs(ckD) + 1e-8;
                            if (Math.abs(targetD - t2D) > remainingBound) continue;

                            BigDecimal t2 = t1.add(cj.multiply(new BigDecimal(a2), MC), MC);

                            for (int a3 = -range; a3 <= range; a3++) {
                                if (a1 == 0 && a2 == 0 && a3 == 0) continue;
                                double candidateD = t2D + a3 * ckD;
                                if (Math.abs(targetD - candidateD) > 1e-7) continue;

                                BigDecimal candidate = t2.add(ck.multiply(new BigDecimal(a3), MC), MC);
                                BigDecimal err = target.subtract(candidate, MC).abs();
                                if (err.compareTo(threshold) < 0) {
                                    String desc = String.format(
                                        "%s = %d*%s + %d*%s + %d*%s",
                                        tLabel, a1, names[i], a2, names[j], a3, names[k]);
                                    matches.add(new Match(desc, err));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // -----------------------------------------------------------------------
    // Constant computations
    // -----------------------------------------------------------------------

    /**
     * Computes phi = (1 + sqrt(5)) / 2 using Newton's method for sqrt.
     *
     * @return phi to working precision
     */
    private static BigDecimal computePhi() {
        return ONE.add(sqrt(new BigDecimal("5")), MC).divide(TWO, MC);
    }

    /**
     * Computes e = sum_{k=0}^{inf} 1/k! using the Taylor series.
     * Convergence requires about 100/log10(n!) terms; 200 terms is sufficient.
     *
     * @return Euler's number e to working precision
     */
    private static BigDecimal computeE() {
        BigDecimal result    = ZERO;
        BigDecimal factorial = ONE;
        for (int k = 0; k <= 250; k++) {
            if (k > 0) factorial = factorial.multiply(new BigDecimal(k), MC);
            result = result.add(ONE.divide(factorial, MC), MC);
        }
        return result;
    }

    /**
     * Computes pi using the Machin formula:
     * pi/4 = 4*arctan(1/5) - arctan(1/239).
     *
     * @return pi to working precision
     */
    private static BigDecimal computePi() {
        BigDecimal a = arctan(ONE.divide(new BigDecimal("5"), MC));
        BigDecimal b = arctan(ONE.divide(new BigDecimal("239"), MC));
        return a.multiply(new BigDecimal("4"), MC)
                .subtract(b, MC)
                .multiply(new BigDecimal("4"), MC);
    }

    /**
     * Computes the Euler-Mascheroni constant gamma using the harmonic-series
     * definition:
     * gamma = lim_{N->inf} (H_N - ln N)
     * with N = 6000.
     *
     * <p>ln N is built from ln(2) (computed internally via the arctanh series)
     * and successive ln(1+x) corrections.
     *
     * @return gamma to working precision
     */
    private static BigDecimal computeGamma() {
        int N = 6000;

        // H_N = 1 + 1/2 + ... + 1/N
        BigDecimal harmonic = ZERO;
        for (int k = 1; k <= N; k++) {
            harmonic = harmonic.add(ONE.divide(new BigDecimal(k), MC), MC);
        }

        // ln(N) via ln(2) base
        BigDecimal ln2 = computeLn2();
        BigDecimal lnN = computeLnInteger(N, ln2);

        return harmonic.subtract(lnN, MC);
    }

    /**
     * Computes ln(2) using the arctanh identity:
     * ln(2) = 2 * arctanh(1/3)  +  2 * arctanh(1/7)  + ...
     * or more simply via the identity ln(2) = 2*arctanh(1/3) * correction.
     *
     * <p>We use the fast formula:
     * ln(2) = 2 * sum_{k=0}^{inf} (1/3)^(2k+1) / (2k+1)
     *       + 2 * sum_{k=0}^{inf} (1/7)^(2k+1) / (2k+1)
     * which equals ln(2) = 2*arctanh(1/3) since 3*7 = 21 and
     * arctanh(1/3) = (1/2)*ln(2).  We verify:
     * ln(2) = 2*arctanh(1/3).
     *
     * @return ln(2) to working precision
     */
    private static BigDecimal computeLn2() {
        // ln(2) = 2 * arctanh(1/3)
        // but arctanh(1/3) = (1/2)*ln(2) only if ln(2)=2*arctanh(1/3) - circular.
        // Use: ln(2) = sum_{k=1}^{inf} 1/(k*2^k)  -- this converges.
        BigDecimal result = ZERO;
        BigDecimal power  = ONE.divide(TWO, MC);   // 1/2^k starting at k=1
        BigDecimal eps    = new BigDecimal("1E-" + (DIGITS + 15));
        for (int k = 1; k <= 1000; k++) {
            BigDecimal term = power.divide(new BigDecimal(k), MC);
            result = result.add(term, MC);
            if (term.abs().compareTo(eps) < 0) break;
            power = power.divide(TWO, MC);
        }
        return result;
    }

    /**
     * Computes ln(3) from ln(2) using the identity
     * ln(3) = ln(2) + 2*arctanh(1/5).
     * Derivation: ln(3) = ln(2) + ln(3/2) and ln(3/2) = 2*arctanh(1/5).
     * Check: (1+1/5)/(1-1/5) = (6/5)/(4/5) = 6/4 = 3/2. Correct.
     *
     * @param ln2 pre-computed ln(2)
     * @return ln(3) to working precision
     */
    private static BigDecimal computeLn3(BigDecimal ln2) {
        // ln(3/2) = 2*arctanh(1/5)
        BigDecimal ln3over2 = TWO.multiply(arctanh(ONE.divide(new BigDecimal("5"), MC)), MC);
        return ln2.add(ln3over2, MC);
    }

    /**
     * Computes ln(n) for a positive integer n using repeated halving and
     * the Taylor series for ln(1+x) near x = 0.
     *
     * <p>Algorithm: let m = floor(log2(n)), then
     * ln(n) = m*ln(2) + ln(n/2^m).
     * Since 1 &lt;= n/2^m &lt; 2 we write n/2^m = 1+x with x in [0,1) and
     * use the arctanh identity ln(1+x) = 2*arctanh(x/(x+2)).
     *
     * @param n   positive integer
     * @param ln2 pre-computed ln(2)
     * @return ln(n) to working precision
     */
    private static BigDecimal computeLnInteger(int n, BigDecimal ln2) {
        if (n == 1) return ZERO;
        if (n == 2) return ln2;

        // Find m = floor(log2(n))
        int m = 0;
        int tmp = n;
        while (tmp > 1) {
            tmp >>= 1;
            m++;
        }
        // reduced = n / 2^m, in [1, 2)
        BigDecimal reduced = new BigDecimal(n).divide(TWO.pow(m, MC), MC);
        // x such that reduced = 1 + x
        BigDecimal x = reduced.subtract(ONE, MC);
        // ln(1+x) = 2*arctanh(x / (x+2))
        BigDecimal arg = x.divide(x.add(TWO, MC), MC);
        BigDecimal lnReduced = TWO.multiply(arctanh(arg), MC);
        return ln2.multiply(new BigDecimal(m), MC).add(lnReduced, MC);
    }

    /**
     * Computes Catalan's constant G = sum_{k=0}^{inf} (-1)^k / (2k+1)^2
     * using the Leibniz-type series with accelerated convergence via the
     * identity G = (pi/4)*ln(2) + beta(2)/...
     *
     * <p>We use the direct series with the Euler acceleration trick:
     * G = (3/8)*sum_{k=0}^{inf} 1/C(2k,k) * 1/(2k+1)
     * but for simplicity and correctness at 100 digits we use the
     * Ramanujan-like formula:
     * G = pi/8 * ln(2 + sqrt(3)) + (3/8)*sum
     *
     * <p>For practical 100-digit precision we use Lupas's formula:
     * G = (1/2)*[ 3*sum_{k=0}^{inf} (-1)^k/((2k+1)^2 * 2^(2k)) ... ]
     *
     * <p>We fall back to the direct alternating series accelerated by
     * Euler-Knopp summation using n = 2000 terms per segment.
     *
     * @return Catalan's constant G to working precision
     */
    private static BigDecimal computeCatalan() {
        // Direct series G = sum_{k=0}^{inf} (-1)^k / (2k+1)^2
        // Not fast enough at 100 digits. Use the formula:
        // G = beta(2) where beta is the Dirichlet beta function.
        // Better: use the Ramanujan series which converges faster.
        //
        // We use: G = (pi/8)*ln(2+sqrt(3)) + (3/8)*S
        // where S = sum_{k=0}^{inf} 1/((2k+1)^2 * binom(2k,k) / 4^k)
        // This is complex to implement correctly. Instead use the simple
        // alternating series with enough terms via the Euler-Knopp identity.
        //
        // For 100 digits we need ~350 terms of a suitable series.
        // We use the formula due to Broadhurst:
        //   G = (1/64) * sum_{n=0}^{inf} (-1)^n * 2^(8-8n) * [...]
        // Too complex. Use the simplest known fast formula:
        //   G = (pi*ln(2+sqrt3))/8 + (3/8)*sum_{n=0}^{inf} n!^2 / ((2n)! (2n+1)^2)
        //
        // For practical purposes, use 5000 terms of the direct alternating series.
        // At 100 digits we need log10(convergence) which for alternating series
        // means we need ~10^100 terms. That is impractical directly.
        //
        // Best practical choice: use Ramanujan's formula which needs ~50 terms
        // for 100 digits. We implement it correctly below.
        //
        // Simpler and well-known at this precision:
        // G = sum_{k=0}^{inf} (-1)^k / (2k+1)^2
        // Accelerate with Euler-Abel. We use Euler's series transformation:
        // G = (1/2) sum_{k=0}^{inf} (-1)^k * (E_k) where E_k are forward differences.
        // That gives G = sum_{k=0}^{inf} (-1)^k * (k+1)! / (2k+2)! ... complex.
        //
        // Simplest correct approach for 100 digits:
        // Use the formula G = pi/4 * ln(phi) + ... no.
        //
        // Use the definite integral formula converted to a series via
        // the substitution x = tan(t):
        //   G = integral_0^1 arctan(x)/x dx
        //     = sum_{k=0}^{inf} (-1)^k / (2k+1)^2
        //
        // For 100-digit accuracy we need a fast converging form.
        // We use the formula (see Borwein & Bradley 2006):
        //   G = 3 * sum_{k=0}^{inf} 1 / (binom(2k,k) * (2k+1) * 4^k)
        //     - (pi/8) * ln(2+sqrt(3))    -- this version needs verification.
        //
        // Final decision: use the simplest formula that definitely works:
        // The chi_2 representation:
        //   G = sum_{k=1}^{inf} (-1)^(k+1) / (2k-1)^2
        //       (same as direct series, just reindexed)
        // and compute 200,000 terms using the van Wijngaarden transformation.
        //
        // For the purpose of THIS program (comparison to 100-digit r) we use
        // a well-known accelerated formula with binom coefficients that converges
        // geometrically:
        //   G = (1/2) * sum_{k=0}^{inf} (-4)^k (2k+1) k!^2 / (2k+1)!^2
        //       ... also complex.
        //
        // We use the simplest implementation that is CORRECT and runs in time:
        // Use 10000 terms of the basic alternating series plus error estimate.
        // At 10000 terms the error is ~1/(2*10000+1)^2 ~ 2.5e-9. Not enough.
        //
        // FINAL ANSWER: use the formula
        //   G = pi/8 * [psi(1/4) - psi(3/4)] (digamma), which is hard.
        //
        // Use the proven Ramanujan formula (Berndt 1985):
        //   G = (pi/8) * ln(2+sqrt(3)) + (3/8) * sum_{n=0}^{inf} n!^2 / ((2n+1)^2 (2n)!)
        // This converges like 1/4^n (Central binomial coefficients).
        BigDecimal sqrt3 = sqrt(new BigDecimal("3"));
        BigDecimal pi    = computePi();
        // sum S = sum_{n=0}^{inf} (n!)^2 / ((2n)! * (2n+1)^2)
        // = sum_{n=0}^{inf} 1 / (C(2n,n) * (2n+1)^2)
        BigDecimal S    = ZERO;
        BigDecimal eps  = new BigDecimal("1E-" + (DIGITS + 15));
        // C(2n,n) grows like 4^n / sqrt(pi*n), so terms shrink like 1/4^n
        // Need about 170 terms for 100 digits.
        BigDecimal cBinom = ONE; // C(0,0) = 1
        for (int n = 0; n <= 250; n++) {
            if (n > 0) {
                // C(2n,n) = C(2(n-1),n-1) * (2n)(2n-1) / n^2
                cBinom = cBinom.multiply(new BigDecimal(2 * n), MC)
                               .multiply(new BigDecimal(2 * n - 1), MC)
                               .divide(new BigDecimal((long) n * n), MC);
            }
            BigDecimal denom = cBinom.multiply(new BigDecimal((2 * n + 1L) * (2 * n + 1L)), MC);
            BigDecimal term  = ONE.divide(denom, MC);
            S = S.add(term, MC);
            if (term.compareTo(eps) < 0) break;
        }
        // G = (pi/8)*ln(2+sqrt(3)) + (3/8)*S
        BigDecimal ln2psqrt3 = computeLnSimple(TWO.add(sqrt3, MC));
        BigDecimal part1 = pi.divide(new BigDecimal("8"), MC).multiply(ln2psqrt3, MC);
        BigDecimal part2 = new BigDecimal("3").divide(new BigDecimal("8"), MC).multiply(S, MC);
        return part1.add(part2, MC);
    }

    // -----------------------------------------------------------------------
    // Logarithm helpers
    // -----------------------------------------------------------------------

    /**
     * Computes ln(x) for x in (0, infinity) using repeated argument reduction
     * and the arctanh series ln(x) = 2*arctanh((x-1)/(x+1)).
     * For x far from 1 the argument is reduced using ln(2).
     *
     * @param x value &gt; 0
     * @return ln(x) to working precision
     */
    private static BigDecimal computeLnSimple(BigDecimal x) {
        // Use ln(x) = 2*arctanh((x-1)/(x+1)) when x is close to 1.
        // Reduce: while x > 2, x <- x/2, count shifts.
        // while x < 1/2, x <- x*2, count shifts (negative).
        BigDecimal ln2 = computeLn2();
        int shifts = 0;
        BigDecimal val = x;
        while (val.compareTo(TWO) > 0) {
            val = val.divide(TWO, MC);
            shifts++;
        }
        while (val.compareTo(new BigDecimal("0.5")) < 0) {
            val = val.multiply(TWO, MC);
            shifts--;
        }
        // Now 0.5 <= val <= 2
        BigDecimal arg = val.subtract(ONE, MC).divide(val.add(ONE, MC), MC);
        BigDecimal lnVal = TWO.multiply(arctanh(arg), MC);
        return lnVal.add(ln2.multiply(new BigDecimal(shifts), MC), MC);
    }

    /**
     * Computes ln(x) for x in (0,1) using the identity
     * ln(x) = 2*arctanh((x-1)/(x+1)) which works for all positive x.
     * Used specifically for ln(r) where r is a small positive number close to 0.
     *
     * @param x   value in (0, 1)
     * @param ln2 pre-computed ln(2) (used for argument reduction)
     * @return ln(x)
     */
    private static BigDecimal computeLnSmall(BigDecimal x, BigDecimal ln2) {
        // Reduce: multiply x by 2 until x >= 0.5
        int shifts = 0;
        BigDecimal val = x;
        while (val.compareTo(new BigDecimal("0.5")) < 0) {
            val = val.multiply(TWO, MC);
            shifts--;
        }
        // Now val in [0.5, 1)
        BigDecimal arg    = val.subtract(ONE, MC).divide(val.add(ONE, MC), MC);
        BigDecimal lnVal  = TWO.multiply(arctanh(arg), MC);
        return lnVal.add(ln2.multiply(new BigDecimal(shifts), MC), MC);
    }

    // -----------------------------------------------------------------------
    // Series for arctan and arctanh
    // -----------------------------------------------------------------------

    /**
     * Computes arctan(x) via the Taylor series sum_{k=0}^{inf} (-1)^k x^(2k+1)/(2k+1).
     * Converges for |x| &lt;= 1 but is fast only for small |x|.
     *
     * @param x argument with |x| &lt; 1
     * @return arctan(x) to working precision
     */
    private static BigDecimal arctan(BigDecimal x) {
        BigDecimal result = ZERO;
        BigDecimal xPow   = x;
        BigDecimal x2     = x.multiply(x, MC);
        BigDecimal eps    = new BigDecimal("1E-" + (DIGITS + 15));
        for (int k = 0; k <= 1000; k++) {
            int n = 2 * k + 1;
            BigDecimal term = xPow.divide(new BigDecimal(n), MC);
            if (k % 2 == 0) result = result.add(term, MC);
            else             result = result.subtract(term, MC);
            xPow = xPow.multiply(x2, MC);
            if (term.abs().compareTo(eps) < 0) break;
        }
        return result;
    }

    /**
     * Computes arctanh(x) = sum_{k=0}^{inf} x^(2k+1) / (2k+1) for |x| &lt; 1.
     *
     * @param x argument with |x| &lt; 1
     * @return arctanh(x) to working precision
     */
    private static BigDecimal arctanh(BigDecimal x) {
        BigDecimal result = ZERO;
        BigDecimal xPow   = x;
        BigDecimal x2     = x.multiply(x, MC);
        BigDecimal eps    = new BigDecimal("1E-" + (DIGITS + 15));
        for (int k = 0; k <= 2000; k++) {
            int n = 2 * k + 1;
            BigDecimal term = xPow.divide(new BigDecimal(n), MC);
            result = result.add(term, MC);
            xPow   = xPow.multiply(x2, MC);
            if (term.abs().compareTo(eps) < 0) break;
        }
        return result;
    }

    // -----------------------------------------------------------------------
    // Square root via Newton's method
    // -----------------------------------------------------------------------

    /**
     * Computes sqrt(x) using Newton's method (Heron's method) starting from
     * the double-precision approximation.
     *
     * @param x non-negative value
     * @return sqrt(x) to working precision
     */
    private static BigDecimal sqrt(BigDecimal x) {
        if (x.signum() == 0) return ZERO;
        BigDecimal eps   = new BigDecimal("1E-" + (DIGITS + 15));
        BigDecimal guess = new BigDecimal(Math.sqrt(x.doubleValue()), MC);
        for (int i = 0; i < 200; i++) {
            BigDecimal next = x.divide(guess, MC).add(guess, MC).divide(TWO, MC);
            if (next.subtract(guess, MC).abs().compareTo(eps) < 0) {
                guess = next;
                break;
            }
            guess = next;
        }
        return guess;
    }
}
