# Die e-Funktion

## Ausgangspunkt

Mit `e` ist hier die Eulersche Zahl gemeint:

```text
e = 2,718281828459...
```

Sie ist eine der wichtigsten Konstanten der Mathematik.
Sobald es um stetiges Wachstum, Zerfall, Zinseszins, Differentialgleichungen oder den natürlichen Logarithmus geht, taucht `e` fast zwangsläufig auf.

## Was mit "e-Funktion" meist gemeint ist

Oft meint man mit der e-Funktion die Exponentialfunktion

```text
f(x) = e^x
```

Das ist diejenige Exponentialfunktion, die sich beim Ableiten nicht verändert:

```text
(e^x)' = e^x
```

Genau das macht sie so zentral.
Sie ist die natürliche Form von Wachstum und Zerfall.

## Die wichtigsten Zusammenhänge

### 1. Exponentialfunktion

Die Grundfunktion ist

```text
f(x) = e^x
```

Eigenschaften:

- immer positiv
- streng monoton steigend
- bei `x = 0` gilt `e^0 = 1`
- Ableitung ist wieder dieselbe Funktion
- Integral ist wieder dieselbe Funktion

Also:

```text
f'(x) = e^x
```

und

```text
∫ e^x dx = e^x + C
```

### 2. Allgemeine e-Funktion

In Anwendungen taucht fast immer diese Form auf:

```text
f(x) = a * e^(bx)
```

Dabei bedeutet:

- `a`: Anfangswert
- `b`: Wachstums- oder Zerfallsrate

Zusammenhang:

- `b > 0`: exponentielles Wachstum
- `b < 0`: exponentieller Zerfall
- `b = 0`: konstanter Wert `a`

Ableitung:

```text
f'(x) = ab * e^(bx)
```

Man sieht: Die Änderungsrate ist proportional zum aktuellen Bestand.
Genau das ist das Kennzeichen exponentieller Prozesse.

### 3. Natürlicher Logarithmus

Die Umkehrfunktion von `e^x` ist der natürliche Logarithmus:

```text
ln(x)
```

Es gilt:

```text
ln(e^x) = x
```

und

```text
e^(ln(x)) = x
```

für `x > 0`.

Dieser Zusammenhang ist fundamental:

- `e^x` baut auf
- `ln(x)` holt den Exponenten wieder heraus

### 4. Grenzwert-Definition von e

Die Zahl `e` kann über einen Grenzwert definiert werden:

```text
e = lim (1 + 1/n)^n    für n -> unendlich
```

Das ist der klassische Zusammenhang mit Zinseszins.
Wenn Verzinsung immer feiner aufgeteilt wird, nähert sich der Wachstumsfaktor der Zahl `e`.

Allgemeiner:

```text
e^x = lim (1 + x/n)^n    für n -> unendlich
```

## Warum e so besonders ist

Die Funktion `e^x` ist die eindeutig natürliche Exponentialfunktion, weil sie die Differentialgleichung

```text
y' = y
```

löst.

Das bedeutet:

- der Bestand erzeugt seine eigene Änderungsrate
- Wachstum hängt direkt vom aktuellen Zustand ab
- viele reale Prozesse folgen genau diesem Muster

Typische Beispiele:

- Bevölkerungswachstum
- Kapitalwachstum
- radioaktiver Zerfall
- Abkühlung
- Lade- und Entladevorgänge

## Wichtige Ableitungen

Ein paar Standardformen:

```text
(e^x)' = e^x
```

```text
(e^(ax))' = a * e^(ax)
```

```text
(a * e^(bx))' = ab * e^(bx)
```

```text
(e^(u(x)))' = u'(x) * e^(u(x))
```

Der letzte Fall ist die Kettenregel.

## Typische Integrale

```text
∫ e^x dx = e^x + C
```

```text
∫ e^(ax) dx = (1/a) * e^(ax) + C
```

für `a != 0`.

## Verhalten des Graphen

Für `f(x) = e^x` gilt:

- Der Graph schneidet die y-Achse bei `1`
- Für große positive `x` wächst er sehr schnell
- Für große negative `x` nähert er sich `0`, ohne sie zu erreichen
- Die x-Achse ist eine horizontale Asymptote

Für `f(x) = e^(-x)` kehrt sich das Verhalten in der Zeitrichtung um:

- großer Wert links
- Zerfall nach rechts

## Verbindung zu Differentialgleichungen

Wenn eine Größe proportional zu sich selbst wächst oder fällt, erhält man

```text
y' = ky
```

Die Lösung ist

```text
y(x) = C * e^(kx)
```

Das ist einer der wichtigsten Gründe, warum `e` in Physik, Technik, Biologie und Wirtschaft ständig auftaucht.

## Verbindung zu Potenzen und Logarithmen

Jede Exponentialfunktion mit Basis `a > 0` kann mit `e` geschrieben werden:

```text
a^x = e^(x ln(a))
```

Das ist ein Schlüsselsatz.
Er zeigt:

- `e` ist nicht nur eine mögliche Basis
- `e` ist die natürliche Basis, auf die sich alle anderen zurückführen lassen

## Reihendarstellung

Die e-Funktion hat die Potenzreihe

```text
e^x = 1 + x + x^2/2! + x^3/3! + x^4/4! + ...
```

Setzt man `x = 1`, erhält man:

```text
e = 1 + 1 + 1/2! + 1/3! + 1/4! + ...
```

Diese Reihe verbindet `e` mit Analysis, Approximation und numerischer Berechnung.

## Kompakte Gesamtübersicht

Die Zahl `e` verbindet mehrere Themen in einer einzigen Struktur:

- Exponentialfunktion: `e^x`
- Umkehrfunktion: `ln(x)`
- Ableitung: Funktion bleibt erhalten
- Integral: Funktion bleibt erhalten
- Wachstum und Zerfall: `a * e^(bx)`
- Grenzwert: `(1 + 1/n)^n`
- Differentialgleichungen: Lösungen von `y' = ky`
- Reihenentwicklung: `e^x = sum x^n / n!`

## Kurzformel zum Merken

Wenn die Änderungsrate proportional zum aktuellen Zustand ist, dann steckt fast immer eine e-Funktion dahinter.
