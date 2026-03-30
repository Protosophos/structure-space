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

### ExtendedConstantSearch.java

Searches for r in terms of 20 mathematical constants including:
- e, pi, phi (and inverses)
- Euler-Mascheroni gamma, ln(2), ln(3)
- sqrt(2), sqrt(3), sqrt(5)
- pi^2, e^2, phi^2, pi*e, pi*phi, e*phi
- Golden angle, zeta(2), Catalan's constant G

Tests r, r^2, sqrt(r), 1/r, and ln(r) against simple fractions p/q (q <= 1000)
and searches for triple linear relations with integer coefficients in [-20, 20].

### GeometricAnalysis.java

Investigates geometric and structural interpretations:
- Golden angle relationship to r
- Pentagon and decagon geometry ratios
- Elliptic integrals K(k) for k related to phi
- Ramanujan-Heegner near-integer comparison
- Area and arc length interpretations of the formula's factors

### LatticeRelationSearch.java

Implements LLL (Lenstra-Lenstra-Lovasz) lattice basis reduction to find minimal
integer relations. Tests 17 different groups of constants including:
- Basic: {r, e, pi, phi, 1}
- Products: e*pi, e*phi, pi*phi, e*pi*phi
- Powers: e^2, pi^2, phi^2, sqrt(pi), sqrt(e)
- Logs: ln(2), ln(pi), ln(phi)
- Cross-group and component combinations
- F's expanded terms: 1/phi^2, e/phi, 1/(pi*phi), e/pi

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

### LLL Lattice Reduction

17 groups of constants tested. **No integer relations found** below threshold 10^-8.
This means r cannot be expressed as a simple linear combination of known mathematical
constants with small integer coefficients.

### Extended Constant Search (20 constants, PSLQ)

No simple fraction p/q (q <= 1000) matches r. No triple linear relation found among
20 constants (e, pi, phi, gamma, ln(2), ln(3), sqrt(2,3,5), zeta(2), Catalan, etc.).

### Geometric Analysis

- Golden angle: no relationship to r
- Pentagon/decagon geometry: no special ratios
- Elliptic integrals K(k): no connection to r
- Ramanujan-Heegner comparison: r = 2.57 * 10^-5 is orders of magnitude larger
  than true near-integer phenomena (e^(pi*sqrt(163)) misses by only 10^-12)

### Overall Conclusion

| Test                     | Result                                    |
|--------------------------|-------------------------------------------|
| Continued fraction match | 6 terms - 629 other expressions equal      |
| Better match exists      | 5(pi+1)/(phi+6) matches 11 terms          |
| LLL integer relations    | None found                                |
| 20-constant PSLQ         | None found                                |
| Geometric connection     | None found                                |
| Ramanujan comparison     | r is 10^7x larger than real near-integers |

**The residual r has no closed form in known constants.** The field formula is a numerical
coincidence - mathematically interesting as an observation, but without deeper structural
connection between phi, pi, and e.

## Run

```bash
javac FieldFormulaAnalysis.java && java FieldFormulaAnalysis
```

```bash
javac ContinuedFractionSearch.java && java ContinuedFractionSearch
```

```bash
javac ExtendedConstantSearch.java && java ExtendedConstantSearch
```

```bash
javac GeometricAnalysis.java && java GeometricAnalysis
```

```bash
javac LatticeRelationSearch.java && java LatticeRelationSearch
```
