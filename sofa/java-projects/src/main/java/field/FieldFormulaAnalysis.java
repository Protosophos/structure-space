package field;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Analyzes the ProtoSophos "Field Formula" near-unity relation between phi, pi, and e.
 * Computes the residual r = 1 - (1/phi - 1/pi)(1/phi + e) to high precision
 * and searches for integer relations using a simplified LLL/PSLQ approach.
 *
 * F = (1/phi - 1/pi)(1/phi + e) = 1 - r
 * Equivalent to: e = (pi + 1) / (pi - phi) - delta
 */
public class FieldFormulaAnalysis {

    private static final MathContext MC = new MathContext(120);

    /**
     * Entry point. Runs all analyses and prints results.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        System.out.println("=============================================================");
        System.out.println("  Field Formula Analysis: (1/phi - 1/pi)(1/phi + e) = 1 - r");
        System.out.println("  ProtoSophos Near-Unity Relation");
        System.out.println("=============================================================\n");

        // Compute constants to 100 digits
        BigDecimal phi = computePhi(MC);
        BigDecimal pi = computePi(MC);
        BigDecimal e = computeE(MC);

        BigDecimal invPhi = BigDecimal.ONE.divide(phi, MC);
        BigDecimal invPi = BigDecimal.ONE.divide(pi, MC);

        System.out.println("--- Constants (100 digits) ---");
        System.out.println("phi   = " + phi);
        System.out.println("pi    = " + pi);
        System.out.println("e     = " + e);
        System.out.println("1/phi = " + invPhi);
        System.out.println("1/pi  = " + invPi);
        System.out.println();

        // Compute field formula
        BigDecimal aDiffP = invPhi.subtract(invPi, MC);      // 1/phi - 1/pi
        BigDecimal aPlusE = invPhi.add(e, MC);                // 1/phi + e
        BigDecimal F = aDiffP.multiply(aPlusE, MC);           // (1/phi - 1/pi)(1/phi + e)
        BigDecimal r = BigDecimal.ONE.subtract(F, MC);        // residual

        System.out.println("--- Field Formula ---");
        System.out.println("a - p     = " + aDiffP);
        System.out.println("a + e     = " + aPlusE);
        System.out.println("F         = " + F);
        System.out.println("r = 1 - F = " + r);
        System.out.println();

        // Equivalent form: e vs (pi+1)/(pi-phi)
        BigDecimal piPlusOne = pi.add(BigDecimal.ONE, MC);
        BigDecimal piMinusPhi = pi.subtract(phi, MC);
        BigDecimal eEquiv = piPlusOne.divide(piMinusPhi, MC);
        BigDecimal delta = eEquiv.subtract(e, MC);

        System.out.println("--- Equivalent Form ---");
        System.out.println("(pi+1)/(pi-phi) = " + eEquiv);
        System.out.println("e               = " + e);
        System.out.println("delta           = " + delta);
        System.out.println();

        // Quotient and Ganzheitszahl
        BigDecimal Q = aPlusE.divide(aDiffP, MC);
        BigDecimal G = Q.add(BigDecimal.ONE, MC);
        System.out.println("--- Quotient & Ganzheitszahl ---");
        System.out.println("Q = (a+e)/(a-p) = " + Q);
        System.out.println("G = Q + 1       = " + G);
        System.out.println();

        // Search for integer relations: r = (a*e + b*pi + c*phi + d) / N
        System.out.println("--- Integer Relation Search ---");
        System.out.println("Searching for r in terms of e, pi, phi...\n");
        searchIntegerRelations(r, e, pi, phi);

        // Test ratios r/known_constants
        System.out.println("\n--- Ratio Analysis ---");
        testRatio("r / e", r, e);
        testRatio("r / pi", r, pi);
        testRatio("r / phi", r, phi);
        testRatio("r / (e-pi)", r, e.subtract(pi, MC));
        testRatio("r / (pi-phi)", r, piMinusPhi);
        testRatio("r / (e-phi)", r, e.subtract(phi, MC));
        testRatio("r / (1/phi - 1/pi)", r, aDiffP);
        testRatio("r / (e*pi)", r, e.multiply(pi, MC));
        testRatio("r / (e*phi)", r, e.multiply(phi, MC));
        testRatio("r / (pi*phi)", r, pi.multiply(phi, MC));
        testRatio("r * phi * pi", r.multiply(phi, MC).multiply(pi, MC));
        testRatio("r * e * pi", r.multiply(e, MC).multiply(pi, MC));
        testRatio("r * phi^2 * pi", r.multiply(phi.pow(2, MC), MC).multiply(pi, MC));

        // Powers of r
        System.out.println("\n--- Powers and Roots ---");
        testRatio("1/r", BigDecimal.ONE.divide(r, MC));
        testRatio("sqrt(r)", sqrt(r, MC));
        testRatio("r^2", r.multiply(r, MC));

        // Test if r is related to known expressions
        System.out.println("\n--- Testing r against known expressions ---");
        BigDecimal[] tests = {
            e.subtract(pi, MC).pow(2, MC),                              // (e-pi)^2
            BigDecimal.ONE.divide(e.multiply(pi, MC).multiply(phi, MC), MC), // 1/(e*pi*phi)
            pi.subtract(phi, MC).subtract(BigDecimal.ONE, MC),          // pi - phi - 1
            invPhi.subtract(invPi, MC).pow(2, MC),                      // (1/phi - 1/pi)^2
            e.subtract(eEquiv, MC).abs(),                               // |e - (pi+1)/(pi-phi)|
        };
        String[] testNames = {
            "(e-pi)^2", "1/(e*pi*phi)", "pi-phi-1",
            "(1/phi - 1/pi)^2", "|e - (pi+1)/(pi-phi)|"
        };
        for (int i = 0; i < tests.length; i++) {
            if (tests[i].signum() != 0) {
                BigDecimal ratio = r.divide(tests[i], MC);
                System.out.printf("  r / %-25s = %s%n", testNames[i], ratio.toPlainString().substring(0, Math.min(50, ratio.toPlainString().length())));
            }
        }

        // Family test: for which c is (1/phi - 1/pi)(1/phi + c) exactly 1?
        System.out.println("\n--- Family Test ---");
        System.out.println("For F=1 exactly, c must be: " + eEquiv);
        System.out.println("e is:                       " + e);
        System.out.println("c_exact - e = " + delta);
        System.out.println();

        // Test nearby constants
        System.out.println("Testing other constants near e:");
        double[] nearE = {2.718, 2.7183, Math.sqrt(2) + 1, 1 + Math.sqrt(3)};
        String[] nearNames = {"2.718", "2.7183", "1+sqrt(2)", "1+sqrt(3)"};
        for (int i = 0; i < nearE.length; i++) {
            BigDecimal c = new BigDecimal(nearE[i], MC);
            BigDecimal fTest = aDiffP.multiply(invPhi.add(c, MC), MC);
            BigDecimal rTest = BigDecimal.ONE.subtract(fTest, MC);
            System.out.printf("  c = %-12s -> F = %s, r = %s%n",
                nearNames[i],
                fTest.toPlainString().substring(0, Math.min(25, fTest.toPlainString().length())),
                rTest.toPlainString().substring(0, Math.min(25, rTest.toPlainString().length())));
        }

        // Continued fraction of (pi+1)/(pi-phi)
        System.out.println("\n--- Continued Fraction of (pi+1)/(pi-phi) ---");
        computeContinuedFraction(eEquiv, 20);

        System.out.println("\n--- Continued Fraction of e ---");
        computeContinuedFraction(e, 20);

        System.out.println("\n--- Continued Fraction of r ---");
        computeContinuedFraction(r, 25);

        System.out.println("\n=============================================================");
        System.out.println("  Analysis Complete");
        System.out.println("=============================================================");
    }

    /**
     * Searches for simple integer relations: n1*r + n2*e + n3*pi + n4*phi + n5 = 0
     * with small coefficients.
     */
    private static void searchIntegerRelations(BigDecimal r, BigDecimal e, BigDecimal pi, BigDecimal phi) {
        BigDecimal bestResidual = new BigDecimal("1");
        String bestRelation = "";
        int range = 30;

        BigDecimal[] constants = {r, e, pi, phi, BigDecimal.ONE};
        String[] names = {"r", "e", "pi", "phi", "1"};

        // Search pairs: r = (a * const_i) / b
        for (int ci = 1; ci < constants.length; ci++) {
            for (int a = -range; a <= range; a++) {
                if (a == 0) continue;
                for (int b = 1; b <= range; b++) {
                    BigDecimal candidate = constants[ci].multiply(new BigDecimal(a), MC)
                        .divide(new BigDecimal(b), MC);
                    BigDecimal diff = r.subtract(candidate, MC).abs();
                    if (diff.compareTo(bestResidual) < 0 && diff.compareTo(r.abs().multiply(new BigDecimal("0.01"), MC)) < 0) {
                        bestResidual = diff;
                        bestRelation = String.format("r = %d*%s/%d (error: %s)", a, names[ci], b, diff.toPlainString().substring(0, Math.min(30, diff.toPlainString().length())));
                    }
                }
            }
        }

        // Search triples: r = (a * const_i + b * const_j) / c
        for (int ci = 1; ci < constants.length; ci++) {
            for (int cj = ci; cj < constants.length; cj++) {
                for (int a = -20; a <= 20; a++) {
                    for (int b = -20; b <= 20; b++) {
                        if (a == 0 && b == 0) continue;
                        for (int d = 1; d <= 20; d++) {
                            BigDecimal candidate = constants[ci].multiply(new BigDecimal(a), MC)
                                .add(constants[cj].multiply(new BigDecimal(b), MC), MC)
                                .divide(new BigDecimal(d), MC);
                            BigDecimal diff = r.subtract(candidate, MC).abs();
                            if (diff.compareTo(bestResidual) < 0 && diff.signum() >= 0) {
                                bestResidual = diff;
                                bestRelation = String.format("r = (%d*%s + %d*%s)/%d (error: %s)",
                                    a, names[ci], b, names[cj], d,
                                    diff.toPlainString().substring(0, Math.min(30, diff.toPlainString().length())));
                            }
                        }
                    }
                }
            }
        }

        if (!bestRelation.isEmpty()) {
            System.out.println("Best relation found:");
            System.out.println("  " + bestRelation);
        } else {
            System.out.println("No simple integer relation found.");
        }
    }

