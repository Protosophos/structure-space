import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

/**
 * Systematically searches for expressions of the form (a*pi + b) / (c*pi + d*phi + f)
 * that share continued fraction terms with e. Determines whether the ProtoSophos
 * relation (pi+1)/(pi-phi) is uniquely close to e or one of many near-matches.
 */
public class ContinuedFractionSearch {

    private static final MathContext MC = new MathContext(80);
    private static final int CF_MATCH_THRESHOLD = 5;
    private static final int COEFF_RANGE = 10;

    /**
     * Entry point. Runs the systematic continued fraction search.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println("  Continued Fraction Search");
        System.out.println("  Finding expressions that match e's continued fraction");
        System.out.println("================================================================\n");

        BigDecimal phi = computePhi(MC);
        BigDecimal pi = computePi(MC);
        BigDecimal e = computeE(MC);

        // e = [2; 1, 2, 1, 1, 4, 1, 1, 6, 1, 1, 8, 1, 1, 10, ...]
        int[] eCF = computeCF(e, 20);
        System.out.print("e's continued fraction: [");
        for (int i = 0; i < 15; i++) {
            if (i > 0) System.out.print(", ");
            System.out.print(eCF[i]);
        }
        System.out.println(", ...]\n");

        // ProtoSophos formula for reference
        BigDecimal proto = pi.add(BigDecimal.ONE, MC).divide(pi.subtract(phi, MC), MC);
        int[] protoCF = computeCF(proto, 20);
        int protoMatch = countCFMatch(eCF, protoCF);
        System.out.println("ProtoSophos: (pi+1)/(pi-phi) = " + proto.toPlainString().substring(0, 30));
        System.out.print("  CF: [");
        for (int i = 0; i < 12; i++) {
            if (i > 0) System.out.print(", ");
            System.out.print(protoCF[i]);
        }
        System.out.println(", ...]");
        System.out.println("  Matching terms with e: " + protoMatch);
        BigDecimal protoDiff = proto.subtract(e, MC).abs();
        System.out.println("  |value - e| = " + protoDiff.toPlainString().substring(0, 20));
        System.out.println();

        // ============================================================
        // Search 1: (a*pi + b) / (c*pi + d*phi + f)
        // ============================================================
        System.out.println("--- Search 1: (a*pi + b) / (c*pi + d*phi + f) ---");
        System.out.println("Coefficient range: -" + COEFF_RANGE + " to " + COEFF_RANGE);
        System.out.println("Searching...\n");

        List<Result> results = new ArrayList<>();
        long searched = 0;

        for (int a = -COEFF_RANGE; a <= COEFF_RANGE; a++) {
            for (int b = -COEFF_RANGE; b <= COEFF_RANGE; b++) {
                BigDecimal numerator = pi.multiply(bd(a), MC).add(bd(b), MC);
                if (numerator.signum() <= 0) continue;

                for (int c = -COEFF_RANGE; c <= COEFF_RANGE; c++) {
                    for (int d = -COEFF_RANGE; d <= COEFF_RANGE; d++) {
                        for (int f = -COEFF_RANGE; f <= COEFF_RANGE; f++) {
                            if (c == 0 && d == 0 && f == 0) continue;

                            BigDecimal denominator = pi.multiply(bd(c), MC)
                                .add(phi.multiply(bd(d), MC), MC)
                                .add(bd(f), MC);

                            if (denominator.signum() <= 0) continue;

                            BigDecimal value = numerator.divide(denominator, MC);
                            searched++;

                            // Quick filter: must be between 2.5 and 3.0
                            if (value.compareTo(bd(2)) < 0 || value.compareTo(bd(4)) > 0) continue;

                            int[] cf = computeCF(value, 15);
                            if (cf[0] != 2) continue;

                            int matchCount = countCFMatch(eCF, cf);
                            if (matchCount >= CF_MATCH_THRESHOLD) {
                                BigDecimal diff = value.subtract(e, MC).abs();
                                results.add(new Result(
                                    String.format("(%d*pi + %d) / (%d*pi + %d*phi + %d)", a, b, c, d, f),
                                    value, cf, matchCount, diff));
                            }
                        }
                    }
                }
            }
        }

        // ============================================================
        // Search 2: (a*phi + b) / (c*phi + d*e + f) matching pi
        // and other permutations with e in the expression
        // ============================================================
        System.out.println("--- Search 2: (a*pi + b*phi + c) / (d*pi + f*phi + g) ---");
        for (int a = -COEFF_RANGE; a <= COEFF_RANGE; a++) {
            for (int b = -COEFF_RANGE; b <= COEFF_RANGE; b++) {
                for (int c2 = -COEFF_RANGE; c2 <= COEFF_RANGE; c2++) {
                    BigDecimal numerator = pi.multiply(bd(a), MC)
                        .add(phi.multiply(bd(b), MC), MC)
                        .add(bd(c2), MC);
                    if (numerator.signum() <= 0) continue;

                    for (int d = -COEFF_RANGE; d <= COEFF_RANGE; d++) {
                        for (int f = -COEFF_RANGE; f <= COEFF_RANGE; f++) {
                            for (int g = -COEFF_RANGE; g <= COEFF_RANGE; g++) {
                                if (d == 0 && f == 0 && g == 0) continue;
                                // Skip if same as search 1 (b2=0)
                                if (b == 0 && f == 0) continue;

                                BigDecimal denominator = pi.multiply(bd(d), MC)
                                    .add(phi.multiply(bd(f), MC), MC)
                                    .add(bd(g), MC);

                                if (denominator.signum() <= 0) continue;

                                BigDecimal value = numerator.divide(denominator, MC);
                                searched++;

                                if (value.compareTo(bd(2)) < 0 || value.compareTo(bd(4)) > 0) continue;

                                int[] cf = computeCF(value, 15);
                                if (cf[0] != 2) continue;

                                int matchCount = countCFMatch(eCF, cf);
                                if (matchCount >= CF_MATCH_THRESHOLD) {
                                    BigDecimal diff = value.subtract(e, MC).abs();
                                    results.add(new Result(
                                        String.format("(%d*pi + %d*phi + %d) / (%d*pi + %d*phi + %d)",
                                            a, b, c2, d, f, g),
                                        value, cf, matchCount, diff));
                                }
                            }
                        }
                    }
                }
            }
        }

        // Sort by match count (descending), then by difference (ascending)
        results.sort((x, y) -> {
            int cmp = Integer.compare(y.matchCount, x.matchCount);
            if (cmp != 0) return cmp;
            return x.diff.compareTo(y.diff);
        });

        // Remove duplicates (same value)
        List<Result> unique = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (Result r : results) {
            String key = r.value.setScale(12, RoundingMode.HALF_UP).toPlainString();
            if (!seen.contains(key)) {
                seen.add(key);
                unique.add(r);
            }
        }

        // Print results
        System.out.println("\n================================================================");
        System.out.printf("  Searched %,d expressions%n", searched);
        System.out.printf("  Found %d with %d+ matching CF terms%n", unique.size(), CF_MATCH_THRESHOLD);
        System.out.println("================================================================\n");

        // Group by match count
        Map<Integer, Integer> matchDist = new TreeMap<>(Collections.reverseOrder());
        for (Result r : unique) {
            matchDist.merge(r.matchCount, 1, Integer::sum);
        }
        System.out.println("Distribution of matches:");
        for (Map.Entry<Integer, Integer> entry : matchDist.entrySet()) {
            System.out.printf("  %2d matching terms: %d expressions%n", entry.getKey(), entry.getValue());
        }

        System.out.println("\n--- Top results (sorted by match count, then closeness to e) ---\n");
        int shown = 0;
        for (Result r : unique) {
            if (shown >= 30) break;
            System.out.printf("  %d terms | |diff| = %s | %s%n",
                r.matchCount,
                r.diff.toPlainString().substring(0, Math.min(18, r.diff.toPlainString().length())),
                r.expression);
            System.out.print("         CF: [");
            for (int i = 0; i < Math.min(12, r.cf.length); i++) {
                if (i > 0) System.out.print(", ");
                System.out.print(r.cf[i]);
            }
            System.out.println("]");
            shown++;
        }

        // Final verdict
        System.out.println("\n================================================================");
        System.out.println("  VERDICT");
        System.out.println("================================================================");

        int maxMatch = unique.isEmpty() ? 0 : unique.get(0).matchCount;
        long countAtMax = unique.stream().filter(r -> r.matchCount == maxMatch).count();

        if (maxMatch <= protoMatch) {
            System.out.printf("%n  ProtoSophos formula matches %d CF terms.%n", protoMatch);
            System.out.printf("  Out of %,d expressions tested:%n", searched);
            System.out.printf("  - %d expressions match %d+ terms%n", unique.size(), CF_MATCH_THRESHOLD);
            System.out.printf("  - %d expressions match the maximum of %d terms%n", countAtMax, maxMatch);

            if (countAtMax <= 3) {
                System.out.println("\n  -> The ProtoSophos formula is EXCEPTIONALLY RARE.");
                System.out.println("     Very few expressions achieve this match quality.");
            } else if (countAtMax <= 20) {
                System.out.println("\n  -> The ProtoSophos formula is UNCOMMON but not unique.");
            } else {
                System.out.println("\n  -> Many expressions achieve similar match quality.");
                System.out.println("     The match is likely COINCIDENTAL.");
            }
        }
        System.out.println();
    }

    /**
     * Computes continued fraction terms for a BigDecimal value.
     *
     * @param x     the value
     * @param terms number of terms
     * @return array of continued fraction coefficients
     */
    private static int[] computeCF(BigDecimal x, int terms) {
        int[] result = new int[terms];
        BigDecimal remaining = x;
        for (int i = 0; i < terms; i++) {
            BigDecimal floor = remaining.setScale(0, RoundingMode.FLOOR);
            result[i] = floor.intValue();
            remaining = remaining.subtract(floor, MC);
            if (remaining.compareTo(new BigDecimal("1E-60")) < 0) {
                for (int j = i + 1; j < terms; j++) result[j] = -1;
                break;
            }
            remaining = BigDecimal.ONE.divide(remaining, MC);
        }
        return result;
    }

