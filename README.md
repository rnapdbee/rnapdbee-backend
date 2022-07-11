# RNApdbee engine
[![CI](https://github.com/rnapdbee/rnapdbee-backend/actions/workflows/ci.yml/badge.svg)](https://github.com/rnapdbee/rnapdbee-backend/actions/workflows/ci.yml)

Project generated with [Spring](https://start.spring.io/) version 2.6.6.

## Requirements
- Maven
- Java 11 (SDK 11)
- Docker

## Build
The server starts on the localhost default 8080 port -> http://localhost:8080/

### With docker
To run project in command line type:
```
mvn clean install -DskipTests
docker compose up
```

### IntelliJ IDEA
To run only postgres database type:
```
docker run --name rnapdbee-backend-db -p 5432:5432 -e POSTGRES_USER=rnapdbee -e POSTGRES_PASSWORD=a1s2d3f4 -d postgres
```
Then you can work with intelliJ environment and dockerized database.
I recommend using build in IntelliJ professional - Database tool which you can configure following this [TUTORIAL](https://www.jetbrains.com/help/idea/postgresql.html).