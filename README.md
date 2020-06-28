CURRENT FEATURES
get hs cards from web json
generate cards tags and mechanics from googlesheets
load deck from deck string
deck archtype inffer
cards that synergies? with the choosen card

get pre-calculated synergy graph between tags from googlesheets

generate played cards graph from matchs file from Hearthsim
load hs combos from file
-inffer future plays by opponent

get decks from h
generate deck tags and mechanics?
-get set, enums, from web
? calculate decks similarity


# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.2.3.BUILD-SNAPSHOT/gradle-plugin/reference/html/)

### Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

Database nodes e relationships can be queried and viewed using Neo4j browser
https://neo4j.com/developer/neo4j-desktop/

1) Run desktop and create a neo4j project
2) Add a new local db at neo4j browser
3) Manage -> settings -> activate/change some props:
	dbms.active_database=hs
	dbms.directories.data=C:/.../Hearthstone-Tools
OBS: neo4j browser looks directly at subfolder /databases/...
4) Start database, skip security (talvez tenha que deletar dbms/auth.ini)??