import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Investigates geometric and structural interpretations of the ProtoSophos field formula:
 * F = (1/phi - 1/pi)(1/phi + e) ~= 1
 *
 * <p>Sections covered:
 * <ol>
 *   <li>Golden Angle Analysis</li>
 *   <li>Pentagon / Decagon Geometry</li>
 *   <li>Elliptic / Circular Integrals via AGM</li>
 *   <li>Ramanujan-style Heegner number check</li>
 *   <li>Area / Arc interpretations of the factors</li>
 * </ol>
 *
 * <p>All fundamental constants are computed from first principles:
 * pi via the Machin formula, e via Taylor series, sqrt via Newton's method.
 */
public class GeometricAnalysis {

    /** Working precision: 80 significant digits. */
    private static final MathContext MC = new MathContext(80);

    /** Threshold for convergence checks: 1e-75 */
    private static final BigDecimal CONVERGENCE_THRESHOLD = new BigDecimal("1E-75");

    // -------------------------------------------------------------------------
    // Entry point
    // -------------------------------------------------------------------------

    /**
     * Entry point. Computes all constants and runs every analysis section.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {

        // ------------------------------------------------------------------
        // Compute fundamental constants
        // ------------------------------------------------------------------
        BigDecimal pi  = computePi(MC);
        BigDecimal e   = computeE(MC);
        BigDecimal phi = computePhi(MC);

        BigDecimal invPhi = BigDecimal.ONE.divide(phi, MC);
        BigDecimal invPi  = BigDecimal.ONE.divide(pi, MC);

        // Field formula components
        BigDecimal factorA = invPhi.subtract(invPi, MC);   // 1/phi - 1/pi  ~= 0.2997
        BigDecimal factorB = invPhi.add(e, MC);            // 1/phi + e      ~= 3.3363
        BigDecimal F       = factorA.multiply(factorB, MC);
        BigDecimal r       = BigDecimal.ONE.subtract(F, MC);

        printHeader("ProtoSophos Field Formula - Geometric Analysis");
        System.out.println("  F = (1/phi - 1/pi)(1/phi + e)");
        System.out.println("  F = " + fmt(F));
        System.out.println("  r = 1 - F = " + fmt(r));
        System.out.println("  1/phi - 1/pi (factor A) = " + fmt(factorA));
        System.out.println("  1/phi + e   (factor B) = " + fmt(factorB));
        System.out.println();

        // ------------------------------------------------------------------
        // Run sections
        // ------------------------------------------------------------------
        goldenAngleAnalysis(r, phi, pi);
        pentagonDecagonGeometry(phi, pi, F, r);
        ellipticIntegralAnalysis(phi, pi, r, F);
        ramanujanHeegnerCheck(pi, r);
        areaArcInterpretations(factorA, factorB, F, r, phi, pi, e);

        printHeader("Analysis Complete");
    }

    // =========================================================================
    // SECTION 1 - Golden Angle Analysis
    // =========================================================================

    /**
     * Analyzes how the residual r relates to the golden angle.
     *
     * <p>The golden angle is defined as 2*pi / phi^2 ~= 137.508 degrees.
     * Several combinations involving r and the golden angle are tested.
     *
     * @param r   the residual 1 - F
     * @param phi the golden ratio
     * @param pi  the circle constant
     */
    private static void goldenAngleAnalysis(BigDecimal r, BigDecimal phi, BigDecimal pi) {
        printHeader("Section 1 - Golden Angle Analysis");

        BigDecimal two      = new BigDecimal("2");
        BigDecimal bd360    = new BigDecimal("360");
        BigDecimal phi2     = phi.multiply(phi, MC);

        // golden angle in radians: 2*pi / phi^2
        BigDecimal goldenRad = two.multiply(pi, MC).divide(phi2, MC);
        // golden angle in degrees
        BigDecimal goldenDeg = goldenRad.multiply(bd360, MC).divide(two.multiply(pi, MC), MC);

        System.out.println("  phi^2             = " + fmt(phi2));
        System.out.println("  Golden angle (rad)= " + fmt(goldenRad));
        System.out.println("  Golden angle (deg)= " + fmt(goldenDeg));
        System.out.println("  Expected ~137.5077640...");
        System.out.println();

        // Tests involving r
        System.out.println("  -- Tests on residual r --");
        System.out.println("  r                 = " + fmt(r));

        BigDecimal rTimes360   = r.multiply(bd360, MC);
        BigDecimal rTimes2pi   = r.multiply(two.multiply(pi, MC), MC);
        BigDecimal rTimesPhi2  = r.multiply(phi2, MC);
        BigDecimal rDivGolden  = r.divide(goldenRad, MC);

        System.out.println("  r * 360           = " + fmt(rTimes360));
        System.out.println("  r * 2*pi          = " + fmt(rTimes2pi));
        System.out.println("  r * phi^2         = " + fmt(rTimesPhi2));
        System.out.println("  r / golden_rad    = " + fmt(rDivGolden));

        // Is r a fraction of the golden angle?
        System.out.println();
        System.out.println("  -- Is r a simple fraction of the golden angle? --");
        for (int denom = 1; denom <= 20; denom++) {
            BigDecimal fraction = goldenRad.divide(new BigDecimal(denom), MC);
            BigDecimal diff = r.subtract(fraction, MC).abs();
            if (diff.compareTo(r.abs().multiply(new BigDecimal("0.005"), MC)) < 0) {
                System.out.printf("  r ~= golden_rad / %d  (diff = %s)%n", denom, fmt(diff));
            }
        }
        for (int numer = 1; numer <= 10; numer++) {
            for (int denom = 1; denom <= 20; denom++) {
                BigDecimal fraction = goldenRad.multiply(new BigDecimal(numer), MC)
                                               .divide(new BigDecimal(denom), MC);
                BigDecimal diff = r.subtract(fraction, MC).abs();
                if (diff.compareTo(r.abs().multiply(new BigDecimal("0.001"), MC)) < 0) {
                    System.out.printf("  r ~= %d/%-3d * golden_rad  (diff = %s)%n",
                                      numer, denom, fmt(diff));
                }
            }
        }

        // Nearest integer to r*360 and r*2pi
        System.out.println();
        System.out.println("  -- Nearest integer closeness --");
        BigDecimal nearInt360 = rTimes360.subtract(
                rTimes360.setScale(0, RoundingMode.HALF_UP), MC).abs();
        BigDecimal nearInt2pi = rTimes2pi.subtract(
                rTimes2pi.setScale(0, RoundingMode.HALF_UP), MC).abs();
        System.out.println("  |r*360 - round(r*360)| = " + fmt(nearInt360));
        System.out.println("  |r*2pi - round(r*2pi)| = " + fmt(nearInt2pi));
        System.out.println();
    }

