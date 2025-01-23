---
lang: de
---

# Anforderungen an das fertige Projekt

Realisieren Sie mit Hilfe des Presets das Spiel [Breitband](SPIELREGELN.md) als Computerspiel in Java.

**-->>** LESEN SIE DIESE DATEI BITTE AUFMERKSAM DURCH! **<<--**

[TOC]

## Allgemein

- Alle Klassen und Schnittstellen gehören zu einem Package, welches zum Beispiel mit dem Wort `breitband` oder Ihrem Projektnamen beginnt.
- Machen Sie sich mit den vorgegebenen Klassen des [Package `breitband.preset`](https://app.pages.gwdg.de/2024ss/breitband-preset/doc/breitband/preset/package-summary.html) vertraut.
- Die vorgegebenen Klassen, Enumerations und Schnittstellen des Package `breitband.preset` dürfen nicht direkt verändert werden.\
  Erweiterung mit Hilfe von Vererbung ist grundsätzlich für alle Klasse, Enumerations und Schnittstellen des Package `breitband.preset` zulässig.\
  (Falls Sie eine vorgegebene Klasse, Enumeration oder Schnittstellen verändert haben wollen, schreiben Sie bitte ein [Issue mit Ihrem Bug Report oder Feature Request](https://gitlab.gwdg.de/app/2024ss/breitband-preset/-/issues).)
- Kommentieren Sie den Quellcode ausführlich. Verwenden Sie JavaDoc für das Application Programming Interface (API) und kommentieren Sie sonst wie üblich.
- Kommentieren Sie für jede Datei und ggf. für jeden Abschnitt der Dateien in ihrem Quellcode wer daran programmiert hat.
- Verwenden Sie Ant zum automatisierten Übersetzen des Programms und zum Erstellen der Dokumentation.
- Ihr GitLab Repo soll nur das notwendigste beinhalten:
    - Die Bibliotheken die sie verwenden.
    - Ihren Quellcode.
    - Das Ant Buildfile.
    - Die README Datei.
    - Ggf. Spielkonfigurationen.
    - Ggf. weitere notwendige Dateien.
- Ihr GitLab Repo soll die folgenden Dinge **definitiv nicht** beinhalten:
    - Den Quellcode des Presets, oder den Quellcode von SAG.
    - Die Markdown Dateien aus dem Preset (Wichtige Dokumente).
    - Aus Ihrem Code kompilierte Klassen.



## Grafische Ein- und Ausgabe

Erstellen Sie eine grafische Ein- und Ausgabe (GUI).

Nutzen Sie für die Grafik die Klasse `sag.SAGPanel` aus der [Simple APP Graphics (SAG) Bibliothek](https://app.pages.gwdg.de/sag/doc), oder nutzen Sie Java Swing.\
(**ACHTUNG:** Es ist empfohlen SAG, statt Java Swing, zu verwenden. Die Grafische Ein- und Ausgabe ist unter Umständen mit Java Swing deutlich schwerer zu programmieren als mit SAG.)

Die GUI soll sich selbständig aktuell halten und Information von einem übergebenen Spielbrett holen.

Die folgenden Anforderungen muss ihre GUI erfüllen:
- Die Regionen, Städte und Kabelstrecken werden auf einer Karte dargestellt.
    - Zu Beginn ist die Karte so positioniert und skallierte, dass sie sich vollständig sichtbar im Fenster befindet.
    - Mit den Pfeiltasten kann die Karte umherbewegt werden.
    - Mit den Tasten '+' und '-' kann das Spielbrett rein- und rausgezoomt werden.
    - Mit der Taste '0' kann die Anfangs-Position und -Skallierung der Karte wieder hergestellt werden.
    - Die Urheberrechtsinformationen zu den Kartendaten aus der Spielkonfiguration werden angezeigt. (Siehe [OSM Attribution Guidelines](https://osmfoundation.org/wiki/Licence/Attribution_Guidelines#Safe_harbour_requirements_for_specific_scenarios).)
    - Für jede Region ist der Name und die Umrisse erkennbar.
    - Für jede Stadt ist die maximale Anzahl an Verbindungen und der Name erkennbar.
    - Für jede Kabelstrecke ist erkennbar welcher Spieler diese gebaut hat, welche Kabelart verwendet wurde und wie viele Kabel verbaut wurden.
- Es wird angezeigt welche Spieler existieren und welchen Punktestand sie haben.
- Es gibt eine Möglichkeit alle ausliegenden Verträge einzusehen.
- Es gibt die Möglichkeit die zugehörigen Städte zu einem Vertrag zu highlighten.
- Es gibt eine Möglichkeit die Inventare aller Spieler einzusehen.
- Es ist ersichtlich welcher Spieler gerade am Zug ist.
- Der (menschliche) Spieler der am Zug ist kann
    - durch einen Linksklick auf einen der ausliegenden Verträge diesen annehmen,
    - auswählen welche und wie viele Kabel ins Inventar aufgenommen werden sollen, und
    - durch Linksklick auf eine Kabelart, gefolgt von Linksklicks auf zwei unterschiedliche Städte eine Kabelstrecke bauen.
    - Anklickbare Elemente und Städte für einen validen Zug werden dabei gehighlightet.
    - Es gibt außerdem eine Möglichkeit keine Kabelstrecke zu bauen.
- Wenn sich das Spiel in der letzten Runde befindet, wird das angezeigt.
- Bei Spielende wird/werden der/die Gewinner mit den jeweiligen Punkteständen angezeigt, sofern niemand geschummelt hat.
- Falls ein Spieler geschummelt hat, wird das angezeigt.
- Text muss bei einer Fenstergröße von ca. 1280x720px gut lesbar sein. Hier ist besonders die Schriftart, die Schriftgröße und der Kontrast zwischen Schriftfarbe und Hintergrund relevant. Ausgenommen sind Texte an die herangezoomt werden kann.

Nicht alle Informationen müssen dauerhaft angezeigt werden, bei manchen kann es sinnvoller sein diese z.B. nur bei bestimmten Events anzuzeigen.

## Spielbrett

Erstellen Sie eine Klasse die ein Spielbrett modelliert.

Die Klasse soll mitunter
- die Logik zum Ausführen und Verifizieren von Zügen,
- die Logik zur Berechnung der Punktestände der Spieler, und
- die Inventare der Spieler
beinhalten.

## Spieler

Erstellen Sie mindestens drei Spieler Klassen:
- Einen `HUMAN` Spieler, der von einem Menschen gesteuert wird.
- Einen `RANDOM_AI` Spieler, der nach dem Zufallsprinzip seinen Spielzug aussucht.
- Einen `CHEATING_AI` Spieler, der sich größtenteils wie eine andere KI verhält, aber in manchen Runden probiert auf unterschiedliche Arten und Weisen zu schummeln.

(Die möglichen Arten von Spieler sind in [`breitband.preset.PlayerType`](https://app.pages.gwdg.de/2024ss/breitband-preset/doc/breitband/preset/PlayerType.html) deklariert.)

Alle Spieler implementieren die Schnittstelle [`breitband.preset.Player`](https://app.pages.gwdg.de/2024ss/breitband-preset/doc/breitband/preset/Player.html).

Alle Funktionen sollen einen entsprechenden Fehler werfen, wenn
- eine Funktion (egal ob sie selber oder eine Andere zuvor) nicht aufgerufen werden durfte,
- fehlerhafte oder unsinnige Argumente übergeben wurden,
- das Spiel sich, vor oder nach dem Ausführen der Funktion, in einem invaliden Zustand (z.B. weil jemand geschummelt hat und dies erkannt wurde) befindet, oder
- es Netzwerkprobleme gab (gilt nur für `REMOTE` Spieler).

Jeder Spieler hat für sich ein privates Spielbrett auf dem die eigenen und die Züge der anderen Spieler ausgeführt werden.\
Die Idee ist, dass kein Spieler einem anderen Spieler vertrauen muss. Alle Spieler überprüfen jeden Zug, den ein anderer Spieler gemacht hat.

Die Funktionen der [Player Schnittstelle](https://app.pages.gwdg.de/2024ss/breitband-preset/doc/breitband/preset/Player.html) sind wie folgt zu verstehen.


### `init(gameConfigurationXML, availableContracts, numplayers, playerid)`

Initialisiert den Spieler.

Diese Funktion darf nur genau einmal für einen Spieler aufgerufen werden und muss vor dem Aufrufen aller anderen Funktionen, außer `getName()`, aufgerufen werden.

Argumente:
- `gameConfigurationXML`: Der Inhalt der Spielkonfigurations XML.
- `availableContracts`: Die Verträge die zum Spielbeginn ausliegen.
- `numplayers`: Liegt im Zahlenbereich von einschließlich _2_ bis einschließlich _5_ und gibt an wie viele Spieler spielen.
- `playerid`: Liegt im Zahlenbereich von einschließlich _1_ bis einschließlich _numplayers_. Die SpielerID gibt die Reihenfolge in der die Spieler ihre jeweiligen Züge machen an.

### `getName()`

Gibt den Namen des Spielers zurück.

Diese Funktion darf jederzeit aufgerufen werden.

### `request()`

Erfragt vom dem Spieler einen Zug zu machen.

Diese Funktion darf nur während der Spieler auch gerade am Zug ist aufgerufen werden und dann auch nur einmal.

### `update(opponentMove)`

Informiert den Spieler über den Zug eines anderen Spielers.

Es ist wichtig, dass hier ein Fehler bei einem ungültigen Zug geworfen wird.

Diese Funktion darf nur während der Spieler gerade nicht am Zug ist aufgerufen werden und dann auch nur einmal pro anderem Mitspieler.

Argumente:
- `opponentMove`: Der Zug des anderen Spielers.

### `getScore(applyEndgameScoring)`

Gibt den aktuellen Punktestand des Spielers zurück.

Argumente:
- `applyEndgameScoring`: Gibt an was für die Punkteberechnung berücksichtigt werden soll.
    - Falls der Wert `false` ist, wird für die Berechnung des Punktestandes nur die von dem Spieler gebauten Kabelstrecken und bereits erfüllten Verträge berücksichtigt.
    - Falls der Wert `true` ist, wird alles für die Berechnung des Punktestandes berücksichtigt.


### `verifyGame(scores)`

Verifiziert das Spielergebnis, nachdem das Spiel vorbei ist.

Falls die Liste der finalen Punktestände nicht mit den Punkteständen der Spieler auf dem lokalen Spielbrett übereinstimmt, wird ein entsprechender Fehler geworfen.

Diese Funktion darf nur nach Spielende aufgerufen werden und auch nur genau einmal.

Argumente:
- `scores`: Eine Liste der finalen Punktestände von allen Spielern. Die Liste ist nach der SpielerID von den Spielern sortiert.

## Spielkonfiguration

Erstellen Sie eine Klasse die Spielkonfiguration modelliert.\
Diese Klasse soll in der Lage sein, eine Spielkonfiguration aus einer Datei (bzw. aus einer [`java.io.Reader`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/Reader.html) Instanz) zu parsen.\
Nutzen Sie dafür die XML-parsing Möglichkeiten die Java bietet, wie z.B. StAX oder DOM.

Es wird erkannt, falls eine Datei nicht dem folgenden Dateiformat entspricht und ein entsprechender Fehler wird geworfen.

### Dateiformat

Spielkonfigurationen sind UTF-8 kodierte wohlgeformte XML 1.0 Dateien.

Eine Spielkonfigurationsdatei verwendet als Wurzelelement ein `BreitbandGameConfiguration`-Element.

Im Folgenden sind die Elemente einer Spielkonfigurationsdatei, deren Attribute, deren Inhalte und deren Einschränkungen dokumentiert.

#### `BreitbandGameConfiguration`-Element

Das Wurzelelement einer Spielkonfiguration.

**Attribute:**
Ein `BreitbandGameConfiguration`-Element muss ein `version`-Attribut haben, welches den Wert ~~`1`~~ `2` haben muss.
(Dieser Wert kann sich ändern, falls diese Definition des Dateiformates verändert wird.)

**Inhalt (Elemente):**
Ein `BreitbandGameConfiguration`-Element muss, in folgender Reihenfolge,
1. genau ein `Country`-Element,
1. genau ein `Description`-Element,
1. genau ein `MapCredits`-Element,
1. genau ein `Regions`-Element,
1. genau ein `ContractPool`-Element, und dann
1. genau ein `CableConfigs`-Element,
beinhalten.

#### `Country`-Element

Informationen über das dargestellte Land.

**Attribute:**
Ein `Country`-Element muss
- ein `name`-Attribut, welches den Namen des Landes beinhaltet, und
- ein `code`-Attribut, welches den zweistelligen Landescode in Großbuchstaben beinhaltet,
haben.

#### `Description`-Element

Die Beschreibung der Spielkonfiguration.

**Inhalt (Text):**
Die Beschreibung.

#### `MapCredits`-Element

(Rechtliche-)Informationen über die Urheber und Lizenzierung der Daten, verwendet zum Erstellen des Inhalts des `Regions`-Elementes.

**Attribute:**
Ein `MapCredits`-Element muss
- ein `license`-Attribut, welches die Bezeichnung der Lizenz unter dem die Daten zur Verfügung stehen beinhaltet,
- ein `licenseshort`-Attribut, welches die Kurzbezeichnung der Lizenz unter dem die Daten zur Verfügung stehen beinhaltet,
- ein `licenselink`-Attribut, welches einen Link zu der Lizenz beinhaltet,
- ein `licenseversion`-Attribut, welches die Version der Lizenz beinhaltet,
- ein `copyright`-Attribut, welches den/die Urheber beinhaltet, und
- ein `copyrightlink`-Attribut, welches einen Link zu Informationen über den/die Urheber beinhaltet,
haben.

#### `Regions`-Element

Die Regionen (z.B. Bundesländer oder Mitgliedstaaten).

**Inhalt (Elemente):**
Ein `Regions`-Element beinhaltet `Region`-Elemente, welche die Regionen definieren.

**Einschränkungen:**
- Es muss mindestens eine Region definiert sein.

#### `Region`-Element

Eine Region (z.B. Bundesland oder Mitgliedsstaat).

**Attribute:**
Ein `Region`-Element muss ein `name`-Attribut haben, welches den Namen der Region beinhaltet.

**Inhalt (Elemente):**
Ein `Region`-Element beinhaltet
- `Border`-Elemente, welche die Flächen der Region definieren, und
- `City`-Elemente, welche die Städte in der Region definieren.

**Einschränkungen:**
- Jede Region muss einen einzigartigen Namen haben.
- Für jede Region muss mindestens eine Fläche definiert sein.

#### `Border`-Element

Eine Fläche einer Region beschrieben als Vieleck durch eine Menge an Punkten.

Das Vieleck kann z.B. via [GPolygon](https://app.pages.gwdg.de/sag/doc/sag/elements/shapes/GPolygon.html) grafisch dargestellt werden.

**Attribute:**
Ein `Border`-Element muss ein `points`-Attribut haben, welches eine leerzeichengetrennte Liste an kommaseparierten *x* und *y* Koordinaten der aufeinanderfolgenden Grenzpunkte des Vieleckes beinhaltet.

**Einschränkungen:**
- Es müssen mindestens drei unterschiedliche Punkte definiert sein.
- Die *x* und *y* Werte der Koordinaten müssen ganze Zahlen sein.

#### `City`-Element

Eine Stadt.

**Attribute:**
Ein `City`-Element muss
- ein `name`-Attribut, welches den Namen der Stadt beinhaltet,
- ein `x`-Attribut, welches den *x*-Wert der Koordinate der Stadt beinhaltet,
- ein `y`-Attribut, welches den *y*-Wert der Koordinate der Stadt beinhaltet, und
- ein `maxconnections`-Attribut, welches die maximale Anzahl an möglichen Streckenverbindungen an die Stadt beinhaltet,
haben.

**Einschränkungen:**
- Jede Stadt muss einen einzigartigen Namen haben.
- Die *x* und *y* Werte der Koordinate müssen eine ganze Zahl sein.
- Das `maxconnections`-Attribut muss eine ganze Zahl größer *0* sein.
- Eine Stadt darf von der Position her nicht auf einer Grenze einer (beliebigen) Region liegen.
- Jede Stadt muss eine einzigartige Position haben.

#### `ContractPool`-Element

Die Menge an Verträgen, von denen eine Teilmenge zum Spielbeginn zufällig ausgewählt werden, welche dann im Spiel zur Verfügung stehen.

**Attribute:**
Ein `ContractPool`-Element muss ein `subsetsize`-Attribut haben, welches die Größe der Teilmenge der im Spiel zur Verfügung stehenden Verträge beschreibt.

**Inhalt (Elemente):**
Ein `ContractPool`-Element beinhaltet
- `CityToCityContract`-Elemente, und/oder
- `RegionContract`-Elemente.

**Einschränkungen:**
- Das `subsetsize`-Attribut muss eine ganze Zahl größer *5* und kleiner oder gleich der Anzahl an definierten Verträgen sein, **außerdem darf der Wert nicht größer *60* sein**.
- Es müssen mindestens *6* Verträge definiert sein.

#### `CityToCityContract`-Element

Einen Vertrag, dass zwei Städte miteinander verbunden werden sollen.

**Attribute:**
Ein `CityToCityContract`-Element muss
- ein `id`-Attribut, welches eine einzigartige Bezeichnung für den Vertrag beinhaltet,
- ein `a`-Attribut, welches einen der Namen der beiden Städte beinhaltet,
- ein `b`-Attribut, welches den Namen der anderen Stadt beinhaltet, und
- ein `value`-Attribut, welches die Anzahl der Punkte die ein Spieler für das Erfüllen des Vertrages erhält beinhaltet,
haben.

**Einschränkungen:**
- Die Städtenamen müssen verschieden sein.
- Die referenzierten Städte müssen definiert sein.
- Es darf für die jeweils zwei referenzierten Städte nur maximal ein `CityToCityContract`-Element geben.
- Das `value`-Attribut muss eine ganze Zahl größer *0* sein.
- Das `id`-Attribut muss ein String mit mindestens einem Zeichen sein.


#### `RegionContract`-Element

Einen Vertrag, dass alle Städte in einer Region verbunden werden sollen.

**Attribute:**
Ein `RegionContract`-Element muss
- ein `id`-Attribut, welches eine einzigartige Bezeichnung für den Vertrag beinhaltet,
- ein `region`-Attribut, welches den Namen der referenzierten Region beinhaltet, und
- ein `value`-Attribut, welches die Anzahl der Punkte die ein Spieler für das Erfüllen des Vertrages erhält beinhaltet,
haben.

**Einschränkungen:**
- Die referenzierte Region muss definiert sein.
- Die referenzierte Region muss mindestens drei Städte beinhalten.
- Es darf für eine referenzierten Region nur maximal ein `RegionContract`-Element geben.
- Das `value`-Attribut muss eine ganze Zahl größer *0* sein.
- Das `id`-Attribut muss ein String mit mindestens einem Zeichen sein.

#### `CableConfigs`-Element

Die Definition und Konfiguration der Kabelarten, welche zum Bauen von Strecken zwischen Städte verwendet werden können.

**Attribute:**
Ein `CableConfigs`-Element muss ein `endinglength`-Attribut haben, welches die maximale Summe an Streckenlänge, die ein Spieler insgesamt gebaut haben darf, bevor das Spielende getriggered wird beinhaltet.

**Inhalt (Elemente):**
Ein `CableConfigs`-Element muss `CableConfig`-Elemente beinhalten.

**Einschränkungen:**
- Ein `CableConfigs`-Element muss mindestens ein `CableConfig`-Element beinhalten.

#### `CableConfig`-Element

Die Definition und Konfiguration einer Kabelart, welche zum Bauen von Strecken zwischen Städten verwendet werden kann.

**Attribute:**
Ein `CableConfig`-Element muss
- ein `type`-Attribut, welches die Art des Kabels beinhaltet,
- ein `value`-Attribut, welches den Wert eines Kabels dieser Art beinhaltet,
- ein `cancrossborders`-Attribut, welches beschreibt, ob ein Kabel dieser Art über Regionsgrenzen gelegt werden darf, beinhaltet,
- ein `cancrosscables`-Attribut, welches eine kommaseparierte Liste an Kabelarten beinhaltet welche diese Kabelart überkreuzen darf beinhaltet,
- ein `fontawesome`-Attribut, welches den Namen eines Font Awesome Icons (darstellbar mit [GFontAwesome](https://app.pages.gwdg.de/sag/doc/sag/elements/GFontAwesome.html)), dass zur Representation dieser Kabelart verwendet werden kann, beinhaltet,
- ein `emoji`-Attribut, welches die Bezeichnung eines OpenMoji Emojis (darstellbar mit [GEmoji](https://app.pages.gwdg.de/sag/doc/sag/elements/GEmoji.html)), dass zur Representation dieser Kabelart verwendet werden kann, beinhaltet,
- ein `distancepercable`-Attribut, welches die Streckendistanz, die mit einem Kabel dieser Art erreicht werden kann, beinhaltet, und
- ein `maxdistance`-Attribut, welches die maximale Streckendistanz, die mit mehreren aneinandergehängten Kabeln dieser Art erstellt werden kann, beinhaltet,
haben

**Einschränkungen:**
- Das `type`-Attribut muss ein String mit mindestens einem Zeichen sein.
- Jede Kabelart muss ein einzigartiges `type`-Attribut haben.
- Das `value`-Attribut muss eine ganze Zahl größer *0* sein.
- Valide Werte für das `cancrossborders`-Attribut sind `true` und `false`.
- Die im `cancrosscables`-Attribut referenzierten Kabelarten müssen definiert sein.
- Die im `cancrosscables`-Attribut referenzierten Kabelarten müssen auch in derem `cancrosscables`-Attribut diese Kabelart zurückreferenzieren.
- Das `distancepercable`-Attribut muss eine ganze Zahl größer *0* sein.
- Das `maxdistance`-Attribut muss eine ganze Zahl sein und größer als der Wert des `distancepercable`-Attributes sein.

## Hauptprogramm

Erstellen Sie eine ausführbare Klasse mit folgender Funktionalität.

- Beim Starten des Programms sollen die Einstellungen über die Kommandozeile festgelegt werden können.\
  Verwenden Sie zum Einlesen der Kommandozeilenparameter eine Instanz der Klasse [`breitband.preset.ArgumentParser`](https://app.pages.gwdg.de/2024ss/breitband-preset/doc/breitband/preset/ArgumentParser.html), welche die Klasse [`breitband.preset.Settings`](https://app.pages.gwdg.de/2024ss/breitband-preset/doc/breitband/preset/Settings.html) erweitert.\
  In der [Dokumentation der Settings Klasse](https://app.pages.gwdg.de/2024ss/breitband-preset/doc/breitband/preset/Settings.html) sind nähere Informationen zum Umgang mit den einzelnen Einstellungen zu finden.\
  **Lesen** Sie bitte diese Dokumentation **bevor** Sie Anfangen Code zu schreiben.
- Die Spielkonfiguration wird ggf. geladen.
- Es wird (falls benötigt) eine grafische Ein- und Ausgabeklasse erzeugt.
- Abhängig von der `--playerTypes` Flagge werden entsprechend viele Spieler erzeugt.\
  Nach dem Erzeugen werden die Spieler ausschließlich über Referenzen der [`breitband.preset.Player`](https://app.pages.gwdg.de/2024ss/breitband-preset/doc/breitband/preset/Player.html) Schnittstelle angesprochen. Einzige Ausnahmen sind
    - eine Instanz von [`breitband.preset.PlayerGUIAccess`](https://app.pages.gwdg.de/2024ss/breitband-preset/doc/breitband/preset/PlayerGUIAccess.html), welche dem `HUMAN` Spieler über seinen Konstruktor übergeben wird, sowie
    - notwendige Netzwerkfunktionalitäten für einen `REMOTE` Spieler.
- Die Teilmenge der Verträge, die während des Spiels ausliegend werden, wird aus der Menge der möglichen Verträge erzeugt.
- Die Spieler werden initialisiert.
- Von den Spielerreferenzen werden abwechselnd Züge erfragt und den Anderen mitgeteilt.
- Die aktuelle Spielsituation wird über die grafischen Ein- und Ausgabe angezeigt.
- Sorgen Sie dafür, dass man das Spiel Computer gegen Computer gut verfolgen kann, indem nach jedem vom Computer ausgeführten Zug das Programm kurz pausiert wird.
- Es wird erkannt, sobald das Spielende eingetreten ist.
- Sobald das Spielende erkannt wurde, beginnt die Spielverifikation und der/die Gewinner wird/werden angezeigt.

## README

Ihr Repository muss eine README Datei (z.B. `README.md`) beinhalten, welche mindestens
- ihren Projektnamen,
- die notwendigen Schritte zum Übersetzen ihres Programms,
- eine Erklärung zur Verwendung ihrer grafischen Ein- und Ausgabe (ggf. mit Screenshots),
- ein Beispiel für einen **ausführbaren** Befehl mit dem ihr Spiel gestartet werden kann (ähnlich der Form `java -jar <jar-file> -c <config-file> -d 100 -g -pt RANDOM_AI RANDOM_AI -pc RED BLUE -pn Rot Blau`), und
- die Ausgabe ihres Programms, wenn es mit der `--help` Flagge aufgerufen wurde beinhaltet.

Es ist **stark empfohlen** mit dem Schreiben der README Datei **möglichst früh** anzufangen und diese während der Bearbeitungszeit kontinuierlich anzupassen.\
Eine gute README Datei *hilft allen* Gruppenmitgliedern und Ihrem Tutor sich in Ihrem Projekt zurechtzufinden!

## Optional

Bauen Sie das Spiel weiter aus.\
Für größere Projektgruppen ist mindestens eine weitere Aufgabe erforderlich. Dies muss direkt bei Projektanmeldung per E-Mail abgeklärt werden.

Bitte Übergeben Sie an den [ArgumentParser](https://app.pages.gwdg.de/2024ss/breitband-preset/doc/breitband/preset/ArgumentParser.html), welche optionalen Features Sie implementiert haben.

Hier sind ein paar Vorschläge:
- Schreiben Sie einen einfachen regelbasierten Computerspieler. (`SIMPLE_AI`)
- Implementieren Sie einen Tournament-Modus, bei dem mehrere (Computer-)Spieler mehrere (viele) Spiele hintereinander Spielen können. Erstellen Sie eine Statistik der Spielergebnisse. (`TOURNAMENTS`)
- Entwickeln Sie einen weiteren, intelligenteren Computerspieler. (`ADVANCED_AI`)
- Implementieren Sie Netzwerkspiel. Nutzen Sie hierfür die Klassen aus dem Package [`breitband.preset.networking`](https://app.pages.gwdg.de/2024ss/breitband-preset/doc/breitband/preset/networking/package-summary.html). Beim Netzwerkspiel muss sich das Hauptprogramm entsprechend anders verhalten. (`NETWORKING`)
- Laden und Speichern von Spielständen. (`SAVEGAMES`)
- Animieren Sie Elemente und Interaktionen mit der grafischen Oberfläche. (`ANIMATIONS`)
- Verschönern Sie die Grafik. (`BETTERGRAPHICS`)
- Entwickeln Sie einen grafischen Spielkonfigurationseditor bzw. Mapeditor. (`EDITOR`)
- Fügen Sie Soundeffekte zu Interaktionen hinzu. (Achten Sie auf die Nutzungsbedinungen der Soundeffekte, falls Sie welche aus dem Internet herunterladen!!!) (`SOUNDEFFECTS`)
- Erstellen Sie Achievements, welche von den Spielern erreicht werden können. (`ACHIEVEMENTS`)
- ...



## Abhängigkeiten

Das Projekt **muss** auf den CIP-Pool Rechnern des Institutes für Informatik vollständig übersetzbar und ausführbar sein.

Hier sind einige Informationen über die installierte Software auf den Rechnern (stand 09.06.2024):

```bash
$ java -version
openjdk version "11.0.23" 2024-04-16
OpenJDK Runtime Environment (build 11.0.23+9-post-Ubuntu-1ubuntu122.04.1)
OpenJDK 64-Bit Server VM (build 11.0.23+9-post-Ubuntu-1ubuntu122.04.1, mixed mode, sharing)

$ javadoc --version
javadoc 11.0.23

$ ant -version
Apache Ant(TM) version 1.10.12 compiled on January 17 1970

$ lsb_release -a
No LSB modules are available.
Distributor ID:	Ubuntu
Description:	Ubuntu 22.04.4 LTS
Release:	22.04
Codename:	jammy
```



