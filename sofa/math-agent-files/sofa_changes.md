# Änderungsprotokoll

Begründung größerer Umbauten an den Projektdokumenten. Reine Tippfehler oder Mini-Edits stehen hier nicht.

## 2026-04-07 — Entrümpelung der md-Dateien, Hooks-Setup, Datei-Layout

### Was geändert wurde

**AGENTS.md (vorher AGENT.md)**
- Umbenannt in `AGENTS.md`, weil Codex CLI standardmäßig nach diesem Namen sucht und ihn als Projektdoc auto-lädt. Mit `AGENT.md` (ohne S) wäre ein Fallback-Eintrag in `~/.codex/config.toml` nötig gewesen — unnötige Komplexität.
- Vollständig neu strukturiert. Vorher bestand die Datei zu großen Teilen aus meta-philosophischer Wiederholung ("Du sollst nicht primär ...", "In diesem Projekt gilt ...") ohne operative Konsequenz.
- Neuer Pflichtblock "Wissenschaftliche Sorgfaltspflichten" mit drei Unterpunkten:
  - *Keine Halluzinationen* — explizites Verbot erfundener Formeln, Konstanten, Quellen, Pseudo-Herleitungen.
  - *Statusklassen als Pflichtangabe* — Tabelle mit `etabliert` / `dimensionskonsistent` / `strukturhypothese` / `heuristisch` / `offen`.
  - *Korrektheit vor Eleganz* — Widersprüche markieren statt wegformulieren, Dimensionsanalyse als Mindeststandard.
- Priorität 1 ist jetzt wissenschaftliche Korrektheit, nicht mehr "interne Konsistenz".

**CONTEXT.md**
- Wiederholungen und Meta-Floskeln entfernt ("Es wurde herausgearbeitet ...", "Dieses Projekt entwickelt ...").
- Die 9 inhaltlichen Befunde sind erhalten, aber kompakter formuliert und jeweils mit Statusklasse versehen, damit sie an die neuen Sorgfaltspflichten anschlussfähig sind.

**WORKSTYLE.md**
- Auf Sprache und Form reduziert. Alle übrigen Inhalte standen redundant auch in AGENTS.md und sind dort verblieben.

**GLOSSARY.md / TASKS.md / EXAMPLES/README.md**
- Kleinere Glättungen. Notation vereinheitlicht (S¹, S², Sⁿ statt S^1, S^2). Doppelte H1-Header (Dateiname als Überschrift) entfernt.

**OPEN_QUESTIONS.md**
- Inhaltlich nicht angefasst. Aber alle Verweise darauf wurden aus AGENTS.md, WORKSTYLE.md, README.md und hooks.md entfernt: die Datei ist eine Arbeitsdatei des Nutzers, nicht für Codex bestimmt, und sollte daher in keiner Codex-lesbaren Anweisung mehr auftauchen.

**README.md (neu)**
- Übersicht über alle Projektdateien und ihre Rolle.
- Hygieneregeln für die Dokumentation (kurz halten, keine Überlappungen, eine Datei = ein Zweck, Hooks erhöhen Token-Kosten).
- Diese Regeln standen ursprünglich in AGENTS.md unter "Dokumenten-Hygiene" — gehören dort aber nicht hin, weil sie Projekt-Layout sind, keine Verhaltensregel für den Agenten. Verschoben.