    // =========================================================================
    // SECTION 2 - Pentagon / Decagon Geometry
    // =========================================================================

    /**
     * Explores the connection between phi and pentagon geometry.
     *
     * <p>Uses the identity phi = 2*cos(pi/5). Computes the area and perimeter of
     * a regular pentagon with unit side length and checks if any ratio equals F
     * or relates to r.
     *
     * @param phi the golden ratio
     * @param pi  the circle constant
     * @param F   the field formula value
     * @param r   the residual
     */
    private static void pentagonDecagonGeometry(BigDecimal phi, BigDecimal pi,
                                                BigDecimal F, BigDecimal r) {
        printHeader("Section 2 - Pentagon / Decagon Geometry");

        // Verify phi = 2*cos(pi/5) via known algebraic form:
        // cos(pi/5) = (1+sqrt(5))/4 = phi/2
        BigDecimal cosPi5 = phi.divide(new BigDecimal("2"), MC);
        BigDecimal phiCheck = cosPi5.multiply(new BigDecimal("2"), MC);
        System.out.println("  phi             = " + fmt(phi));
        System.out.println("  2*cos(pi/5)     = " + fmt(phiCheck));
        System.out.println("  Match: " + (phi.subtract(phiCheck, MC).abs()
                .compareTo(CONVERGENCE_THRESHOLD) < 0 ? "YES" : "NO"));
        System.out.println();

        // Regular pentagon with side s = 1
        // Perimeter P = 5
        BigDecimal P = new BigDecimal("5");
        // Area A = (s^2 / 4) * sqrt(5*(5+2*sqrt(5)))
        // sqrt(5) is available via Newton
        BigDecimal sqrt5 = sqrt(new BigDecimal("5"), MC);
        BigDecimal inner  = new BigDecimal("5").multiply(
                new BigDecimal("5").add(new BigDecimal("2").multiply(sqrt5, MC), MC), MC);
        BigDecimal A = sqrt(inner, MC).divide(new BigDecimal("4"), MC);
        // Circumradius R = s / (2*sin(pi/5))
        // sin(pi/5) = sqrt(10 - 2*sqrt(5)) / 4
        BigDecimal sinPi5 = sqrt(new BigDecimal("10").subtract(
                new BigDecimal("2").multiply(sqrt5, MC), MC), MC)
                .divide(new BigDecimal("4"), MC);
        BigDecimal R = BigDecimal.ONE.divide(sinPi5.multiply(new BigDecimal("2"), MC), MC);
        // Inradius r_in = (1/(2*tan(pi/5))) = cos(pi/5)/sin(pi/5)/2...
        // tan(pi/5) = sin(pi/5)/cos(pi/5) ; cos(pi/5) = phi/2
        BigDecimal tanPi5 = sinPi5.divide(cosPi5, MC);
        BigDecimal r_in   = BigDecimal.ONE.divide(
                new BigDecimal("2").multiply(tanPi5, MC), MC);
        // Diagonal d = phi * s = phi
        BigDecimal diagonal = phi;

        System.out.println("  Regular pentagon (side = 1):");
        System.out.println("  Perimeter         = " + fmt(P));
        System.out.println("  Area              = " + fmt(A));
        System.out.println("  Circumradius R    = " + fmt(R));
        System.out.println("  Inradius r_in     = " + fmt(r_in));
        System.out.println("  Diagonal d        = " + fmt(diagonal));
        System.out.println();

        // Check ratios against F and r
        System.out.println("  -- Ratio checks against F and r --");
        BigDecimal[] geomVals = { P, A, R, r_in, diagonal,
                                  A.divide(P, MC), R.divide(r_in, MC),
                                  diagonal.divide(P, MC) };
        String[] geomNames = { "P", "A", "R", "r_in", "diagonal",
                                "A/P", "R/r_in", "diagonal/P" };
        for (int i = 0; i < geomVals.length; i++) {
            BigDecimal ratioF = F.divide(geomVals[i], MC);
            BigDecimal ratioR = r.divide(geomVals[i], MC);
            System.out.printf("  F / %-12s = %s%n", geomNames[i], fmt(ratioF));
            System.out.printf("  r / %-12s = %s%n", geomNames[i], fmt(ratioR));
        }

        // Decagon: regular 10-gon with side 1
        // Area of decagon with side 1 = (5/2)*sqrt(5+2*sqrt(5))
        BigDecimal innerDec = new BigDecimal("5").add(
                new BigDecimal("2").multiply(sqrt5, MC), MC);
        BigDecimal aDec = new BigDecimal("5").multiply(sqrt(innerDec, MC), MC)
                         .divide(new BigDecimal("2"), MC);
        System.out.println();
        System.out.println("  Regular decagon (side = 1):");
        System.out.println("  Area              = " + fmt(aDec));
        System.out.printf("  F / decagon_area  = %s%n", fmt(F.divide(aDec, MC)));
        System.out.printf("  r / decagon_area  = %s%n", fmt(r.divide(aDec, MC)));
        System.out.println();
    }