    /**
     * Counts how many continued fraction terms match between two sequences.
     *
     * @param a first CF sequence
     * @param b second CF sequence
     * @return number of matching leading terms
     */
    private static int countCFMatch(int[] a, int[] b) {
        int count = 0;
        for (int i = 0; i < Math.min(a.length, b.length); i++) {
            if (a[i] == b[i] && a[i] >= 0 && b[i] >= 0) count++;
            else break;
        }
        return count;
    }

    private static BigDecimal bd(int val) {
        return new BigDecimal(val);
    }

    private static BigDecimal computePhi(MathContext mc) {
        BigDecimal sqrt5 = sqrt(new BigDecimal("5"), mc);
        return BigDecimal.ONE.add(sqrt5, mc).divide(new BigDecimal("2"), mc);
    }

    private static BigDecimal computeE(MathContext mc) {
        BigDecimal e = BigDecimal.ZERO;
        BigDecimal factorial = BigDecimal.ONE;
        for (int i = 0; i < 150; i++) {
            if (i > 0) factorial = factorial.multiply(new BigDecimal(i), mc);
            e = e.add(BigDecimal.ONE.divide(factorial, mc), mc);
        }
        return e;
    }

    private static BigDecimal computePi(MathContext mc) {
        BigDecimal pi4 = arctan(BigDecimal.ONE.divide(new BigDecimal("5"), mc), mc)
            .multiply(new BigDecimal("4"), mc)
            .subtract(arctan(BigDecimal.ONE.divide(new BigDecimal("239"), mc), mc), mc);
        return pi4.multiply(new BigDecimal("4"), mc);
    }

