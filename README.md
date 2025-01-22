#  The Cable Quest - Readme


# **Clone from https://gitlab.gwdg.de/marcarne.schlegel/0-the-cable-quest**

---

## How-to Compile

Run build.xml: \
`ant javadoc` to create documentation \
`ant build` to create a jar file in the ./dist-folder (Also compiles)

---
## How-to Run
The jar file should be in the `dist` folder  
`java -jar <jar-file> -c <cofig-file> -d <wait-time-in-ms> -g -pt HUMAN RANDOM_AI CHEATING_AI -pc RED BLUE ORANGE -pn Rot Blau Orange`   
Currently 'ant run': `java -jar ./dist/TheCableQuest.jar -c ./gameconfigs/DE.xml -d 100 -g -pt RANDOM_AI RANDOM_AI RANDOM_AI RANDOM_AI  -pc RED BLUE ORANGE GREEN  -pn Rot Blau Orange Gruen`

---
## How-to play the game

 1. Start Game
 2. Let game run

---

## Contributing
| Name | Area of ​​responsibility  |
| ----------- | ----------- |
| Marc-Arne Schlegel  | Project management & Basic Graphic | 
| Batoul Mhawesh   &ensp;&thinsp;&ensp;&thinsp;| Player implementation |
| Marius Degenhardt&ensp;&thinsp; | Game board & Game board configuration |


---
## --help

```
usage: java -jar ./dist/TheCableQuest.jar [-c <FILE>] [-d <DELAY>] [-g]
       [-h] [-ll <LEVEL>] [-pc <COLOR ...>] [-pn <NAME ...>] [-pt <TYPE
       ...>]

=========================================================================
                       The Cable Quest <Alpha 1.0>
  Authors: Marius Degenhardt, Batoul Mhawesh, Marc-Arne Schlegel
=========================================================================

Options:
 -c,--config <FILE>               The file from which the game
                                  configuration should be read.
 -d,--delay <DELAY>               Delay in milliseconds after an ai has
                                  made their move.
 -g,--gui                         Show the GUI, even if no HUMAN player
                                  exists.
 -h,--help                        Print this help message and some extra
                                  information about this program.
 -ll,--loglevel <LEVEL>           The maximum log level. [ERRORS,
                                  WARNINGS, INFO, DEBUG]
 -pc,--playerColors <COLOR ...>   The color(s) of the player(s). [RED,
                                  BLUE, \#RRGGBB, ...]
 -pn,--playerNames <NAME ...>     The name(s) of the player(s).
 -pt,--playerTypes <TYPE ...>     The type(s) of the player(s). [HUMAN,
                                  RANDOM_AI, CHEATING_AI]

Preset: v1.0.2 (Tue Jul 09 10:36:05 CEST 2024)
SAG: v2.4.0 (Sun Jul 07 20:33:58 CEST 2024)
  This project uses the Simple APP Graphics (SAG) library.
    Website: https://gitlab.gwdg.de/app/sag
    License: BSD 3-Clause
  SAG includes and uses the SVG Salamander library.
    Website: http://blackears.github.io/svgSalamander
    License: BSD 3-Clause
    Copyright: Mark McKay (2010)
  SAG includes and uses OpenMoji - the open-source emoji and icon project.
    Website: https://openmoji.org
    License: CC BY-SA 4.0
  SAG includes and uses Font Awesome free icons.
    Website: https://fontawesome.com
    License: CC BY-SA 4.0

```
