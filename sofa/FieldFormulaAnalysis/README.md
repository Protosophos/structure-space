# Field Formula Analysis

Analysis tools for the ProtoSophos "Field Formula" - a near-unity relation between phi, pi, and e.

## Background

The Field Formula proposes:

```
F = (1/phi - 1/pi) * (1/phi + e) = 0.99997426...
```

This is remarkably close to 1. The residual r = 1 - F is approximately 2.574 * 10^-5.

## Algebraic Simplification

Through algebraic expansion, the condition F = 1 is equivalent to:

```
e = (pi + 1) / (pi - phi)
```

This simplification reveals that the entire "field formula" reduces to the question:
is e equal to (pi + 1) / (pi - phi)?

Numerically:
- (pi + 1) / (pi - phi) = 2.71836...
- e = 2.71828...

They are close but not equal.

## Tools

### FieldFormulaAnalysis.java

Computes the field formula to 100+ digit precision and searches for integer relations
in the residual r. Performs:

- High-precision computation of F, r, Q, G
- Ratio analysis (r divided by combinations of e, pi, phi)
- Continued fraction decomposition of all key values
- Integer relation search (simplified PSLQ approach)
- Family test: checks if other constants besides e produce F close to 1

### ContinuedFractionSearch.java

Systematically tests whether the continued fraction match between e and
(pi+1)/(pi-phi) is unique or common. Generates all expressions of the form
`(a*pi + b*phi + c) / (d*pi + f*phi + g)` with small integer coefficients
and counts how many continued fraction terms they share with e.

## Key Findings

### Continued Fraction Match

```
(pi+1)/(pi-phi) = [2, 1, 2, 1, 1, 4, 2, 2, 1, 47, ...]
e                = [2, 1, 2, 1, 1, 4, 1, 1, 6, 1,  ...]
```

The first 6 terms match. This explains the quality of the approximation.

### Statistical Significance

Out of 22 million tested expressions:

| Matching CF terms | Count |
|-------------------|-------|
| 11                | 2     |
| 10                | 1     |
| 9                 | 2     |
| 8                 | 204   |
| 7                 | 397   |
| 6 (ProtoSophos)   | 629   |
| 5                 | 18819 |

The ProtoSophos formula (6 matching terms) is one of 629 expressions at that level.
There are significantly better matches, notably:

```
5(pi + 1) / (phi + 6) = 2.71828181...  (11 matching terms, 7000x closer to e)
```

### Conclusion

The near-unity property of the field formula is a **numerical coincidence** arising from
the density of rational-like approximations when combining two transcendental constants
with small integer coefficients. The match is not unique to the specific form
`(1/phi - 1/pi)(1/phi + e)`.

## Run

```bash
javac FieldFormulaAnalysis.java && java FieldFormulaAnalysis
```

```bash
javac ContinuedFractionSearch.java && java ContinuedFractionSearch
```
