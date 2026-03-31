# Kettenbrüche

## Überblick

Kettenbrüche sind eine besonders tiefe und elegante Darstellung von Zahlen.
Sie verbinden elementare Arithmetik, den euklidischen Algorithmus, rationale Approximation, irrationale Zahlen, Zahlentheorie und algebraische Strukturen.

Ihr besonderer Reiz liegt darin, dass sie zugleich:

- sehr einfach definiert sind
- eine klare algorithmische Struktur besitzen
- beste rationale Näherungen liefern
- tiefe Aussagen über irrationale und quadratische Zahlen erlauben

Kettenbrüche gehören damit zu den klassischen Werkzeugen der Zahlentheorie und Analysis.

## Grundidee

Ein Kettenbruch ist ein Ausdruck der Form

```text
a0 + 1/(a1 + 1/(a2 + 1/(a3 + ...)))
```

Üblicher schreibt man kompakt:

```text
[a0; a1, a2, a3, ...]
```

Dabei gilt meist:

- `a0` ist eine ganze Zahl
- `a1, a2, a3, ...` sind positive ganze Zahlen

Beispiel:

```text
[1; 2, 2, 2] = 1 + 1/(2 + 1/(2 + 1/2))
```

## Warum Kettenbrüche natürlich sind

Die Dezimaldarstellung ist additiv aufgebaut:

```text
3,14159...
```

Kettenbrüche sind dagegen verschachtelte Bruchdarstellungen.
Sie sind nicht in erster Linie für das Rechnen im Alltag gedacht, sondern für strukturelles Verständnis.

Ihre Stärke liegt darin, dass sie die Teilbarkeit und die ganzzahlige Struktur einer Zahl sichtbar machen.
Insbesondere entstehen sie direkt aus wiederholtem Dividieren mit Rest.

## Endliche und unendliche Kettenbrüche

### Endliche Kettenbrüche

Ein endlicher Kettenbruch hat nur endlich viele Glieder:

```text
[a0; a1, a2, ..., an]
```

Jeder endliche Kettenbruch stellt eine rationale Zahl dar.

Beispiel:

```text
[1; 2, 2] = 1 + 1/(2 + 1/2) = 1 + 1/(5/2) = 1 + 2/5 = 7/5
```

### Unendliche Kettenbrüche

Ein unendlicher Kettenbruch hat unendlich viele Glieder:

```text
[a0; a1, a2, a3, ...]
```

Solche Kettenbrüche stellen in vielen Fällen irrationale Zahlen dar.

Grundprinzip:

- rationale Zahlen <-> endliche Kettenbrüche
- irrationale Zahlen <-> unendliche Kettenbrüche

Das ist ein fundamentaler Zusammenhang.

## Erste Beispiele

### Beispiel 1: Eine rationale Zahl

Nehmen wir

```text
13/8
```

Dann gilt:

```text
13/8 = 1 + 5/8
8/5 = 1 + 3/5
5/3 = 1 + 2/3
3/2 = 1 + 1/2
2/1 = 2
```

Daraus folgt:

```text
13/8 = [1; 1, 1, 1, 2]
```

### Beispiel 2: Die goldene Zahl

Die goldene Zahl

```text
φ = (1 + sqrt(5)) / 2
```

erfüllt

```text
φ = 1 + 1/φ
```

Deshalb hat sie den Kettenbruch

```text
[1; 1, 1, 1, 1, ...]
```

also einen rein periodischen, extrem einfachen unendlichen Kettenbruch.

### Beispiel 3: Quadratwurzel aus 2

Für

```text
sqrt(2)
```

gilt:

```text
sqrt(2) = [1; 2, 2, 2, 2, ...]
```

Auch hier erscheint ein periodisches Muster.

## Wie man einen Kettenbruch konstruiert

Die Konstruktion beruht auf wiederholtem Abspalten des ganzzahligen Anteils.

Sei `x > 0`.

### Schritt 1

Setze

```text
a0 = floor(x)
```

Dann ist

```text
x = a0 + r0
```

mit `0 <= r0 < 1`.

Ist `r0 = 0`, dann ist `x` ganzzahlig und der Prozess endet.

### Schritt 2

Falls `r0 > 0`, bilde den Kehrwert:

```text
x1 = 1 / r0
```

Dann setze

```text
a1 = floor(x1)
```

und schreibe

```text
x1 = a1 + r1
```

mit `0 <= r1 < 1`.

### Fortsetzung

So fährt man fort:

```text
x_{n+1} = 1 / r_n
```

und

```text
a_{n+1} = floor(x_{n+1})
```

Dadurch entsteht die Folge

```text
[a0; a1, a2, a3, ...]
```

## Zusammenhang mit dem euklidischen Algorithmus