    /**
     * Prints a ratio and checks if it is close to a simple fraction.
     */
    private static void testRatio(String name, BigDecimal ratio) {
        String val = ratio.toPlainString();
        System.out.printf("  %-25s = %s%n", name, val.substring(0, Math.min(50, val.length())));
    }

    private static void testRatio(String name, BigDecimal r, BigDecimal divisor) {
        if (divisor.signum() == 0) return;
        BigDecimal ratio = r.divide(divisor, MC);
        testRatio(name, ratio);
    }

    /**
     * Computes the continued fraction representation of a number.
     *
     * @param x     the number
     * @param terms number of terms to compute
     */
    private static void computeContinuedFraction(BigDecimal x, int terms) {
        StringBuilder sb = new StringBuilder("[");
        BigDecimal remaining = x;
        for (int i = 0; i < terms; i++) {
            BigDecimal floor = remaining.setScale(0, RoundingMode.FLOOR);
            if (i > 0) sb.append(", ");
            sb.append(floor.toBigInteger());
            remaining = remaining.subtract(floor, MC);
            if (remaining.compareTo(new BigDecimal("1E-80")) < 0) break;
            remaining = BigDecimal.ONE.divide(remaining, MC);
        }
        sb.append(", ...]");
        System.out.println("  " + sb);
    }

