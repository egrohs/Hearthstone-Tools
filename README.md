# CURRENT FEATURES

### Card features

* get hs cards from web json

* get card ranks from [hearthstonetopdecks] (https://www.hearthstonetopdecks.com/)

generate cards tags and mechanics from googlesheets

cards that synergies? with the choosen card

get pre-calculated synergy graph between tags from googlesheets

generate played cards graph from matchs file from Hearthsim

load hs combos from file

-get expansions from wikipediaExpansions?, enums, from web

### Deck features

load deck from deck string

deck archtype inffer

-inffer future plays by opponent

get decks from hearthstonetopdecksDecks

get meta decks from tempostormMeta

generate deck tags and mechanics?

? calculate decks similarity

# Getting Started

### Graph visualization

Database nodes and relationships can be queried and viewed using
[Neo4j browser] (https://neo4j.com/developer/neo4j-desktop/)

1) Run desktop and create a neo4j project
2) Add a new local db at neo4j browser
3) Manage -> settings -> activate/change some props:
	dbms.active_database=hs
	dbms.directories.data=C:/.../Hearthstone-Tools
OBS: neo4j browser looks directly at subfolder /databases/...
4) Start database, skip security (maybe delete dbms/auth.ini)??

# TODOs

use log level at htools, enabled log debug.

create cards test for mechanics verification from cards scripts?

calibrate deck archtype inffer ann and add more archtypes

# FUTURE

PEGs, semantic card text parser