**hooks.md (neu)**
- Anleitung für Codex-CLI-Hooks unter Linux mit Begründung, warum Hooks gegen Modell-Drift in langen Sessions notwendig sind.
- Endgültige Architektur ist ein **Zwei-Tier-Modell**:
  - Tier 1 (`SessionStart`): einmalige Injektion von CONTEXT.md, GLOSSARY.md, WORKSTYLE.md, TASKS.md — Codex lädt diese laut Doku nicht von selbst (verifiziert in [AGENTS.md guide](https://developers.openai.com/codex/guides/agents-md) und [Configuration Reference](https://developers.openai.com/codex/config-reference): "at most one file per directory", `project_doc_fallback_filenames` ist Ersatz statt Zusatz).
  - Tier 2 (`UserPromptSubmit`): vollständige AGENTS.md vor jedem Prompt — als Anti-Drift für die Verhaltensregeln, die still wegrutschen.
- Zwei Skripte: `inject_state.sh` (Tier 1) und `inject_agents.sh` (Tier 2).
- hooks.md enthält außerdem einen Marker-Datei-Test, mit dem die Aussage "Codex lädt nur AGENTS.md" in der installierten Codex-Version empirisch nachprüfbar ist. Wenn der Test ergibt, dass Codex doch alles auto-lädt, würde Tier 1 überflüssig.
- Diese Architektur entstand iterativ durch mehrere Korrekturen vom Nutzer:
  - Ursprünglich war fast alles an `SessionStart` gehängt — nutzlos gegen *Drift*, aber tatsächlich nötig für Dateien, die Codex sonst gar nicht sieht.
  - Zwischenstand: alle Dateien an `UserPromptSubmit` — Token-Verschwendung, weil Referenzdateien nicht still driften, sondern bewusst nachgeschlagen werden.
  - Zwischenstand: AGENTS.md kompakt (nur Sorgfaltspflichten) per `UserPromptSubmit`, plus Sparmodus — verworfen, weil das Zerschneiden von AGENTS.md inkonsistent ist.
  - Zwischenstand: Drei-Tier-Modell mit `PostToolUse` als Tier 3 — verworfen, weil `UserPromptSubmit` AGENTS.md innerhalb eines Turns ohnehin schon eingespeist hat, ein zusätzlicher PostToolUse-Hook wäre Redundanz ohne Nutzen.
  - Endstand: AGENTS.md per Tier 2, alles andere per Tier 1.
- Token-Kostenangaben in einer früheren Version waren erfunden ("ca. 700 Tokens"). Korrigiert auf verifizierbare Zeichenanzahlen plus expliziten Hinweis, dass echte Token-Werte nur per Tokenizer messbar sind.
- OPEN_QUESTIONS.md kommt in keinem Tier vor — sie ist eine Arbeitsdatei des Nutzers, nicht für Codex bestimmt.

**sofa_changes.md (diese Datei)**
- Neu, um nachvollziehbar festzuhalten, was und warum geändert wurde.

### Warum

**Floskeln raus.** Die ursprünglichen Dateien waren für KI-Konsumenten (Codex) geschrieben, aber voll mit menschen-rhetorischen Wiederholungen, die operativ nichts bewirken. Codex liest sie als Eingabetokens — jede Zeile ohne klare Anweisung kostet Tokens und verdünnt die wirklich wichtigen Aussagen.

**Anti-Halluzinations-Block fehlte.** Vorher gab es nur diffuse Andeutungen ("strukturell plausibel / dimensionsmäßig korrekt / etabliert / heuristisch") und keine harte Pflicht, Aussagen zu klassifizieren. Ohne explizites Verbot erfundener Formeln und ohne Pflicht-Statusklassen ist nicht zu verhindern, dass Codex Strukturhypothesen wie etablierte Physik aussehen lässt.

**Hooks gegen Drift in langen Sessions.** Codex liest AGENTS.md beim Sessionstart einmal. Über eine längere Unterhaltung verblasst dieser Kontext im Modell — Sorgfaltspflichten werden weicher, Begriffe rutschen Richtung Schulbuch, Statusklassen verschwinden. Ein `UserPromptSubmit`-Hook, der vor jedem Prompt die vollständige AGENTS.md re-injiziert, korrigiert das. Andere Dateien (CONTEXT, GLOSSARY, WORKSTYLE, TASKS) werden vom Agenten bewusst nachgeschlagen und brauchen daher nur Tier 1 (`SessionStart`).

**Trennung Verhaltensregel vs. Projekt-Layout.** Hygiene-Regeln zur Dateistruktur ("eine Datei = ein Zweck", "Überlappungen vermeiden") sind kein Verhaltensauftrag an den Agenten, sondern Projekt-Konvention. Sie gehören in README.md, nicht in AGENTS.md. Sonst bläht sich AGENTS.md auf, und die Sorgfaltspflichten gehen optisch unter.

### Maßstab für künftige Edits

- Lange Markdown-Dateien führen dazu, dass KI-Werkzeuge bei umfangreichen Aufgaben Teile davon vergessen oder ignorieren. Je länger eine Datei, desto unzuverlässiger ihre Wirkung — und desto teurer, falls sie per Hook re-injiziert wird.
- Überlappungen zwischen Dateien sind zu vermeiden. Wiederholt sich ein Inhalt, entstehen bei späteren Edits Drift und Widersprüche, weil nur eine Stelle aktualisiert wird.
- Vor jedem größeren Edit prüfen: Gehört das wirklich in *diese* Datei laut Tabelle in README.md? Wenn nein, dann nicht hineinschreiben.
