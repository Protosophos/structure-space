# Primzahlen

## Überblick

Primzahlen sind die atomaren Bausteine der ganzen Zahlen.
Sie stehen im Zentrum der Zahlentheorie und verbinden reine Mathematik mit praktischen Anwendungen wie Kryptographie, Algorithmik, Zufallstests und Computeralgebra.

Eine vollständig abschließende Darstellung aller Erkenntnisse über Primzahlen gibt es nicht, weil das Gebiet aktiv erforscht wird.
Dieses Dokument fasst jedoch die wichtigsten bekannten Strukturen, Sätze, Vermutungen, Verfahren und Anwendungen in kompakter, aber breiter Form zusammen.

## Definition

Eine Primzahl ist eine natürliche Zahl `p > 1`, die genau zwei positive Teiler besitzt:

- `1`
- `p`

Beispiele:

```text
2, 3, 5, 7, 11, 13, 17, 19, 23, 29, ...
```

Nicht-Primzahlen größer als `1` heißen zusammengesetzte Zahlen.

Beispiele:

```text
4 = 2 * 2
6 = 2 * 3
8 = 2 * 4
9 = 3 * 3
10 = 2 * 5
```

## Erste Grundtatsachen

### 1. Die kleinste Primzahl

Die kleinste Primzahl ist `2`.
Sie ist zugleich die einzige gerade Primzahl.

Begründung:
Jede andere gerade Zahl ist durch `2` teilbar und daher zusammengesetzt.

### 2. Die Zahl `1` ist keine Primzahl

Die `1` hat nur einen positiven Teiler, nämlich sich selbst.
Sie wird deshalb weder als prim noch als zusammengesetzt klassifiziert.

Das ist wichtig, damit der Fundamentalsatz der Arithmetik eindeutig bleibt.

### 3. Es gibt unendlich viele Primzahlen

Das ist ein klassischer Satz von Euklid.
Die Idee des Beweises:

- Man nehme an, es gebe nur endlich viele Primzahlen.
- Man multipliziere alle und addiere `1`.
- Die neue Zahl ist durch keine der angenommenen Primzahlen teilbar.
- Daraus entsteht ein Widerspruch.

Also gibt es unendlich viele Primzahlen.

## Der Fundamentalsatz der Arithmetik

Jede natürliche Zahl `n > 1` lässt sich eindeutig als Produkt von Primzahlen schreiben, bis auf die Reihenfolge der Faktoren.

Beispiele:

```text
12 = 2^2 * 3
18 = 2 * 3^2
60 = 2^2 * 3 * 5
360 = 2^3 * 3^2 * 5
```

Dieser Satz ist fundamental, weil er Primzahlen zur elementaren Struktur jeder ganzen Zahl macht.

## Wie Primzahlen unter den natürlichen Zahlen verteilt sind

Die Folge der Primzahlen wirkt lokal unregelmäßig, folgt global aber statistischen Gesetzen.

### Primzahlzählfunktion

Mit `π(x)` bezeichnet man die Anzahl der Primzahlen `<= x`.

Beispiele:

```text
π(10) = 4
π(100) = 25
π(1000) = 168
```

### Primzahlsatz

Der Primzahlsatz beschreibt die asymptotische Verteilung:

```text
π(x) ~ x / ln(x)
```

für `x -> unendlich`.

Das bedeutet:
Die Dichte der Primzahlen in der Nähe von `x` ist ungefähr `1 / ln(x)`.

Folgerungen:

- Primzahlen werden seltener, aber verschwinden nie.
- Die durchschnittliche Lücke zwischen benachbarten Primzahlen wächst ungefähr wie `ln(x)`.

### Genauere Approximationen

Oft ist die logarithmische Integralfunktion `Li(x)` genauer als `x / ln(x)`.

Der Vergleich zwischen `π(x)` und `Li(x)` ist tief mit der Riemannschen Vermutung verbunden.

## Wichtige Klassen von Primzahlen

### Gerade und ungerade Primzahlen

- `2` ist die einzige gerade Primzahl
- alle anderen Primzahlen sind ungerade

### Zwillingsprimzahlen

Zwillingsprimzahlen sind Primzahlpaare mit Abstand `2`:

```text
(3, 5), (5, 7), (11, 13), (17, 19), ...
```