    // =========================================================================
    // SECTION 3 - Elliptic / Circular Integrals via AGM
    // =========================================================================

    /**
     * Computes complete elliptic integrals K(k) using the AGM method for
     * moduli related to phi, and checks whether any K(k) relates to r or F.
     *
     * <p>The complete elliptic integral of the first kind is computed as:
     * K(k) = pi / (2 * AGM(1, sqrt(1 - k^2)))
     *
     * @param phi the golden ratio
     * @param pi  the circle constant
     * @param r   the residual
     * @param F   the field formula value
     */
    private static void ellipticIntegralAnalysis(BigDecimal phi, BigDecimal pi,
                                                 BigDecimal r, BigDecimal F) {
        printHeader("Section 3 - Elliptic / Circular Integrals");

        System.out.println("  K(k) = pi / (2 * AGM(1, sqrt(1 - k^2)))");
        System.out.println();

        BigDecimal invPhi   = BigDecimal.ONE.divide(phi, MC);
        BigDecimal phiMinus1 = phi.subtract(BigDecimal.ONE, MC);  // = 1/phi
        BigDecimal invSqrtPhi = BigDecimal.ONE.divide(sqrt(phi, MC), MC);

        // k values to test
        BigDecimal[] kVals  = { invPhi, phiMinus1, invSqrtPhi,
                                new BigDecimal("0.5"),
                                BigDecimal.ONE.divide(sqrt(new BigDecimal("2"), MC), MC) };
        String[]     kNames = { "1/phi", "phi-1", "1/sqrt(phi)",
                                "1/2", "1/sqrt(2)" };

        for (int i = 0; i < kVals.length; i++) {
            BigDecimal k    = kVals[i];
            BigDecimal k2   = k.multiply(k, MC);
            BigDecimal comp = BigDecimal.ONE.subtract(k2, MC); // 1 - k^2
            if (comp.signum() <= 0) {
                System.out.printf("  K(%s): k^2 >= 1, skipped%n", kNames[i]);
                continue;
            }
            BigDecimal kPrime = sqrt(comp, MC); // sqrt(1 - k^2)
            BigDecimal agm    = agm(BigDecimal.ONE, kPrime, MC);
            BigDecimal K      = pi.divide(new BigDecimal("2").multiply(agm, MC), MC);

            System.out.printf("  k = %-14s = %s%n", kNames[i], fmt(k));
            System.out.printf("  K(k)              = %s%n", fmt(K));
            System.out.printf("  r / K(k)          = %s%n", fmt(r.divide(K, MC)));
            System.out.printf("  F / K(k)          = %s%n", fmt(F.divide(K, MC)));
            System.out.printf("  K(k) * r          = %s%n", fmt(K.multiply(r, MC)));
            System.out.println();
        }
    }

