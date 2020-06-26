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