Es ist bis heute nicht bewiesen, ob es unendlich viele davon gibt.
Das ist die Zwillingsprimzahlvermutung.

### Sophie-Germain-Primzahlen

Eine Primzahl `p` heißt Sophie-Germain-Primzahl, wenn auch `2p + 1` prim ist.

Beispiele:

```text
2 -> 5
3 -> 7
5 -> 11
11 -> 23
```

### Mersenne-Primzahlen

Zahlen der Form

```text
M_p = 2^p - 1
```

heißen Mersenne-Zahlen.
Ist `M_p` prim, so spricht man von einer Mersenne-Primzahl.

Wenn `2^p - 1` prim ist, muss `p` selbst prim sein.
Die Umkehrung gilt nicht immer.

Beispiele:

```text
2^2 - 1 = 3
2^3 - 1 = 7
2^5 - 1 = 31
2^7 - 1 = 127
2^11 - 1 = 2047 = 23 * 89
```

Mersenne-Primzahlen spielen eine große Rolle bei der Suche nach sehr großen Primzahlen.

### Fermat-Primzahlen

Zahlen der Form

```text
F_n = 2^(2^n) + 1
```

heißen Fermat-Zahlen.

Die ersten Beispiele:

```text
3, 5, 17, 257, 65537
```

sind prim.
Für viele weitere `n` sind die entsprechenden Fermat-Zahlen zusammengesetzt.

### Primzahltripel und Muster

Unter ungeraden Zahlen sind viele lokale Muster wegen Teilbarkeitsargumenten verboten.
Zum Beispiel kann es kein Primzahltripel der Form

```text
p, p + 2, p + 4
```

mit `p > 3` geben, weil eine der drei Zahlen durch `3` teilbar sein muss.

Das einzige Beispiel ist:

```text
3, 5, 7
```

## Kongruenzen und Restklassen

Für Primzahlen gilt eine Vielzahl arithmetischer Muster modulo kleiner Zahlen.

### Modulo `2`

Jede Primzahl außer `2` ist kongruent zu `1 mod 2`, also ungerade.

### Modulo `6`

Jede Primzahl `p > 3` hat die Form

```text
6k - 1
oder
6k + 1
```

denn Zahlen der Formen `6k`, `6k + 2`, `6k + 3`, `6k + 4` sind durch `2` oder `3` teilbar.

Wichtig:
Nicht jede Zahl der Form `6k ± 1` ist prim.
Es ist nur eine notwendige, keine hinreichende Bedingung.

### Primzahlen in arithmetischen Progressionen

Der Satz von Dirichlet besagt:
Ist `a` teilerfremd zu `d`, dann enthält die Folge

```text
a, a + d, a + 2d, a + 3d, ...
```

unendlich viele Primzahlen.

Beispiele:

- `1, 5, 9, 13, ...` enthält unendlich viele Primzahlen
- `3, 7, 11, 15, ...` enthält unendlich viele Primzahlen

Das ist ein tiefes Resultat über die Verteilung von Primzahlen in Restklassen.

## Zentrale Sätze über Primzahlen

### Satz von Euklid

Es gibt unendlich viele Primzahlen.

### Lemma von Euklid

Wenn eine Primzahl `p` ein Produkt `ab` teilt, dann teilt `p` mindestens einen der Faktoren:

```text
p | ab  =>  p | a oder p | b
```

Dieses Lemma ist einer der Grundpfeiler der elementaren Zahlentheorie.

### Kleiner Satz von Fermat

Ist `p` prim und `a` nicht durch `p` teilbar, dann gilt:

```text
a^(p-1) ≡ 1 mod p
```

Äquivalent:

```text
a^p ≡ a mod p
```

Dieser Satz ist zentral für Primzahltests und Kryptographie.

### Satz von Wilson

Eine Zahl `p > 1` ist genau dann prim, wenn

```text
(p - 1)! ≡ -1 mod p
```

gilt.

Der Satz ist theoretisch elegant, aber für große Zahlen praktisch nicht effizient.

### Satz von Euler

Für teilerfremde Zahlen `a` und `n` gilt:

```text
a^φ(n) ≡ 1 mod n
```

Der kleine Satz von Fermat ist der Spezialfall `n = p`.