    // =========================================================================
    // SECTION 4 - Ramanujan / Heegner Number Check
    // =========================================================================

    /**
     * Computes e^(pi*sqrt(n)) for known Heegner numbers and selected others,
     * measuring how close each value is to an integer.
     *
     * <p>The famous Heegner numbers 1, 2, 3, 7, 11, 19, 43, 67, 163 yield
     * values extraordinarily close to integers (Ramanujan's constant for n=163).
     * This section compares those residuals to our r.
     *
     * @param pi the circle constant
     * @param r  the residual 1 - F
     */
    private static void ramanujanHeegnerCheck(BigDecimal pi, BigDecimal r) {
        printHeader("Section 4 - Ramanujan-style Heegner Number Check");

        System.out.println("  Computing e^(pi*sqrt(n)) and distance to nearest integer:");
        System.out.println();

        int[] ns = { 1, 2, 3, 5, 7, 11, 13, 43, 67, 163 };

        BigDecimal absR = r.abs();

        for (int n : ns) {
            BigDecimal sqrtN  = sqrt(new BigDecimal(n), MC);
            BigDecimal expon  = pi.multiply(sqrtN, MC);
            BigDecimal val    = exp(expon, MC);

            // Distance to nearest integer
            BigDecimal nearest     = val.setScale(0, RoundingMode.HALF_UP);
            BigDecimal distToInt   = val.subtract(nearest, MC).abs();

            // Ratio to r
            String ratioStr;
            if (distToInt.signum() == 0) {
                ratioStr = "N/A (exact)";
            } else {
                BigDecimal ratio = distToInt.divide(absR, MC);
                ratioStr = fmt(ratio);
            }

            System.out.printf("  n = %3d : e^(pi*sqrt(n)) ~= %s%n",
                              n, val.toPlainString().substring(0,
                                  Math.min(50, val.toPlainString().length())));
            System.out.printf("           nearest int = %s%n", nearest.toPlainString());
            System.out.printf("           dist to int = %s%n",
                              distToInt.toPlainString().substring(0,
                                  Math.min(50, distToInt.toPlainString().length())));
            System.out.printf("           dist / r    = %s%n%n", ratioStr);
        }

        System.out.println("  Our residual r = " + fmt(r));
        System.out.println("  (Compare magnitudes above to r for structural similarity.)");
        System.out.println();
    }

