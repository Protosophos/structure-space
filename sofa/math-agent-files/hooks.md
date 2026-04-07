# Hooks

Anleitung, wie Codex (CLI) per Hooks dauerhaft an die Projektdateien erinnert wird, sodass nichts wegdriftet, was über eine lange Session hinweg präsent bleiben muss.

## Kernidee

Sprachmodelle vergessen. Je länger eine Unterhaltung läuft, desto stärker driftet der Inhalt der initial geladenen Dateien aus dem effektiven Aufmerksamkeitsbereich:

- Sorgfaltspflichten aus AGENTS.md werden gegen Ende der Session schwächer beachtet.
- Begriffe aus GLOSSARY.md werden durch Schulbuchterminologie ersetzt.
- Statusklassen (`etabliert` / `strukturhypothese` / ...) bleiben weg.

Außerdem lädt Codex von sich aus nur `AGENTS.md`. Alle anderen Projektdateien (CONTEXT.md, GLOSSARY.md, ...) sieht das Modell ohne Hooks gar nicht.

Hooks decken beides ab: sie holen Dateien überhaupt erst in die Session, und sie frischen die kritischsten unter ihnen über die Session hinweg auf.

## Zwei-Tier-Modell

Nicht jede Datei braucht denselben Hook. Welcher Event-Typ richtig ist, hängt davon ab, wie kritisch und wie drift-anfällig der Inhalt ist.

| Tier | Hook-Event | Wofür | Kosten |
|---|---|---|---|
| 1 | `SessionStart` | reine Referenz, einmal pro Session ausreichend | gering, einmalig |
| 2 | `UserPromptSubmit` | drift-kritische Verhaltensregeln, müssen jeden Turn präsent sein | hoch, multipliziert mit Turn-Anzahl |

### Tier 1 — `SessionStart` (Default für die meisten Dateien)

Reicht aus für Inhalte, die das Modell *bewusst* benutzt: Projektstand, Begriffsliste, Stilvorgaben, Aufgabenliste. Diese werden nicht still verdrängt — der Agent schaut sie nach, wenn er sie braucht. Es genügt, sie einmal beim Sessionstart in den Kontext zu legen.

Dateien (Stand bei Erstellung dieses Dokuments, Zeichenanzahl per `wc -m`):
- **CONTEXT.md** — Projektstand, Befunde, Strukturhypothesen (2.818 Zeichen)
- **GLOSSARY.md** — Arbeitsbegriffe (1.133 Zeichen)
- **WORKSTYLE.md** — Sprache und Form (383 Zeichen)
- **TASKS.md** — nächste Schritte (417 Zeichen)
- **Summe:** ca. 4.750 Zeichen

Kosten: einmalig pro Session. Echte Token-Anzahl siehe Abschnitt "Token-Kosten" weiter unten — die hängt vom Tokenizer ab und ist von hier aus nicht ohne Messung präzise anzugeben.

### Tier 2 — `UserPromptSubmit` (für AGENTS.md)

Reicht *nicht* aus, wenn ein Inhalt still wegdriftet, ohne dass das Modell Anlass hätte ihn nachzuschlagen. Das gilt für die gesamte AGENTS.md: Anti-Halluzination, Statusklassen, Modi, Verbote, Priorität — all das wirkt implizit und verblasst über die Session hinweg, ohne dass jemand merkt, dass es passiert. Genau hier ist die per-Turn-Reinjektion gerechtfertigt.

Datei:
- **AGENTS.md** — vollständig, jeder Prompt. Aktuell 3.143 Zeichen (per `wc -m`).

Codex lädt AGENTS.md zwar zum Sessionstart automatisch — aber nur einmal. Der Hook ergänzt das durch die fortlaufende Auffrischung über die ganze Session.

Kosten: 3.143 Zeichen × Anzahl Turns. Wer den exakten Token-Verbrauch will, jagt die Datei durch den Tokenizer des verwendeten Modells (siehe Abschnitt "Token-Kosten" weiter unten). Eine grobe Faustregel ist 1 Token ≈ 2–3 Zeichen für deutschen Text — präziser geht es nur mit Messung, nicht mit Schätzung.

## Weitere Hook-Events für spezifische Fälle

Codex bietet über die hier verwendeten `SessionStart` und `UserPromptSubmit` hinaus drei weitere Event-Typen. Sie sind im Default-Setup nicht aktiv, lassen sich aber gezielt einsetzen, wenn bestimmte Dateien nur in bestimmten Situationen relevant sind.

