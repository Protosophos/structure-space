# Mathematical Visualizations (Java)

## Requirements

- Java 17+
- Gradle (included via wrapper)

## Build

```bash
./gradlew build
```

## Run

```bash
./gradlew run
```

This launches the MathExplorer application.

To run other main classes:

```bash
./gradlew run -PmainClass=fibonacci.FibonacciGalaxy
./gradlew run -PmainClass=fibonacci.FibonacciSunflower
./gradlew run -PmainClass=fibonacci.FibonacciTree
./gradlew run -PmainClass=plotter.Funktionsplotter
./gradlew run -PmainClass=taylor.TaylorExponential
./gradlew run -PmainClass=field.FieldFormulaAnalysis
```

## Project structure

```
java-projects/
  build.gradle.kts          Build configuration
  settings.gradle.kts       Project settings
  gradlew / gradlew.bat     Gradle wrapper
  src/main/java/
    explorer/               MathExplorer (main application)
    fibonacci/              Fibonacci visualizations (Galaxy, Sunflower, Tree)
    plotter/                Function plotter
    taylor/                 Taylor series visualization
    field/                  Field formula analysis tools
```