    // =========================================================================
    // SECTION 5 - Area / Arc Interpretations
    // =========================================================================

    /**
     * Explores geometric interpretations of the two factors and their product.
     *
     * <p>Factor A = 1/phi - 1/pi ~= 0.2997 is examined as a possible arc length
     * or normalized area. Factor B = 1/phi + e ~= 3.3363 and the product F are
     * similarly inspected.
     *
     * @param factorA the value (1/phi - 1/pi)
     * @param factorB the value (1/phi + e)
     * @param F       the product F = factorA * factorB
     * @param r       the residual 1 - F
     * @param phi     the golden ratio
     * @param pi      the circle constant
     * @param e       Euler's number
     */
    private static void areaArcInterpretations(BigDecimal factorA, BigDecimal factorB,
                                               BigDecimal F, BigDecimal r,
                                               BigDecimal phi, BigDecimal pi,
                                               BigDecimal e) {
        printHeader("Section 5 - Area / Arc Interpretations");

        BigDecimal two  = new BigDecimal("2");
        BigDecimal four = new BigDecimal("4");
        BigDecimal invPhi = BigDecimal.ONE.divide(phi, MC);
        BigDecimal invPi  = BigDecimal.ONE.divide(pi, MC);

        System.out.println("  Factor A = 1/phi - 1/pi = " + fmt(factorA));
        System.out.println("  Factor B = 1/phi + e     = " + fmt(factorB));
        System.out.println("  F = A * B                = " + fmt(F));
        System.out.println("  r = 1 - F                = " + fmt(r));
        System.out.println();

        // ------------------------------------------------------------------
        // Circular arcs: arc length on unit circle = angle in radians
        // Factor A ~= 0.2997 rad -> degrees
        // ------------------------------------------------------------------
        BigDecimal bd360 = new BigDecimal("360");
        BigDecimal aInDeg = factorA.multiply(bd360, MC).divide(
                two.multiply(pi, MC), MC);
        System.out.println("  Circular arc interpretation:");
        System.out.println("  Factor A as angle (deg) = " + fmt(aInDeg));
        System.out.println("  (arc length = Factor A on unit circle)");
        System.out.println();

        // ------------------------------------------------------------------
        // Area of a circular sector: A_sector = (1/2)*r^2*theta
        // If sector area = factorA with r=1: theta = 2*factorA
        // ------------------------------------------------------------------
        BigDecimal thetaSector = two.multiply(factorA, MC);
        System.out.println("  Sector area interpretation:");
        System.out.printf("  If sector area = Factor A on unit circle, theta = 2*A = %s rad%n",
                          fmt(thetaSector));
        System.out.printf("  That angle in degrees = %s%n",
                          fmt(thetaSector.multiply(bd360, MC).divide(
                                  two.multiply(pi, MC), MC)));
        System.out.println();

        // ------------------------------------------------------------------
        // Unit square / rectangle interpretations
        // ------------------------------------------------------------------
        System.out.println("  Rectangle interpretations:");
        System.out.printf("  A * B = F ~= 1 -> rectangle with sides A, B has area F%n");
        System.out.printf("  Deficit from unit area = r = %s%n", fmt(r));
        System.out.printf("  r as fraction of A     = r/A = %s%n",
                          fmt(r.divide(factorA, MC)));
        System.out.printf("  r as fraction of B     = r/B = %s%n",
                          fmt(r.divide(factorB, MC)));
        System.out.println();

        // ------------------------------------------------------------------
        // Annular / ring area: pi*(R^2 - r^2)
        // What radii give area = factorA?
        // ------------------------------------------------------------------
        System.out.println("  Annular area A_ring = pi*(R^2 - r2^2) = Factor A:");
        BigDecimal ringDiff = factorA.divide(pi, MC);  // R^2 - r2^2
        System.out.println("  R^2 - r2^2          = A/pi = " + fmt(ringDiff));
        System.out.println();

        // ------------------------------------------------------------------
        // Known arc lengths and areas for comparison
        // ------------------------------------------------------------------
        System.out.println("  Known geometric quantities for comparison:");
        BigDecimal sqrt5 = sqrt(new BigDecimal("5"), MC);
        // Unit circle arc from 0 to 1/phi radians
        BigDecimal arcUnit = invPhi;  // arc length on unit circle = angle in rad
        // Quarter circle arc = pi/2
        BigDecimal quarterArc = pi.divide(two, MC);
        // Lemniscate constant (approx) ~ 2.622
        // Use known value; we compute via the AGM: varpi = pi / AGM(1, sqrt(2))
        BigDecimal agmSqrt2 = agm(BigDecimal.ONE, sqrt(new BigDecimal("2"), MC), MC);
        BigDecimal varpi = pi.divide(agmSqrt2, MC);  // = 2 * lemniscate constant / 2
        // Actually lemniscate constant = 2*K(1/sqrt(2)) where K is via AGM
        // K(1/sqrt(2)) = pi / (2*AGM(1, 1/sqrt(2)))
        BigDecimal invSqrt2 = BigDecimal.ONE.divide(sqrt(two, MC), MC);
        BigDecimal agmLem   = agm(BigDecimal.ONE, invSqrt2, MC);
        BigDecimal lemnConst = pi.divide(two.multiply(agmLem, MC), MC).multiply(two, MC);

        System.out.printf("  1/phi (arc on unit circle) = %s%n", fmt(arcUnit));
        System.out.printf("  1/pi                       = %s%n", fmt(invPi));
        System.out.printf("  pi/2 (quarter circle arc)  = %s%n", fmt(quarterArc));
        System.out.printf("  Lemniscate constant ~= 2.622 = %s%n", fmt(lemnConst));
        System.out.println();

        // How does 1/phi - 1/pi relate to known arcs?
        System.out.println("  Relation checks for Factor A = 1/phi - 1/pi:");
        System.out.printf("  Factor A / (1/phi)    = %s%n",
                          fmt(factorA.divide(invPhi, MC)));
        System.out.printf("  Factor A / (1/pi)     = %s%n",
                          fmt(factorA.divide(invPi, MC)));
        System.out.printf("  Factor A * pi         = %s%n",
                          fmt(factorA.multiply(pi, MC)));
        System.out.printf("  Factor A * phi        = %s%n",
                          fmt(factorA.multiply(phi, MC)));
        System.out.printf("  Factor A * pi * phi   = %s%n",
                          fmt(factorA.multiply(pi, MC).multiply(phi, MC)));
        System.out.println();

        // Factor B interpretations
        System.out.println("  Relation checks for Factor B = 1/phi + e:");
        BigDecimal piOver2 = pi.divide(two, MC);
        System.out.printf("  Factor B / e          = %s%n",
                          fmt(factorB.divide(e, MC)));
        System.out.printf("  Factor B / pi         = %s%n",
                          fmt(factorB.divide(pi, MC)));
        System.out.printf("  Factor B - e          = %s (= 1/phi = %s)%n",
                          fmt(factorB.subtract(e, MC)),
                          fmt(invPhi));
        System.out.printf("  Factor B - pi         = %s%n",
                          fmt(factorB.subtract(pi, MC)));
        System.out.printf("  Factor B / (pi/2+1/phi)= %s%n",
                          fmt(factorB.divide(piOver2.add(invPhi, MC), MC)));
        System.out.println();

        // Product F as area of various shapes
        System.out.println("  F ~= 1 as area:");
        System.out.printf("  F / (pi/4)            = %s  (ratio to unit-circle quarter)%n",
                          fmt(F.divide(pi.divide(four, MC), MC)));
        System.out.printf("  F * pi / 4            = %s  (area of circle radius sqrt(F/pi))%n",
                          fmt(F.multiply(pi, MC).divide(four, MC)));
        BigDecimal radiusForF = sqrt(F.divide(pi, MC), MC);
        System.out.printf("  Circle of area F has radius r = sqrt(F/pi) = %s%n",
                          fmt(radiusForF));
        System.out.println();
    }

