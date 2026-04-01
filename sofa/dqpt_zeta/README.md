# DQPT-Zeta

Classical implementation of the Wei et al. (arXiv:2511.11199) DQPT-RH
equivalence framework. Computes dynamical quantum phase transitions
in two quantum systems whose zeros correspond to the nontrivial zeros
of the Riemann zeta function.

See `paper/` for the full theoretical framework document.

## What you can do with this software

### Stage 1: Verify implementation against known zeros

Verify the implementation against all ~10200 known zeta zeros from
the LMFDB/Odlyzko tables. Run this first to validate the installation.

```bash
# Linux/macOS
./build/dqpt_zeta --stage1

# Windows
build\dqpt_zeta.exe --stage1
```

### Stage 2: Find zeta zeros via quantum phase transitions

Detect nontrivial zeros of the Riemann zeta function through the DQPT
framework - a completely different computational pathway than classical
direct zeta evaluation. Uses GPU acceleration automatically when CUDA
is available.

```bash
# Linux/macOS
./build/dqpt_zeta --stage2 --tmax=1000
./build/dqpt_zeta --stage2 --tmax=10000000

# Windows
build\dqpt_zeta.exe --stage2 --tmax=1000
build\dqpt_zeta.exe --stage2 --tmax=10000000
```

### Stage 3: Test the Riemann Hypothesis

Scan the full (beta, t) plane for off-critical-line zeros. If any are
found at beta != 1/2, that would disprove the Riemann Hypothesis.

```bash
# Linux/macOS
./build/dqpt_zeta --stage3 --tmax=1000

# Windows
build\dqpt_zeta.exe --stage3 --tmax=1000
```

### Stage 4: Refine a candidate off-line zero

If Stage 3 flags a candidate at some (beta, t), refine it using 2D
Newton optimization to determine if it is a genuine off-line zero or
a near-miss.

```bash
# Linux/macOS
./build/dqpt_zeta --stage4 --beta=0.52 --t=100.5

# Windows
build\dqpt_zeta.exe --stage4 --beta=0.52 --t=100.5
```

### Generate phase diagrams

Produce a 2D heat map of |L(beta, t)| showing DQPT signal confinement
to the critical line beta = 1/2.

```bash
# Linux/macOS
./build/dqpt_zeta --phase-diagram --tmax=1000 > phase_diagram.csv

# Windows
build\dqpt_zeta.exe --phase-diagram --tmax=1000 > phase_diagram.csv
```

## Prerequisites

### Windows

```
winget install MSYS2.MSYS2
```

Then in a terminal:

```bash
# C++ compiler
pacman -S mingw-w64-ucrt-x86_64-gcc mingw-w64-ucrt-x86_64-make

# CMake
winget install Kitware.CMake

# CUDA Toolkit (for GPU acceleration, NVIDIA GPUs only)
# AMD GPUs are not yet supported.
winget install Nvidia.CUDA

# MiKTeX (for paper compilation only)
winget install MiKTeX.MiKTeX
initexmf --set-config-value="[MPM]AutoInstall=1"

# Python + mpmath (only needed to regenerate reference values in
# test/reference_values.h and data/zeros_known.txt - these are
# already included in the repository, so Python is NOT required
# for building or running the software)
# winget install Python.Python.3
# pip install mpmath
```

Add to PATH: `C:\msys64\ucrt64\bin`, `C:\Program Files\CMake\bin`,
`C:\Program Files\NVIDIA GPU Computing Toolkit\CUDA\vXX.X\bin`.

### Linux (Debian / Ubuntu)

```bash
sudo apt update
sudo apt install g++ cmake make

# CUDA Toolkit (for GPU acceleration, NVIDIA GPUs only)
# AMD GPUs are not yet supported.
# Follow: https://developer.nvidia.com/cuda-downloads
# Select Linux > x86_64 > Ubuntu > deb (network)

```

### Linux (Fedora / RHEL / CentOS)

```bash
sudo dnf install gcc-c++ cmake make

# CUDA Toolkit (for GPU acceleration, NVIDIA GPUs only)
# AMD GPUs are not yet supported.
sudo dnf config-manager --add-repo \
  https://developer.download.nvidia.com/compute/cuda/repos/fedora39/x86_64/cuda-fedora39.repo
sudo dnf install cuda-toolkit
```

### Linux (Arch Linux / Manjaro)

```bash
sudo pacman -S gcc cmake make

# CUDA Toolkit (for GPU acceleration, NVIDIA GPUs only)
# AMD GPUs are not yet supported.
sudo pacman -S cuda
```

### macOS

```bash
brew install cmake gcc
# Note: CUDA is not available on macOS. GPU acceleration is not
# supported on Apple Silicon (M1/M2/M3/M4) or Intel Macs.
# The software runs in CPU-only mode on macOS.
```

