# Projekt

Strukturorientierte Ordnung für Mathematik, Physik und ihre Übergangsbereiche. Inhaltliches steht in CONTEXT.md, Verhaltensregeln für den Agenten in AGENTS.md.

## Dateien

| Datei | Zweck |
|---|---|
| AGENTS.md | Verhaltensregeln für den Agenten (Codex) |
| WORKSTYLE.md | Sprache und Form |
| CONTEXT.md | Aktueller Projektstand und Befunde |
| GLOSSARY.md | Arbeitsbegriffe |
| TASKS.md | Nächste Schritte |
| EXAMPLES/README.md | Beispielideen |
| hooks.md | Codex-Hooks-Setup unter Linux |
| sofa_changes.md | Begründung größerer Umbauten an den Dokumenten |

## Hygieneregeln für die Dokumentation

- **Kurz halten.** Lange Markdown-Dateien führen dazu, dass KI-Werkzeuge Teile davon "vergessen". Wenn ein Abschnitt nicht regelmäßig gebraucht wird, raus damit.
- **Überlappungen vermeiden.** Jede Information gehört genau in eine Datei. Sonst Drift und Widersprüche bei Edits. Im Zweifel verlinken statt duplizieren.
- **Eine Datei = ein Zweck.** Die Trennung in der Tabelle oben nicht aufweichen.
- **Hooks erhöhen die Token-Kosten.** Wer Dateien per Hook (siehe hooks.md) bei jedem Prompt re-injiziert, multipliziert ihre Länge mit der Anzahl der Turns. Zusätzlicher Grund, knapp zu bleiben.
