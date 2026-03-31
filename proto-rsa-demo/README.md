# RSA Demo

Kleine Java-Swing-Lernanwendung, die den RSA-Ablauf in einem Fenster zeigt:
Schluesselbildung aus `p` und `q`, Auswahl von `e`, Berechnung von `d`, blockweise
Verschluesselung einer Nachricht und Rueckwandlung per Entschluesselung.

Die Anwendung ist bewusst didaktisch gehalten. Sie erklaert den Grundmechanismus von RSA,
ist aber keine Vorlage fuer ein sicheres Produktivsystem.

## Was die Demo zeigt

- Eingabe zweier Primzahlen `p` und `q`
- Berechnung von `n = p * q`
- Berechnung von `phi(n) = (p - 1)(q - 1)`
- Wahl eines passenden oeffentlichen Exponenten `e`
- Berechnung des privaten Exponenten `d` als modulares Inverses von `e`
- Zerlegung der Nachricht in einzelne UTF-8-Bytes
- Verschluesselung jedes Blocks mit `c = m^e mod n`
- Entschluesselung mit dem privaten Schluessel
- Grafische Uebersicht des gesamten Ablaufs

## Voraussetzungen

- Java 25
- Ein System mit grafischer Oberflaeche, da die Anwendung auf Swing basiert

Das Projekt verwendet in `build.gradle` eine Java-Toolchain mit Version `25`.

## Starten

Im Projektverzeichnis:

```bash
./gradlew run
```

Unter Windows:

```bat
gradlew.bat run
```

Falls du nur kompilieren willst:

```bash
./gradlew build
```

## Bedienung

1. Primzahlen fuer `p` und `q` eintragen.
2. Auf `Schluessel berechnen` klicken.
3. Eine Nachricht eingeben.
4. Auf `Verschluesseln` klicken.
5. Auf `Entschluesseln` klicken, um den Rueckweg zu sehen.

Die linke Seite des Fensters zeigt:

- die aktuellen Schluesselwerte `n`, `phi(n)`, `e` und `d`
- kurze fachliche Erklaerungen
- Blockdaten und Cipher-Bloecke
- die entschluesselte Nachricht

Die rechte Seite visualisiert den Ablauf von der Schluesselbildung bis zur
Nachrichtenverarbeitung.

## Wichtige Hinweise

- `p` und `q` muessen Primzahlen sein.
- `p` und `q` sollten verschieden sein.
- Jeder Nachrichtenblock muss kleiner als `n` sein.
- Die Demo verwendet einzelne UTF-8-Bytes als Bloecke. Deshalb muessen die
  gewaelten Primzahlen gross genug sein, damit `n` groesser als alle vorkommenden
  Bytewerte ist.

Wenn ein Byte nicht kleiner als `n` ist, bricht die Anwendung mit einem Hinweis ab,
groessere Primzahlen zu waehlen.

## Sicherheitsgrenzen der Demo

Diese Anwendung dient nur dem Lernen. Sie bildet absichtlich nicht die sichere Praxis
realer RSA-Systeme ab.

- Kein Padding wie OAEP oder PKCS#1
- Keine sichere Schluesselgroesse
- Keine Hybridverschluesselung
- Nachrichten werden byteweise und direkt verarbeitet
- Fokus auf Nachvollziehbarkeit statt Sicherheit

## Projektaufbau

```text
src/main/java/
├── app/              Startpunkt der Anwendung
├── crypto/           RSA-Logik fuer Schluessel, Ver- und Entschluesselung
├── gui/              Swing-Oberflaeche
├── model/            Datenmodelle fuer Schluessel und Nachrichtenbloecke
└── visualization/    Grafische Darstellung des RSA-Ablaufs
```

## Technischer Ablauf in der Demo

1. Aus `p` und `q` werden `n` und `phi(n)` berechnet.
2. Fuer `e` wird bevorzugt einer der Werte `65537`, `17`, `5` oder `3` gewaehlt,
   sofern er zu `phi(n)` teilerfremd ist.
3. `d` wird als modulares Inverses von `e` modulo `phi(n)` berechnet.
4. Die Nachricht wird in UTF-8-Bytes zerlegt.
5. Jedes Byte wird als Zahlenblock `m` mit `m < n` verschluesselt.
6. Bei der Entschluesselung wird jeder Cipher-Block mit `d` wieder in ein Byte
   zurueckgefuehrt.

## Zweck

Das Projekt eignet sich fuer Unterricht, eigene Experimente und kurze Demonstrationen,
wenn RSA nicht nur formal, sondern sichtbar Schritt fuer Schritt erklaert werden soll.
