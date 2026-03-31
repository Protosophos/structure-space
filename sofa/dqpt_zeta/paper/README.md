# Paper: Numerical Investigation of the Riemann Hypothesis via DQPTs

LaTeX source for the theoretical framework and implementation plan document.

Document structure follows the conventions established in these three
German university LaTeX guides:

1. [Uni Ulm - LaTeX Guide](https://www.uni-ulm.de/fileadmin/website_uni_ulm/mawi.inst.160/pdf_dokumente/LaTeX.pdf)
   (Gregor Hesse, 2012)
2. [Uni Bayreuth - LaTeX for Scientific Work](https://www.icp.uni-bayreuth.de/pool/dokumente/LaTeX-fuer-wissenschaftliche-Arbeiten.pdf)
   (Pekka Sagner, 2015)
3. [HTWK Leipzig - Creating a Scientific Thesis with LaTeX](https://i4s.htwk-leipzig.de/fileadmin/portal/m_i4s/Abschlussarbeiten/BA_Eine_wissenschaftliche_Arbeit_mit_LaTeX_erstellen.pdf)
   (Theres Saebel, 2020)

All three guides agree on the following conventions, which this document implements:

- `\documentclass[12pt,a4paper,titlepage]{article}`
- Dedicated title page via `\begin{titlepage}`
- Roman numerals (I, II, ...) for front matter (abstract, table of contents)
- Arabic numerals starting at 1 for main content (`\pagenumbering{arabic}`, `\setcounter{page}{1}`)
- Section-based equation numbering: (1.1), (1.2), (2.1), ...
- Asymmetric margins (4 cm left for binding, 2 cm right)
- Headers/footers via `fancyhdr`
- Bibliography via `natbib` + BibTeX
- Labels and cross-references (`\label{}`, `\ref{}`, `\eqref{}`)
- Table of contents via `\tableofcontents` directly after the title page

## Prerequisites

A working LaTeX distribution with `pdflatex` and `bibtex` is required.

### Windows

**Option A: winget (recommended)**
```
winget install MiKTeX.MiKTeX
```

**Option B: Chocolatey**
```
choco install miktex
```

After installation, configure MiKTeX to auto-install missing packages
(avoids manual confirmation dialogs for each package):
```
initexmf --set-config-value="[MPM]AutoInstall=1"
```

MiKTeX adds `pdflatex` and `bibtex` to `PATH` after a terminal restart.
If not, the default location is:
```
C:\Users\<username>\AppData\Local\Programs\MiKTeX\miktex\bin\x64
```

### Linux (Debian / Ubuntu)

```bash
sudo apt update
sudo apt install texlive-full
```

Minimal alternative (smaller download, ~500 MB instead of ~5 GB):
```bash
sudo apt install texlive-latex-recommended texlive-latex-extra \
                 texlive-fonts-recommended texlive-science \
                 texlive-bibtex-extra biber
```

### Linux (Fedora / RHEL / CentOS)

```bash
sudo dnf install texlive-scheme-full
```

Minimal alternative:
```bash
sudo dnf install texlive-latex texlive-collection-latexrecommended \
                 texlive-collection-latexextra texlive-collection-fontsrecommended \
                 texlive-collection-mathscience texlive-natbib texlive-bibtex
```

### Linux (Arch Linux / Manjaro)

```bash
sudo pacman -S texlive
```

This installs the full TeX Live distribution. For a minimal setup:
```bash
sudo pacman -S texlive-basic texlive-latex texlive-latexrecommended \
               texlive-latexextra texlive-fontsrecommended texlive-mathscience \
               texlive-bibtexextra
```

### macOS

**Option A: Homebrew (recommended)**
```bash
brew install --cask mactex
```

**Option B: Minimal installation**
```bash
brew install --cask basictex
sudo tlmgr update --self
sudo tlmgr install natbib booktabs float listings mathtools fancyhdr caption
```

After installing MacTeX or BasicTeX, restart the terminal so that
`pdflatex` and `bibtex` are available in `PATH`.

## Building the PDF

Run the following commands from this directory:

```bash
pdflatex -interaction=nonstopmode paper.tex
bibtex paper
pdflatex -interaction=nonstopmode paper.tex
pdflatex -interaction=nonstopmode paper.tex
```

Three `pdflatex` passes are required:
1. First pass: generates `.aux` files with label/reference information
2. `bibtex`: resolves citations from `references.bib`
3. Second pass: incorporates bibliography and updates cross-references
4. Third pass: resolves any remaining forward references and page numbers

To clean up build artifacts afterwards:

```bash
rm -f paper.aux paper.bbl paper.blg paper.out paper.toc paper.log
```

The output is `paper.pdf`.

## File overview

| File | Content |
|------|---------|
| `paper.tex` | Main document (preamble, title page, abstract, `\input{}` structure) |
| `references.bib` | BibTeX bibliography (28 entries with DOI/URL links) |
| `sec_intro.tex` | Section 1: Introduction and Motivation |
| `sec_theory.tex` | Section 2: Theoretical Framework (Systems 1+2, RH equivalence) |
| `sec_methods.tex` | Section 3: Numerical Methods (Borwein, Euler-Maclaurin, Riemann-Siegel, GPU) |
| `sec_experiment.tex` | Section 4: Experimental Design (protocol, parameters, validation) |
| `sec_errors.tex` | Section 5: Error Analysis (truncation, FP64, cancellation) |
| `sec_results.tex` | Section 6: Expected Results |
| `sec_prior.tex` | Section 7: Relation to Prior Work |
| `sec_directions.tex` | Section 8: Directions Toward Proof |
| `sec_architecture.tex` | Section 9: Implementation Architecture |
| `sec_criteria.tex` | Section 10: Success Criteria (V1-V7, R1-R2) |
| `sec_timeline.tex` | Section 11: Timeline and Milestones |