Der tiefste elementare Zusammenhang ist:
Die Kettenbruchentwicklung einer rationalen Zahl ist genau der euklidische Algorithmus in anderer Form.

### Beispiel mit `43/19`

Man dividiert mit Rest:

```text
43 = 2 * 19 + 5
19 = 3 * 5 + 4
5 = 1 * 4 + 1
4 = 4 * 1 + 0
```

Die Quotienten sind:

```text
2, 3, 1, 4
```

Daher:

```text
43/19 = [2; 3, 1, 4]
```

Das ist kein Zufall.
Bei rationalen Zahlen liest man die Kettenbruchkoeffizienten direkt aus den Quotienten des euklidischen Algorithmus ab.

## Konvergenten

Schneidet man einen unendlichen Kettenbruch nach endlich vielen Gliedern ab, erhält man eine rationale Näherung.
Diese Näherungen heißen Konvergenten.

Ist

```text
x = [a0; a1, a2, a3, ...]
```

dann sind die Konvergenten:

```text
[a0]
[a0; a1]
[a0; a1, a2]
[a0; a1, a2, a3]
...
```

### Beispiel bei `sqrt(2) = [1; 2, 2, 2, ...]`

Die ersten Konvergenten sind:

```text
1
3/2
7/5
17/12
41/29
99/70
...
```

Diese Brüche nähern `sqrt(2)` sehr gut an:

```text
sqrt(2) ≈ 1,41421356...
1     = 1,0
3/2   = 1,5
7/5   = 1,4
17/12 = 1,41666...
41/29 = 1,41379...
```

Die Näherungen springen abwechselnd über und unter den wahren Wert.

## Rekursionsformeln für Konvergenten

Schreibt man den `n`-ten Konvergenten als

```text
p_n / q_n
```

dann gelten die Rekursionen:

```text
p_n = a_n p_{n-1} + p_{n-2}
q_n = a_n q_{n-1} + q_{n-2}
```

mit den Startwerten

```text
p_{-2} = 0,   p_{-1} = 1
q_{-2} = 1,   q_{-1} = 0
```

Diese Formeln sind zentral, weil man damit alle Näherungsbrüche systematisch berechnen kann.

### Beispiel

Für `sqrt(2)` mit `a0 = 1` und danach ständig `2` ergibt sich:

```text
p0 = 1, q0 = 1
p1 = 3, q1 = 2
p2 = 7, q2 = 5
p3 = 17, q3 = 12
```

also genau die Folge der Konvergenten oben.

## Warum Konvergenten so gut sind

Kettenbrüche liefern nicht bloß irgendwelche rationale Näherungen, sondern besonders gute.

Eine der wichtigsten Einsichten lautet:
Die Konvergenten sind die besten rationalen Approximationen mit beschränktem Nenner.

Anschaulich heißt das:
Wenn man eine Zahl durch einen Bruch mit kleinem Nenner approximieren will, landet man sehr oft bei einem Konvergenten.

Das erklärt, warum etwa

```text
22/7
355/113
```

so gute Näherungen für `π` sind:
Sie entstehen aus seiner Kettenbruchentwicklung.

## Fehlerabschätzung

Für Konvergenten `p_n / q_n` gilt eine sehr starke Kontrolle des Fehlers.

Typisch ist eine Abschätzung der Form:

```text
|x - p_n/q_n| < 1 / q_n^2
```

genauer oft sogar:

```text
|x - p_n/q_n| < 1 / (q_n q_{n+1})
```

Das ist bemerkenswert, weil der Fehler quadratisch mit der Größe des Nenners fällt.

## Eindeutigkeit

Jede irrationale Zahl besitzt genau eine unendliche einfache Kettenbruchentwicklung.

Bei rationalen Zahlen gibt es fast Eindeutigkeit, aber eine kleine Besonderheit:

```text
[a0; ..., an]
```

und

```text
[a0; ..., an - 1, 1]
```

können dieselbe rationale Zahl beschreiben, wenn man die Enddarstellung passend umformt.

Beispiel:

```text
[1; 2] = [1; 1, 1]
```

Daher wählt man für rationale Zahlen meist die Darstellung mit letztem Eintrag `> 1`.

## Endliche Kettenbrüche und rationale Zahlen

Ein fundamentaler Satz lautet:

- Jede rationale Zahl besitzt einen endlichen einfachen Kettenbruch.
- Jeder endliche einfache Kettenbruch ist rational.

Warum das so ist:

- Bei rationalen Zahlen endet der euklidische Algorithmus.
- Umgekehrt kann man jeden endlichen Kettenbruch durch Rückwärtsrechnen zu einem gewöhnlichen Bruch auswerten.