## Primfaktorzerlegung und Teilbarkeit

Die Primfaktorzerlegung liefert viele weitere Aussagen:

### Anzahl der Teiler

Ist

```text
n = p_1^a1 * p_2^a2 * ... * p_k^ak
```

dann ist die Anzahl positiver Teiler:

```text
(a1 + 1)(a2 + 1)...(ak + 1)
```

### Summe der Teiler

Die Teilersumme `σ(n)` lässt sich ebenfalls direkt aus der Primfaktorzerlegung berechnen.

### Größter gemeinsamer Teiler und kleinstes gemeinsames Vielfaches

Aus den Exponenten der Primfaktorzerlegung folgen elegante Formeln für `ggT` und `kgV`.

## Primzahlen und analytische Zahlentheorie

Hier verbindet sich Zahlentheorie mit Analysis.

### Euler-Produkt der Zetafunktion

Für `Re(s) > 1` gilt:

```text
ζ(s) = ∏ 1 / (1 - p^(-s))
```

wobei das Produkt über alle Primzahlen läuft.

Dieses Euler-Produkt zeigt unmittelbar:

- Primzahlen steuern die multiplikative Struktur der natürlichen Zahlen
- Analysis kann Aussagen über Primzahlen liefern

### Divergenz der Summe der Kehrwerte der Primzahlen

Die Reihe

```text
Σ 1/p
```

über alle Primzahlen divergiert.

Das bedeutet:
Primzahlen werden zwar seltener, aber nicht so schnell, dass ihre Kehrwerte eine konvergente Reihe bilden.

### Riemannsche Zetafunktion

Die Nullstellen der Zetafunktion sind eng mit dem Fehlerterm im Primzahlsatz verknüpft.
Darum ist die Riemannsche Vermutung eine der wichtigsten offenen Fragen der Mathematik.

## Die Riemannsche Vermutung

Die Vermutung besagt vereinfacht:
Alle nichttrivialen Nullstellen der Riemannschen Zetafunktion haben Realteil `1/2`.

Warum das wichtig ist:

- Sie würde die Verteilung der Primzahlen sehr präzise kontrollieren.
- Viele Abschätzungen in der Zahlentheorie würden deutlich schärfer werden.
- Sie gehört zu den Millennium-Problemen.

Bis heute ist sie unbewiesen.

## Primzahllücken

Die Differenz zwischen zwei benachbarten Primzahlen heißt Primzahllücke.

Beispiele:

```text
3 - 2 = 1
5 - 3 = 2
7 - 5 = 2
11 - 7 = 4
13 - 11 = 2
```

Bekannte Tatsachen:

- Es gibt beliebig große Primzahllücken.
- Trotzdem treten auch kleine Lücken sehr häufig auf.
- Ob es unendlich viele Lücken der Größe `2` gibt, ist offen.

Warum es beliebig große Lücken gibt:
Für jedes `n` sind die Zahlen

```text
(n + 1)! + 2,
(n + 1)! + 3,
...,
(n + 1)! + (n + 1)
```

alle zusammengesetzt.

## Goldbach und andere berühmte Vermutungen

### Goldbachsche Vermutung

Jede gerade Zahl größer als `2` soll sich als Summe zweier Primzahlen schreiben lassen.

Beispiele:

```text
4 = 2 + 2
6 = 3 + 3
8 = 3 + 5
10 = 5 + 5
12 = 5 + 7
```

Die Vermutung ist extrem gut numerisch getestet, aber bis heute nicht allgemein bewiesen.

### Schwache Goldbachsche Vermutung

Jede ungerade Zahl größer als `5` ist Summe von drei Primzahlen.

Diese Aussage ist bewiesen.

### Zwillingsprimzahlvermutung

Es gibt unendlich viele Primzahlpaare mit Abstand `2`.

Bis heute offen.

### Legendre-Vermutung

Zwischen `n^2` und `(n + 1)^2` soll immer eine Primzahl liegen.

Ebenfalls offen.

### Brocards Problem

Zwischen `n^2` und `(n + 1)^2` sollen mindestens vier Primzahlen liegen, für `n > 1`.

Auch das ist offen.

## Ergebnisse über kleine Primzahllücken

Große Fortschritte der modernen Zahlentheorie betreffen beschränkte Primzahllücken.