### `PreToolUse` — vor einem Tool-Aufruf
Feuert direkt bevor der Agent ein Tool ausführt (in Codex aktuell mit `Bash`-Matcher). Eignet sich für Inhalte, die *nur* im Moment eines Tool-Calls relevant sind und sonst nur Tokens verbrauchen würden.

Beispiel-Anwendung: Eine Datei `BASH_POLICY.md` enthält Regeln für Shell-Befehle (welche Pfade erlaubt sind, welche destruktiven Operationen verboten sind, Sandbox-Hinweise). Sie wird per `PreToolUse`-Hook nur dann eingespeist, wenn der Agent gerade einen Bash-Call absetzen will — und nicht in jeder Turn.

```json
"PreToolUse": [
  {
    "matcher": "Bash",
    "hooks": [
      { "type": "command", "command": "bash ~/.codex/hooks/inject_bash_policy.sh" }
    ]
  }
]
```

### `PostToolUse` — nach einem Tool-Aufruf
Feuert nach Abschluss eines Tool-Calls (ebenfalls Bash-Matcher). Eignet sich für Inhalte, die *erst nach* einer Aktion greifen sollen — typischerweise Klassifikations- oder Verifikationsregeln, die nur dann zählen, wenn tatsächlich etwas berechnet wurde.

Beispiel-Anwendung: Eine Datei `EVAL_CHECKLIST.md` mit Punkten wie "Ergebnis einer Statusklasse zuordnen", "Dimensionen prüfen", "Numerik plausibilisieren". Wird per `PostToolUse`-Hook nur dann eingespeist, wenn der Agent gerade ein Tool-Ergebnis vor sich hat — nicht in reinen Konversations-Turns.

```json
"PostToolUse": [
  {
    "matcher": "Bash",
    "hooks": [
      { "type": "command", "command": "bash ~/.codex/hooks/inject_eval_checklist.sh" }
    ]
  }
]
```

### `Stop` — vor Antwortabschluss
Feuert wenn der Agent seinen Turn beendet. Eignet sich für Schluss-Checks, die nur am Ende einer Antwort relevant sind und dort als "letzte Instanz" wirken sollen.

Beispiel-Anwendung: Eine Datei `OUTPUT_CHECKLIST.md` mit Fragen wie "Alle nichttrivialen Aussagen klassifiziert?", "Wurden Quellen erfunden?", "Liegen offene Fragen unkommentiert herum?". Wird per `Stop`-Hook eingespeist, sodass der Agent sie als Selbst-Review vor Abschluss heranziehen kann.

```json
"Stop": [
  {
    "hooks": [
      { "type": "command", "command": "bash ~/.codex/hooks/inject_output_checklist.sh" }
    ]
  }
]
```

### Wann welcher Event-Typ
| Inhalt nur relevant ... | Geeigneter Event |
|---|---|
| ab Sessionstart, dauerhaft | `SessionStart` |
| in jedem User-Turn (drift-anfällig) | `UserPromptSubmit` |
| vor Bash-Calls (Policy, Sandbox) | `PreToolUse` |
| nach Bash-Calls (Auswertung, Klassifikation) | `PostToolUse` |
| am Ende einer Antwort (Schluss-Review) | `Stop` |

## Empfohlene Default-Konfiguration

Für dieses Projekt:

- **Tier 1 (SessionStart):** `inject_state.sh` lädt CONTEXT.md, GLOSSARY.md, WORKSTYLE.md, TASKS.md.
- **Tier 2 (UserPromptSubmit):** `inject_agents.sh` re-injiziert die vollständige AGENTS.md.

Damit hat das Modell den vollen Projektzustand ab Turn 1, und die Verhaltensregeln werden zusätzlich pro Turn aufgefrischt. Die exakte Token-Belastung hängt von Tokenizer und Sessionlänge ab.

## Hooks-Feature aktivieren

Codex lädt `AGENTS.md` automatisch beim Start als Projektdoc. Für die Hooks selbst muss in `~/.codex/config.toml` das Feature-Flag gesetzt werden:

```toml
[features]
codex_hooks = true
```

## Setup unter Linux

### 1. Verzeichnisse anlegen
```bash
mkdir -p ~/.codex/hooks
mkdir -p <project_root>/.codex
```