    // =========================================================================
    // Mathematical helpers
    // =========================================================================

    /**
     * Computes phi = (1 + sqrt(5)) / 2 to the given precision.
     *
     * @param mc the math context
     * @return the golden ratio phi
     */
    private static BigDecimal computePhi(MathContext mc) {
        BigDecimal sqrt5 = sqrt(new BigDecimal("5"), mc);
        return BigDecimal.ONE.add(sqrt5, mc).divide(new BigDecimal("2"), mc);
    }

    /**
     * Computes pi using the Machin formula:
     * pi/4 = 4*arctan(1/5) - arctan(1/239)
     *
     * @param mc the math context
     * @return pi to the given precision
     */
    private static BigDecimal computePi(MathContext mc) {
        BigDecimal a = arctan(BigDecimal.ONE.divide(new BigDecimal("5"), mc), mc);
        BigDecimal b = arctan(BigDecimal.ONE.divide(new BigDecimal("239"), mc), mc);
        return a.multiply(new BigDecimal("4"), mc)
                .subtract(b, mc)
                .multiply(new BigDecimal("4"), mc);
    }

    /**
     * Computes e = sum_{n=0}^{inf} 1/n! using the Taylor series.
     *
     * @param mc the math context
     * @return Euler's number e to the given precision
     */
    private static BigDecimal computeE(MathContext mc) {
        BigDecimal result    = BigDecimal.ZERO;
        BigDecimal factorial = BigDecimal.ONE;
        for (int n = 0; n < 200; n++) {
            if (n > 0) {
                factorial = factorial.multiply(new BigDecimal(n), mc);
            }
            BigDecimal term = BigDecimal.ONE.divide(factorial, mc);
            result = result.add(term, mc);
            if (term.compareTo(CONVERGENCE_THRESHOLD) < 0) {
                break;
            }
        }
        return result;
    }