Bewiesen ist:
Es gibt unendlich viele Primzahlpaare mit Abstand kleiner als einer festen endlichen Konstante.

Das ist deutlich schwächer als die Zwillingsprimzahlvermutung, aber ein tiefes Resultat.

## Primzahltests

Ein Primzahltest entscheidet, ob eine gegebene Zahl prim ist.

### Probeteilung

Die einfachste Methode:
Man testet Teilbarkeit durch alle Primzahlen bis `sqrt(n)`.

Denn:
Hat `n` einen echten Teiler, dann auch einen `<= sqrt(n)`.

### Sieb des Eratosthenes

Damit bestimmt man alle Primzahlen bis zu einer Grenze `N`.

Prinzip:

1. Schreibe alle Zahlen von `2` bis `N` auf.
2. Streiche Vielfache von `2`.
3. Nimm die nächste nicht gestrichene Zahl.
4. Streiche deren Vielfache.
5. Wiederhole das.

Das ist eines der wichtigsten klassischen Verfahren.

### Verbesserte Siebe

Es gibt zahlreiche Varianten:

- segmentiertes Sieb
- lineares Sieb
- Rad-Sieb
- Atkin-Sieb

Sie sind wichtig für große Suchräume und speichereffiziente Berechnungen.

### Fermat-Test

Auf Basis des kleinen Satzes von Fermat kann man Kandidaten testen.
Allerdings gibt es Pseudoprimzahlen und Carmichael-Zahlen, die den Test täuschen.

### Miller-Rabin-Test

Ein sehr wichtiger probabilistischer Primzahltest.
Er ist in der Praxis schnell und zuverlässig.
Für viele Zahlenbereiche kann er sogar deterministisch gemacht werden, wenn man passende Basen verwendet.

### AKS-Primzahltest

Der AKS-Test zeigte theoretisch, dass Primzahltestung in polynomialer Zeit deterministisch möglich ist.

Er ist ein Meilenstein der Komplexitätstheorie, aber praktisch meist langsamer als spezialisierte Verfahren.

### Lucas-Lehmer-Test

Ein spezieller Test für Mersenne-Zahlen `2^p - 1`.
Er ist zentral bei der Suche nach Mersenne-Primzahlen.

## Primfaktorzerlegung als algorithmisches Problem

Es ist leicht, große Primzahlen zu multiplizieren.
Es ist aber deutlich schwerer, aus einem großen Produkt die Primfaktoren zurückzugewinnen.

Diese Asymmetrie ist die Grundlage klassischer Public-Key-Kryptographie.

Wichtige Faktorisierungsverfahren:

- Pollard-Rho
- Pollard-p-1
- Quadratisches Sieb
- General Number Field Sieve
- Elliptic Curve Method

## Primzahlen in der Kryptographie

### RSA

RSA verwendet typischerweise das Produkt zweier großer Primzahlen.
Die Sicherheit beruht auf der Schwierigkeit, dieses Produkt zu faktorisieren.

### Diffie-Hellman

Das klassische Diffie-Hellman-Verfahren arbeitet in multiplikativen Gruppen modulo einer großen Primzahl oder in verwandten Strukturen.

### Elliptische Kurven

Auch in der Elliptische-Kurven-Kryptographie spielen Primzahlen eine große Rolle:

- bei endlichen Körpern
- bei Gruppenordnungen
- bei Sicherheitsparametern

## Besondere Phänomene

### Carmichael-Zahlen

Das sind zusammengesetzte Zahlen, die gewisse Fermat-Tests bestehen und daher wie Primzahlen wirken können.

### Wie zufällig verhalten sich Primzahlen?

Lokal wirken Primzahlen oft zufällig.
Global zeigen sie jedoch feine deterministische Struktur.

Diese Spannung zwischen Zufälligkeit und Gesetzmäßigkeit ist eines der zentralen Themen der Zahlentheorie.

### Primzahlen und Zufallsmodelle

Es gibt heuristische Modelle, die Primzahlen so behandeln, als träte die Eigenschaft "prim" bei Zahlen nahe `n` mit Wahrscheinlichkeit ungefähr `1 / ln(n)` auf.
Solche Modelle sind nicht exakt, liefern aber oft überraschend gute Vorhersagen.

