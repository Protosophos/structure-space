# LaTeX-Prüfbericht (Abgleich mit Uni-Ulm Leitfaden)

**Datum:** 31. März 2026  
**Prüfbasis:** Leitfaden "Das Erstellen einer wissenschaftlichen Arbeit in LaTeX" (Hesse, Uni Ulm)

## 1. Abweichungen von den formalen Vorgaben

### 1.1 Fehlende Standard-Pakete
- **Befund:** In `paper.tex` fehlen die Pakete `babel` (für korrekte Silbentrennung) und `inputenc` (für UTF-8 Zeichenkodierung/Umlaute).
- **Vorgabe:** Der Leitfaden schreibt `\usepackage[ngerman]{babel}` und `\usepackage[utf8]{inputenc}` zwingend vor.

### 1.2 Fehlende Verzeichnisse
- **Befund:** Das Dokument enthält kein Abbildungsverzeichnis und kein Tabellenverzeichnis.
- **Vorgabe:** Laut Leitfaden sollten nach dem Inhaltsverzeichnis `\listoffigures` und `\listoftables` aufgeführt werden.

### 1.3 Zitierstil und Paketwahl
- **Befund:** Das Projekt nutzt `natbib` mit dem Stil `unsrtnat` (numerisch).
- **Vorgabe:** Der Leitfaden empfiehlt das Paket `cite` und den Zitierstil `agsm` (Harvard-Stil/Autor-Jahr).

### 1.4 Seitenränder
- **Befund:** In `paper.tex` sind asymmetrische Ränder (4cm links, 2cm rechts) via `geometry` definiert.
- **Vorgabe:** Der Leitfaden nutzt das `geometry`-Paket mit Standardwerten (meist 3cm umlaufend), erwähnt jedoch keine asymmetrischen Bindekorrekturen für Artikel.

## 2. Konformitäts-Bestätigung
- **Dokumentenklasse:** Die Verwendung von `article` mit `12pt` und `titlepage` entspricht exakt der Empfehlung.
- **Mathe-Umgebungen:** Die Nutzung von `amsmath` und den Umgebungen `equation`/`align` ist konform.
- **Dateistruktur:** Die Aufteilung in separate Dateien via `\input` und eine externe `.bib`-Datei folgt den Best Practices des Leitfadens.
