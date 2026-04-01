cmake_minimum_required(VERSION 3.16)

# Paper Section 9.3: "CUDA Toolkit: Optional; the code compiles and runs
# in CPU-only mode without CUDA."
include(CheckLanguage)
check_language(CUDA)
if(CMAKE_CUDA_COMPILER)
    project(dqpt_zeta LANGUAGES CXX CUDA)
    set(CMAKE_CUDA_STANDARD 17)
    # Detect GPU architecture automatically
    if(NOT DEFINED CMAKE_CUDA_ARCHITECTURES)
        set(CMAKE_CUDA_ARCHITECTURES native)
    endif()
    add_definitions(-DHAS_CUDA)
    message(STATUS "CUDA found - building with GPU acceleration")
else()
    project(dqpt_zeta LANGUAGES CXX)
    message(STATUS "CUDA not found - building CPU-only")
endif()

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Critical: IEEE 754 compliance (Paper Remark 3.2)
if(CMAKE_CXX_COMPILER_ID MATCHES "GNU|Clang")
    add_compile_options(-ffp-contract=off -fno-fast-math -Wall -Wextra -O2)
elseif(MSVC)
    add_compile_options(/fp:strict /W4 /O2)
endif()

# Core library
set(CORE_SOURCES
    src/dirichlet.cpp
    src/partition.cpp
    src/theta.cpp
    src/euler_maclaurin.cpp
    src/system1.cpp
    src/system2.cpp
    src/rate_function.cpp
    src/zeros.cpp
)

# GPU acceleration: use CUDA implementation if available, else CPU fallback
if(CMAKE_CUDA_COMPILER)
    list(APPEND CORE_SOURCES src/gpu_accel_cuda.cu)
else()
    list(APPEND CORE_SOURCES src/gpu_accel_cpu.cpp)
endif()

add_library(dqpt_core ${CORE_SOURCES})
target_include_directories(dqpt_core PUBLIC src)

if(CMAKE_CUDA_COMPILER)
    set_target_properties(dqpt_core PROPERTIES CUDA_SEPARABLE_COMPILATION ON)
    target_compile_options(dqpt_core PRIVATE
        $<$<COMPILE_LANGUAGE:CUDA>:--fmad=false -O2>)
endif()

# Main executable - ONE binary (Paper Section 9.1)
add_executable(dqpt_zeta src/main.cpp)
target_link_libraries(dqpt_zeta dqpt_core)

# Tests
enable_testing()

add_executable(test_dirichlet test/test_dirichlet.cpp)
target_link_libraries(test_dirichlet dqpt_core)
add_test(NAME test_dirichlet COMMAND test_dirichlet)

add_executable(test_theta test/test_theta.cpp)
target_link_libraries(test_theta dqpt_core)
add_test(NAME test_theta COMMAND test_theta)

add_executable(test_known_zeros test/test_known_zeros.cpp)
target_link_libraries(test_known_zeros dqpt_core)
add_test(NAME test_known_zeros COMMAND test_known_zeros)

add_executable(test_convergence test/test_convergence.cpp)
target_link_libraries(test_convergence dqpt_core)
add_test(NAME test_convergence COMMAND test_convergence)

add_executable(test_rate_function test/test_rate_function.cpp)
target_link_libraries(test_rate_function dqpt_core)
add_test(NAME test_rate_function COMMAND test_rate_function)

add_executable(test_cross_validation test/test_cross_validation.cpp)
target_link_libraries(test_cross_validation dqpt_core)
add_test(NAME test_cross_validation COMMAND test_cross_validation)

add_executable(test_off_line test/test_off_line.cpp)
target_link_libraries(test_off_line dqpt_core)
add_test(NAME test_off_line COMMAND test_off_line)

add_executable(test_functional_eq test/test_functional_eq.cpp)
target_link_libraries(test_functional_eq dqpt_core)
add_test(NAME test_functional_eq COMMAND test_functional_eq)
