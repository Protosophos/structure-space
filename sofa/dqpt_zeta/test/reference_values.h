#ifndef REFERENCE_VALUES_H
#define REFERENCE_VALUES_H

#include "../src/dirichlet.h"

/**
 * High-precision reference values computed with mpmath (50-digit precision).
 * Paper Section 4.4.1.
 */

// eta(s) at test points (Paper Section 4.4.1)
// Index: 0 = 0.5+14.134725i, 1 = 0.5+21.022040i, 2 = 0.5+50i,
//        3 = 0.3+50i, 4 = 0.7+50i
struct TestPoint {
    double beta;
    double t;
    Complex eta_ref;
};

constexpr TestPoint ETA_TEST_POINTS[] = {
    {0.5, 14.134725, {-1.621225738722521040e-08, -2.663504932664623123e-07}},
    {0.5, 21.022040, {-3.708005920248203832e-07,  7.543805983317244211e-07}},
    {0.5, 50.0,      {-1.500664161743687497e-01,  8.077915411705268722e-01}},
    {0.3, 50.0,      {-1.201700733151715106e+00,  8.670246715197147402e-01}},
    {0.7, 50.0,      { 4.544989789631222465e-01,  6.968282629989208665e-01}},
};
constexpr int NUM_ETA_TEST_POINTS = 5;

// theta(t) reference values (Paper Section 4.4.1)
struct ThetaTestPoint {
    double t;
    double theta_ref;
};

constexpr ThetaTestPoint THETA_TEST_POINTS[] = {
    {14.134725, -1.728670304117276624e+00},
    {21.022040,  1.791371484241375489e+00},
    {100.0,      8.797216523178721559e+01},
    {1000.0,     2.034546428038031536e+03},
};
constexpr int NUM_THETA_TEST_POINTS = 4;

// First 100 nontrivial zeta zeros (imaginary parts)
// Source: LMFDB / Odlyzko. Paper Section 4.4.4.
// Only first 10 shown here; full list in data/zeros_known.txt.
constexpr double ZETA_ZEROS_10[] = {
    1.413472514173469463e+01,
    2.102203963877155601e+01,
    2.501085758014568938e+01,
    3.042487612585951240e+01,
    3.293506158773919168e+01,
    3.758617815882567470e+01,
    4.091871901214749840e+01,
    4.332707328091500187e+01,
    4.800515088116716100e+01,
    4.977383247767230046e+01,
};
constexpr int NUM_ZETA_ZEROS_10 = 10;

#endif // REFERENCE_VALUES_H
