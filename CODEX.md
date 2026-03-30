# Project Guidelines

## Language

- All code must be written in English.
- All code comments must be written in English.
- Variable names, function names, class names, and all identifiers must be in English.

## Documentation

- Always add JSDoc comments to all public classes, methods, and functions.
- JSDoc comments must describe the purpose, parameters, and return values.

## Example

```java
/**
 * Calculates the nth Fibonacci number.
 *
 * @param n the position in the Fibonacci sequence
 * @return the Fibonacci number at position n
 */
public static int fibonacci(int n) {
    if (n <= 1) return n;
    return fibonacci(n - 1) + fibonacci(n - 2);
}
```