## Build

Linux/macOS:

```bash
mkdir -p build && cd build
cmake ..
make
```

Windows (MSYS2):

```bash
mkdir -p build && cd build
cmake -G "MinGW Makefiles" -DCMAKE_CXX_COMPILER=g++ ..
mingw32-make
```

If CUDA is installed, CMake detects it automatically and builds with
GPU acceleration. If not, it builds CPU-only. One binary either way.

### Compiler flags (critical)

```
-ffp-contract=off    # prevent FMA from defeating Kahan summation
-fno-fast-math       # ensure IEEE 754 compliance
```

Without these flags, results will not be reproducible across platforms.

## Run tests

Run the tests after building to verify the software computes correctly.
All 8 tests must pass before using the Stages for research.
The tests validate against mpmath reference values and the success
criteria from the paper (Section 4.4, 10).

Run all tests at once:

```bash
cd build
ctest
```

Or individually with detailed output:

| Test | Paper Criterion | What it checks |
|------|----------------|----------------|
| test_dirichlet | Prerequisite | Borwein eta matches mpmath to 10+ digits |
| test_theta | Prerequisite | Riemann-Siegel theta matches mpmath to 10+ digits |
| test_known_zeros | V1, V2, V3 | L and Z_H vanish at first 100 known zeros, zero count matches Riemann-von Mangoldt |
| test_convergence | V7 | eta_N converges at rate O(N^{-0.5}) |
| test_rate_function | V4, V5, C1, C2 | Rate function F_1 = 1.0 at zeros, F_1 = (1-beta) away |
| test_cross_validation | V6, C4 | System 1 and System 2 find same zeros, G is real-valued |
| test_off_line | R1 | No off-critical-line zeros detected |
| test_functional_eq | C5 | Borwein and Euler-Maclaurin zeta agree |

```bash
# Linux/macOS                          # Windows
./test_dirichlet                        test_dirichlet.exe
./test_theta                            test_theta.exe
./test_known_zeros                      test_known_zeros.exe
./test_convergence                      test_convergence.exe
./test_rate_function                    test_rate_function.exe
./test_cross_validation                 test_cross_validation.exe
./test_off_line                         test_off_line.exe
./test_functional_eq                    test_functional_eq.exe
```

## Project structure

```
dqpt_zeta/
  CMakeLists.txt           Build configuration (auto-detects CUDA)
  README.md                This file
  .gitignore               Build artifacts, results, LaTeX temp files
  src/
    kahan.h                Kahan compensated summation
    config.h               Compile-time constants
    dirichlet.h/.cpp       Partial Dirichlet series + Borwein acceleration
    partition.h/.cpp       Partition function Z_N(beta)
    theta.h/.cpp           Riemann-Siegel theta function
    euler_maclaurin.h/.cpp Euler-Maclaurin tail correction
    system1.h/.cpp         System 1: L_N(beta, t) - probe spin coherence
    system2.h/.cpp         System 2: G_N(beta, t) - Loschmidt amplitude
    rate_function.h/.cpp   Rate function F_1 with underflow guard
    zeros.h/.cpp           Zero detection (Gram points, bisection, Turing)
    gpu_accel.h            GPU acceleration interface
    gpu_accel_cpu.cpp      CPU fallback (when CUDA not available)
    gpu_accel_cuda.cu      CUDA implementation (when CUDA available)
    main.cpp               CLI entry point (Stages 1-4, phase diagram)
  test/
    reference_values.h     mpmath-generated reference constants
    test_dirichlet.cpp     Borwein eta vs mpmath
    test_theta.cpp         Riemann-Siegel theta vs mpmath
    test_known_zeros.cpp   V1, V2: zeros at known positions
    test_convergence.cpp   V7: convergence rate O(N^{-0.5})
    test_rate_function.cpp V4, V5: rate function values
    test_cross_validation.cpp  V6: System 1 vs System 2 agreement
    test_off_line.cpp      R1: no off-critical-line zeros
    test_functional_eq.cpp C5: functional equation check
  data/
    zeros_known.txt        ~10200 known zeta zeros (LMFDB/Odlyzko)
  paper/                   LaTeX paper (see paper/README.md)
```

## References

- Wei et al., "The Riemann Hypothesis Emerges in Dynamical Quantum Phase
  Transitions", arXiv:2511.11199, 2025.
  https://arxiv.org/abs/2511.11199

- Borwein, "An Efficient Algorithm for the Riemann Zeta Function",
  Canadian Mathematical Society Conference Proceedings, 2000.

- Gabcke, "Neue Herleitung und explizite Restabschaetzung der
  Riemann-Siegel-Formel", PhD thesis, Goettingen, 1979.