## Unendliche Kettenbrüche und irrationale Zahlen

Für irrationale Zahlen endet der Prozess nie.
Es entsteht daher ein unendlicher Kettenbruch.

Das ist konzeptionell sehr wichtig:
Die Unendlichkeit der Darstellung entspricht direkt der Unmöglichkeit, die Zahl als endlichen Bruch `m/n` zu schreiben.

## Periodische Kettenbrüche

Ein unendlicher Kettenbruch heißt periodisch, wenn sich die Koeffizienten ab einem Punkt wiederholen.

Beispiel:

```text
[1; 2, 2, 2, 2, ...]
```

oder

```text
[1; 1, 1, 1, 1, ...]
```

### Rein periodisch

Rein periodisch heißt:
Die Periode beginnt sofort.

Beispiel:

```text
[1; 1, 1, 1, ...]
```

### Schließlich periodisch

Schließlich periodisch heißt:
Zunächst kommen einige Anfangsglieder, danach wiederholt sich ein Block.

## Der Satz von Lagrange

Ein tiefer klassischer Satz lautet:

Eine reelle Zahl hat genau dann einen schließlich periodischen einfachen Kettenbruch, wenn sie eine quadratische Irrationalzahl ist.

Quadratische Irrationalzahl bedeutet:
Sie ist Lösung einer quadratischen Gleichung mit ganzzahligen Koeffizienten, aber nicht rational.

Beispiele:

- `sqrt(2)`
- `sqrt(3)`
- `(1 + sqrt(5)) / 2`

Das ist eine der schönsten Brücken zwischen einfacher Bruchstruktur und algebraischer Zahlentheorie.

## Beispiel: `sqrt(2)` im Detail

Setze

```text
x = sqrt(2)
```

Dann ist:

```text
1 < x < 2
```

also

```text
a0 = 1
```

Weiter:

```text
x - 1 = sqrt(2) - 1
1 / (x - 1) = 1 / (sqrt(2) - 1)
```

Rationalisieren liefert:

```text
1 / (sqrt(2) - 1) = sqrt(2) + 1
```

und damit

```text
sqrt(2) + 1 = 2 + (sqrt(2) - 1)
```

Der ganzzahlige Anteil ist also immer wieder `2`.
Deshalb entsteht:

```text
sqrt(2) = [1; 2, 2, 2, 2, ...]
```

Dieses Beispiel zeigt sehr schön, wie algebraische Struktur zu periodischer Kettenbruchstruktur führt.

## Beispiel: die goldene Zahl `φ`

Die Gleichung

```text
φ = 1 + 1/φ
```

ist bereits die Kettenbruchgleichung selbst.
Darum hat `φ` den einfachsten unendlichen Kettenbruch überhaupt:

```text
[1; 1, 1, 1, 1, ...]
```

Die Konvergenten sind:

```text
1, 2, 3/2, 5/3, 8/5, 13/8, ...
```

also Quotienten aufeinanderfolgender Fibonacci-Zahlen.

Darin zeigt sich ein tiefer Zusammenhang zwischen:

- Kettenbrüchen
- Fibonacci-Folge
- goldener Zahl
- optimalen Näherungen

## Beispiel: `π`

Die Zahl `π` hat keinen periodischen einfachen Kettenbruch.
Ihre Entwicklung beginnt:

```text
[3; 7, 15, 1, 292, ...]
```

Darum erhält man die berühmten Näherungen:

```text
3
22/7
333/106
355/113
...
```

Besonders `355/113` ist erstaunlich genau.

Der große Koeffizient `292` erklärt, warum diese Näherung ungewöhnlich gut ist.

## Matrixsicht

Kettenbrüche lassen sich elegant mit Matrizen beschreiben.
Jeder Schritt

```text
x -> a + 1/x
```

entspricht einer linearen gebrochenen Transformation.

Der Koeffizient `a` kann durch die Matrix

```text
[a 1]
[1 0]
```

kodiert werden.

Das Produkt solcher Matrizen erzeugt die Zähler und Nenner der Konvergenten.

Diese Sicht ist besonders nützlich in:

- Zahlentheorie
- Modulgruppen
- dynamischen Systemen
- algorithmischen Ableitungen

## Kettenbrüche als beste Approximationen

Warum sind Kettenbrüche so wichtig?
Weil sie die systematisch besten Brüche mit kleinen Nennern liefern.

Wenn man eine irrationale Zahl `x` durch

```text
p/q
```

annähern will, möchte man:

- kleinen Fehler
- kleinen Nenner

Diese beiden Ziele konkurrieren miteinander.
Kettenbrüche liefern genau hier den optimalen Kompromiss.

Das ist in Theorie und Praxis zentral, zum Beispiel bei:

- numerischen Näherungen
- Resonanzproblemen
- Gittermethoden
- diophantischen Gleichungen

## Diophantische Approximation

Die Theorie der Kettenbrüche ist ein Grundpfeiler der diophantischen Approximation.
Hier untersucht man, wie gut reelle Zahlen durch rationale Zahlen approximierbar sind.

Wichtige Leitfrage:
Wie klein kann

```text
|x - p/q|
```

in Abhängigkeit von `q` werden?

Kettenbrüche geben darauf die natürlichste und oft schärfste Antwort.

## Schlechte und gute Approximierbarkeit

Nicht alle irrationalen Zahlen lassen sich gleich gut approximieren.

### Besonders schlecht approximierbar

Die goldene Zahl `φ` gilt in einem präzisen Sinn als eine der am schlechtesten rational approximierbaren irrationalen Zahlen.

Der Grund ist:
Ihr Kettenbruch hat nur Einsen.
Dadurch wachsen die Nenner ihrer Konvergenten relativ langsam, und die Approximation bleibt gewissermaßen maximal widerständig.

### Besonders gut approximierbar

Wenn in der Kettenbruchentwicklung sehr große Koeffizienten auftauchen, entstehen oft außergewöhnlich gute Näherungsbrüche.
Genau das geschieht bei `π`.

## Pell-Gleichung

Ein besonders tiefer Zusammenhang besteht zur Pell-Gleichung:

```text
x^2 - Dy^2 = 1
```

für nichtquadratisches `D`.

Die Lösungen dieser Gleichung lassen sich aus den Konvergenten von `sqrt(D)` gewinnen.

Das ist ein klassisches Highlight der elementaren Zahlentheorie:

- Quadratwurzeln erzeugen periodische Kettenbrüche
- periodische Kettenbrüche liefern Lösungen einer diophantischen Gleichung

## Anwendungen

Kettenbrüche tauchen in vielen Bereichen auf:

- rationale Approximation irrationaler Zahlen
- Zahlentheorie und diophantische Gleichungen
- Analyse von Algorithmen
- Dynamik und symbolische Codierung
- Computeralgebra
- Gitterprobleme und numerische Methoden
- Musik- und Resonanztheorie bei Verhältnissen

## Algorithmische Sicht

Kettenbrüche sind algorithmisch besonders attraktiv, weil sie aus sehr einfachen Operationen bestehen:

- ganzzahligen Anteil nehmen
- Rest bestimmen
- Kehrwert bilden

Das macht sie robust und effizient.
In Computeralgebra und exakter Arithmetik sind sie deshalb ein natürliches Werkzeug.

## Typische Rechenstrategie

Wenn man eine Zahl per Kettenbruch untersuchen will, geht man oft so vor:

1. Bestimme den ganzzahligen Anteil.
2. Ziehe ihn ab.
3. Bilde den Kehrwert des Rests.
4. Wiederhole den Prozess.
5. Berechne die Konvergenten.
6. Untersuche Muster, Perioden und Approximationseigenschaften.

## Typische Fehlvorstellungen

### "Kettenbrüche sind nur eine andere Schreibweise"

Nein.
Sie sind nicht bloß kosmetisch anders, sondern tragen tiefe arithmetische Information.

### "Dezimalbrüche sind immer praktischer"

Für Alltagsrechnungen oft ja.
Für Struktur, Approximation und Zahlentheorie sind Kettenbrüche häufig deutlich aussagekräftiger.

### "Nur irrationale Zahlen haben interessante Kettenbrüche"

Auch rationale Zahlen sind wichtig, weil ihre Kettenbruchentwicklung direkt mit dem euklidischen Algorithmus zusammenhängt.

## Zentrale Merksätze

- Jede rationale Zahl hat einen endlichen einfachen Kettenbruch.
- Jede irrationale Zahl hat einen unendlichen einfachen Kettenbruch.
- Konvergenten sind ausgezeichnete rationale Näherungen.
- Quadratische Irrationalzahlen haben genau die schließlich periodischen Kettenbrüche.
- Der euklidische Algorithmus und Kettenbrüche sind zwei Seiten derselben Struktur.

## Fazit

Kettenbrüche sind eines der klarsten Beispiele dafür, wie aus einer einfachen Definition tiefe Mathematik entsteht.
Sie machen rationale und irrationale Zahlen auf strukturelle Weise sichtbar und liefern zugleich konkrete Rechenverfahren.

Ihre besondere Stärke ist die Verbindung von:

- Arithmetik
- Algebra
- Approximation
- Algorithmik

Wer Kettenbrüche versteht, versteht nicht nur eine Darstellungsform von Zahlen, sondern ein ganzes Netzwerk mathematischer Zusammenhänge.