## Primzahlen in Polynomen und Spezialfolgen

Ein klassisches Thema ist die Frage, wann Formeln viele Primzahlen erzeugen.

### Eulersches Polynom

Das Polynom

```text
n^2 + n + 41
```

liefert für viele kleine `n` Primzahlen.
Es erzeugt aber nicht für alle `n` Primzahlen.

### Allgemeines Problem

Es gibt kein nichtkonstantes Polynom mit ganzzahligen Koeffizienten, das für alle natürlichen `n` nur Primzahlen liefert.

## Geometrische und kombinatorische Aspekte

Primzahlen sind zwar primär arithmetische Objekte, aber sie tauchen auch in anderen Kontexten auf:

- in zyklischen Gruppen der Primzahlordnung
- in endlichen Körpern `F_p`
- in projektiven und algebraischen Strukturen
- in Kombinatorik und additiver Zahlentheorie

Ein besonders wichtiges Beispiel:
Für jede Primzahlpotenz `p^k` existiert ein endlicher Körper mit genau `p^k` Elementen.

## Primzahlen in der additiven Zahlentheorie

Hier untersucht man Summen von Primzahlen und Muster in Primzahlmengen.

Wichtige Themen:

- Goldbach-artige Probleme
- arithmetische Progressionen von Primzahlen
- additive Basen
- Kreis-Methode

Ein berühmtes Resultat:
Es gibt beliebig lange arithmetische Progressionen aus Primzahlen.

Beispiel:

```text
5, 11, 17, 23, 29
```

ist eine arithmetische Progression aus Primzahlen der Länge `5`.

## Computergestützte Suche nach großen Primzahlen

Sehr große Primzahlen werden meist mit spezialisierten Algorithmen und verteiltem Rechnen gesucht.
Besonders populär sind Mersenne-Primzahlen, weil sie sich effizient testen lassen.

Solche Projekte sind wichtig für:

- Rekordberechnungen
- Tests großer Arithmetik-Bibliotheken
- algorithmische Forschung
- numerische Verifikation theoretischer Vermutungen

## Didaktisch wichtige Einsichten

Wenn man Primzahlen verstehen will, sind folgende Punkte besonders zentral:

- Primzahlen sind die irreduziblen Bausteine der natürlichen Zahlen.
- Ihre lokale Verteilung wirkt chaotisch, ihre globale Verteilung folgt präzisen Gesetzen.
- Viele einfache Fragen über Primzahlen sind bis heute offen.
- Tiefe Analysis, Algebra und Kombinatorik greifen bei Primzahlen ineinander.
- Primzahlen sind nicht nur theoretisch, sondern auch technologisch relevant.

## Typische Fehlvorstellungen

### "Alle ungeraden Zahlen sind prim"

Falsch.
Beispiele:

```text
9, 15, 21, 25
```

sind ungerade, aber nicht prim.

### "Jede Zahl der Form `6k ± 1` ist prim"

Falsch.
Beispiele:

```text
25 = 6 * 4 + 1
35 = 6 * 6 - 1
```

beide sind zusammengesetzt.

### "Primzahlen kommen irgendwann nicht mehr vor"

Falsch.
Es gibt unendlich viele Primzahlen.

## Offene Probleme

Trotz jahrhundertelanger Forschung sind viele Kernfragen ungelöst:

- Riemannsche Vermutung
- Zwillingsprimzahlvermutung
- Goldbachsche Vermutung
- Legendre-Vermutung
- genaue Struktur der Primzahllücken
- viele Fragen zu Primzahlen in Spezialfolgen

Das macht das Gebiet zugleich klassisch und hochaktuell.

## Fazit

Primzahlen sind eines der grundlegendsten und tiefsten Objekte der Mathematik.
Sie erscheinen elementar, weil sie leicht definiert sind, und zugleich extrem komplex, weil ihre Verteilung subtile Muster aufweist.

Fast jede größere Richtung der Zahlentheorie berührt Primzahlen:

- elementare Zahlentheorie
- analytische Zahlentheorie
- algebraische Zahlentheorie
- algorithmische Zahlentheorie
- Kryptographie
- additive Kombinatorik

Wer Primzahlen studiert, bewegt sich genau an der Grenze zwischen einfacher Definition und tiefer mathematischer Struktur.
