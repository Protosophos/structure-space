#ifndef CONFIG_H
#define CONFIG_H

#include <cmath>

/**
 * Compile-time constants derived from Paper Sections 4-5.
 */

// Truncation parameters (Paper Section 4.3)
constexpr int N_DEFAULT = 10000;
constexpr int N_SCAN    = 1000;
constexpr int N_REFINE  = 100000;

// Acceleration parameters
constexpr int BORWEIN_N_MIN = 40; // minimum Borwein terms (Paper Section 3.1)
constexpr int EM_TERMS  = 5;    // Euler-Maclaurin correction terms

// Zero detection tolerances (Paper Section 4.3)
constexpr double EPSILON_COARSE = 1e-6;
constexpr double EPSILON_FINE   = 1e-12;
constexpr double EPSILON_FLAG   = 1e-3;   // adaptive refinement flagging

// Underflow guard for log|L| (Paper Eq. 5.5)
constexpr double UNDERFLOW_GUARD = 1e-300;

// Beta grid (Paper Section 4.3)
constexpr double BETA_MIN     = 0.1;
constexpr double BETA_MAX     = 0.9;
constexpr double BETA_EXCL_LO = 0.4975;
constexpr double BETA_EXCL_HI = 0.5025;
constexpr double DBETA_COARSE = 0.005;
constexpr double DBETA_FINE   = 0.001;

// Time grid (Paper Section 4.3)
constexpr double DT_COARSE = 0.1;
constexpr double DT_FINE   = 0.01;

// Scan range (Paper Section 4.3)
constexpr double T_MAX      = 1000.0;
constexpr double T_MAX_FP64     = 1e7;   // FP64 precision wall (Paper Section 5.2)
constexpr double T_RS_THRESHOLD = 200.0; // RS formula reliable for t >= 200 (Gabcke)

// Bisection (Paper Section 5.4)
constexpr int BISECTION_ITERS = 30;   // position error ~1e-10

// Mathematical constants
constexpr double PI = 3.14159265358979323846;
constexpr double TWO_PI = 2.0 * PI;

#endif // CONFIG_H