`<project_root>` ist der absolute Pfad zum Projekt — also dem Verzeichnis, in dem AGENTS.md liegt. Z.B. `/home/user/projects/math/.codex`.

### 2. `hooks.json` im Projekt
Datei: `<project_root>/.codex/hooks.json`

```json
{
  "hooks": {
    "SessionStart": [
      {
        "matcher": "startup|resume",
        "hooks": [
          {
            "type": "command",
            "command": "bash ~/.codex/hooks/inject_state.sh",
            "statusMessage": "Lade Projektzustand"
          }
        ]
      }
    ],
    "UserPromptSubmit": [
      {
        "hooks": [
          {
            "type": "command",
            "command": "bash ~/.codex/hooks/inject_agents.sh"
          }
        ]
      }
    ]
  }
}
```

### 3. Skripte ausführbar machen
```bash
chmod +x ~/.codex/hooks/*.sh
```

### 4. Voraussetzungen
Die Skripte brauchen `jq`:
```bash
sudo apt install jq
```

### 5. Testlauf
```bash
echo '{"cwd":"'"$(pwd)"'","hook_event_name":"SessionStart"}' \
  | bash ~/.codex/hooks/inject_state.sh

echo '{"cwd":"'"$(pwd)"'","hook_event_name":"UserPromptSubmit"}' \
  | bash ~/.codex/hooks/inject_agents.sh
```
Erwartet jeweils: gültiges JSON auf stdout mit `hookSpecificOutput.additionalContext`.

## Skripte

### `~/.codex/hooks/inject_state.sh` (Tier 1)
Lädt zusätzlich zum Auto-Load der AGENTS.md auch CONTEXT.md, GLOSSARY.md, WORKSTYLE.md und TASKS.md einmalig zum Sessionstart.

```bash
#!/usr/bin/env bash
set -euo pipefail

# Findet den Projekt-Root, indem vom Startverzeichnis aus aufwärts
# nach AGENTS.md gesucht wird. Git-agnostisch.
find_project_root() {
  local dir="$1"
  while [ "$dir" != "/" ] && [ -n "$dir" ]; do
    if [ -f "$dir/AGENTS.md" ]; then
      echo "$dir"
      return 0
    fi
    dir=$(dirname "$dir")
  done
  echo "$1"
}

INPUT=$(cat)
CWD=$(echo "$INPUT" | jq -r '.cwd // empty')
PROJECT_ROOT=$(find_project_root "${CWD:-$PWD}")

read_if_exists() {
  local f="$1"
  if [ -f "$f" ]; then
    echo "=== $(basename "$f") ==="
    cat "$f"
    echo
  fi
}

CONTEXT=$(
  read_if_exists "$PROJECT_ROOT/CONTEXT.md"
  read_if_exists "$PROJECT_ROOT/GLOSSARY.md"
  read_if_exists "$PROJECT_ROOT/WORKSTYLE.md"
  read_if_exists "$PROJECT_ROOT/TASKS.md"
)

[ -n "$CONTEXT" ] || exit 0

jq -nc --arg ctx "Projektzustand (einmalig zum Sessionstart):

$CONTEXT" '{
  hookSpecificOutput: {
    hookEventName: "SessionStart",
    additionalContext: $ctx
  }
}'
```

### `~/.codex/hooks/inject_agents.sh` (Tier 2)
Re-injiziert die vollständige AGENTS.md vor jedem User-Prompt.

```bash
#!/usr/bin/env bash
set -euo pipefail

find_project_root() {
  local dir="$1"
  while [ "$dir" != "/" ] && [ -n "$dir" ]; do
    if [ -f "$dir/AGENTS.md" ]; then
      echo "$dir"
      return 0
    fi
    dir=$(dirname "$dir")
  done
  echo "$1"
}

INPUT=$(cat)
CWD=$(echo "$INPUT" | jq -r '.cwd // empty')
PROJECT_ROOT=$(find_project_root "${CWD:-$PWD}")
AGENTS="$PROJECT_ROOT/AGENTS.md"

[ -f "$AGENTS" ] || exit 0

CONTENT=$(cat "$AGENTS")

jq -nc --arg ctx "Erinnerung — vollständige AGENTS.md:

$CONTENT" '{
  hookSpecificOutput: {
    hookEventName: "UserPromptSubmit",
    additionalContext: $ctx
  }
}'
```