    /**
     * Computes phi = (1 + sqrt(5)) / 2 to the given precision.
     *
     * @param mc the math context
     * @return phi
     */
    private static BigDecimal computePhi(MathContext mc) {
        BigDecimal sqrt5 = sqrt(new BigDecimal("5"), mc);
        return BigDecimal.ONE.add(sqrt5, mc).divide(new BigDecimal("2"), mc);
    }

    /**
     * Computes e using the Taylor series sum(1/n!) to the given precision.
     *
     * @param mc the math context
     * @return Euler's number e
     */
    private static BigDecimal computeE(MathContext mc) {
        BigDecimal e = BigDecimal.ZERO;
        BigDecimal factorial = BigDecimal.ONE;
        for (int i = 0; i < 200; i++) {
            if (i > 0) factorial = factorial.multiply(new BigDecimal(i), mc);
            e = e.add(BigDecimal.ONE.divide(factorial, mc), mc);
        }
        return e;
    }

    /**
     * Computes pi using the Machin formula:
     * pi/4 = 4*arctan(1/5) - arctan(1/239)
     *
     * @param mc the math context
     * @return pi
     */
    private static BigDecimal computePi(MathContext mc) {
        BigDecimal pi4 = arctan(BigDecimal.ONE.divide(new BigDecimal("5"), mc), mc)
            .multiply(new BigDecimal("4"), mc)
            .subtract(arctan(BigDecimal.ONE.divide(new BigDecimal("239"), mc), mc), mc);
        return pi4.multiply(new BigDecimal("4"), mc);
    }

    /**
     * Computes arctan(x) using the Taylor series.
     *
     * @param x  the argument (|x| must be less than 1)
     * @param mc the math context
     * @return arctan(x)
     */
    private static BigDecimal arctan(BigDecimal x, MathContext mc) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal xPow = x;
        BigDecimal x2 = x.multiply(x, mc);
        for (int i = 0; i < 300; i++) {
            int n = 2 * i + 1;
            BigDecimal term = xPow.divide(new BigDecimal(n), mc);
            if (i % 2 == 0) result = result.add(term, mc);
            else result = result.subtract(term, mc);
            xPow = xPow.multiply(x2, mc);
            if (term.abs().compareTo(new BigDecimal("1E-" + (mc.getPrecision() + 10))) < 0) break;
        }
        return result;
    }

    /**
     * Computes the square root of a BigDecimal using Newton's method.
     *
     * @param x  the value
     * @param mc the math context
     * @return sqrt(x)
     */
    private static BigDecimal sqrt(BigDecimal x, MathContext mc) {
        if (x.signum() == 0) return BigDecimal.ZERO;
        BigDecimal two = new BigDecimal("2");
        BigDecimal guess = new BigDecimal(Math.sqrt(x.doubleValue()), mc);
        for (int i = 0; i < 100; i++) {
            BigDecimal prev = guess;
            guess = x.divide(guess, mc).add(guess, mc).divide(two, mc);
            if (guess.subtract(prev, mc).abs().compareTo(new BigDecimal("1E-" + (mc.getPrecision() + 5))) < 0) break;
        }
        return guess;
    }
}