    /**
     * Computes exp(x) = sum_{n=0}^{inf} x^n / n! using the Taylor series.
     *
     * <p>For large arguments the series is applied to the fractional part and
     * the integer part is handled by repeated squaring of e.
     *
     * @param x  the exponent
     * @param mc the math context
     * @return e^x
     */
    private static BigDecimal exp(BigDecimal x, MathContext mc) {
        // Split x into integer and fractional parts to keep series convergent
        int intPart = x.setScale(0, RoundingMode.FLOOR).intValueExact();
        BigDecimal frac = x.subtract(new BigDecimal(intPart), mc);

        // Taylor series for e^frac
        BigDecimal result    = BigDecimal.ZERO;
        BigDecimal xPow      = BigDecimal.ONE;
        BigDecimal factorial = BigDecimal.ONE;
        for (int n = 0; n < 300; n++) {
            if (n > 0) {
                xPow      = xPow.multiply(frac, mc);
                factorial = factorial.multiply(new BigDecimal(n), mc);
            }
            BigDecimal term = xPow.divide(factorial, mc);
            result = result.add(term, mc);
            if (n > 5 && term.abs().compareTo(CONVERGENCE_THRESHOLD) < 0) {
                break;
            }
        }

        // Multiply by e^intPart via repeated squaring of e
        BigDecimal eConst = computeE(mc);
        BigDecimal ePow   = power(eConst, intPart, mc);
        return result.multiply(ePow, mc);
    }

