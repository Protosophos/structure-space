# Project Guidelines

## Language

- All code must be written in English.
- All code comments must be written in English.
- Variable names, function names, class names, and all identifiers must be in English.

## Documentation

- Always add JSDoc/Doxygen comments to all public classes, methods, and functions.
- Comments must describe the purpose, parameters, and return values.

## DQPT-Zeta Rules

- The paper `sofa/dqpt_zeta/paper/paper.pdf` is the single source of truth.
- Before writing or changing ANY code in `sofa/dqpt_zeta/`, the exact paper section and equation number that justifies the change MUST be cited.
- Code that is not backed by the paper MUST NOT be written.
- No shortcuts, no "technically easier" alternatives, no separate programs that the paper does not describe.
- The paper describes ONE software with optional CUDA acceleration, not multiple executables.
- Never take shortcuts that deviate from the paper. If something is hard to implement as the paper describes, implement it exactly as the paper describes anyway.
- If something is unclear or missing in the paper, report it to the user. Do NOT fill in the gap with own assumptions.