    private static BigDecimal arctan(BigDecimal x, MathContext mc) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal xPow = x;
        BigDecimal x2 = x.multiply(x, mc);
        for (int i = 0; i < 250; i++) {
            int n = 2 * i + 1;
            BigDecimal term = xPow.divide(new BigDecimal(n), mc);
            if (i % 2 == 0) result = result.add(term, mc);
            else result = result.subtract(term, mc);
            xPow = xPow.multiply(x2, mc);
            if (term.abs().compareTo(new BigDecimal("1E-" + (mc.getPrecision() + 5))) < 0) break;
        }
        return result;
    }

    private static BigDecimal sqrt(BigDecimal x, MathContext mc) {
        BigDecimal two = new BigDecimal("2");
        BigDecimal guess = new BigDecimal(Math.sqrt(x.doubleValue()), mc);
        for (int i = 0; i < 80; i++) {
            BigDecimal prev = guess;
            guess = x.divide(guess, mc).add(guess, mc).divide(two, mc);
            if (guess.subtract(prev, mc).abs().compareTo(new BigDecimal("1E-" + (mc.getPrecision() + 3))) < 0) break;
        }
        return guess;
    }

    /** Holds a search result. */
    static class Result {
        String expression;
        BigDecimal value;
        int[] cf;
        int matchCount;
        BigDecimal diff;

        Result(String expr, BigDecimal val, int[] cf, int match, BigDecimal diff) {
            this.expression = expr;
            this.value = val;
            this.cf = cf;
            this.matchCount = match;
            this.diff = diff;
        }
    }
}