    /**
     * Raises a BigDecimal base to a non-negative integer exponent.
     *
     * @param base     the base value
     * @param exponent the non-negative integer exponent
     * @param mc       the math context
     * @return base^exponent
     */
    private static BigDecimal power(BigDecimal base, int exponent, MathContext mc) {
        if (exponent == 0) {
            return BigDecimal.ONE;
        }
        BigDecimal result = BigDecimal.ONE;
        BigDecimal b      = base;
        int        exp    = exponent;
        while (exp > 0) {
            if ((exp & 1) == 1) {
                result = result.multiply(b, mc);
            }
            b   = b.multiply(b, mc);
            exp = exp >> 1;
        }
        return result;
    }

    /**
     * Computes arctan(x) using the Gregory-Leibniz Taylor series:
     * arctan(x) = x - x^3/3 + x^5/5 - ...
     *
     * <p>Converges for |x| <= 1; fastest when |x| is small (e.g., 1/5, 1/239).
     *
     * @param x  the argument (|x| should be well below 1 for fast convergence)
     * @param mc the math context
     * @return arctan(x)
     */
    private static BigDecimal arctan(BigDecimal x, MathContext mc) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal xPow   = x;
        BigDecimal x2     = x.multiply(x, mc);
        for (int i = 0; i < 500; i++) {
            int         n    = 2 * i + 1;
            BigDecimal term  = xPow.divide(new BigDecimal(n), mc);
            if (i % 2 == 0) {
                result = result.add(term, mc);
            } else {
                result = result.subtract(term, mc);
            }
            xPow = xPow.multiply(x2, mc);
            if (term.abs().compareTo(CONVERGENCE_THRESHOLD) < 0) {
                break;
            }
        }
        return result;
    }

    /**
     * Computes the square root of x using Newton's method.
     *
     * @param x  the radicand (must be non-negative)
     * @param mc the math context
     * @return sqrt(x)
     */
    private static BigDecimal sqrt(BigDecimal x, MathContext mc) {
        if (x.signum() == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal two   = new BigDecimal("2");
        BigDecimal guess = new BigDecimal(Math.sqrt(x.doubleValue()), mc);
        for (int i = 0; i < 200; i++) {
            BigDecimal prev = guess;
            guess = x.divide(guess, mc).add(guess, mc).divide(two, mc);
            if (guess.subtract(prev, mc).abs().compareTo(CONVERGENCE_THRESHOLD) < 0) {
                break;
            }
        }
        return guess;
    }

    /**
     * Computes the arithmetic-geometric mean (AGM) of a and b.
     *
     * <p>Iterates: a_{n+1} = (a_n + b_n) / 2, b_{n+1} = sqrt(a_n * b_n)
     * until |a_n - b_n| &lt; convergence threshold.
     *
     * @param a  first value (positive)
     * @param b  second value (positive)
     * @param mc the math context
     * @return AGM(a, b)
     */
    private static BigDecimal agm(BigDecimal a, BigDecimal b, MathContext mc) {
        BigDecimal two = new BigDecimal("2");
        for (int i = 0; i < 200; i++) {
            BigDecimal aNext = a.add(b, mc).divide(two, mc);
            BigDecimal bNext = sqrt(a.multiply(b, mc), mc);
            BigDecimal diff  = aNext.subtract(bNext, mc).abs();
            a = aNext;
            b = bNext;
            if (diff.compareTo(CONVERGENCE_THRESHOLD) < 0) {
                break;
            }
        }
        return a;  // a and b have converged
    }

    // =========================================================================
    // Formatting / printing utilities
    // =========================================================================

    /**
     * Formats a BigDecimal to at most 60 characters of its plain-string form.
     *
     * @param v the value to format
     * @return a truncated plain string representation
     */
    private static String fmt(BigDecimal v) {
        String s = v.toPlainString();
        return s.substring(0, Math.min(60, s.length()));
    }

    /**
     * Prints a section header surrounded by separator lines.
     *
     * @param title the section title
     */
    private static void printHeader(String title) {
        System.out.println("=".repeat(70));
        System.out.println("  " + title);
        System.out.println("=".repeat(70));
    }
}
