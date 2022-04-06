# RNApdbee engine
Project generated with [Spring](https://start.spring.io/) version 2.6.6.

## Requirements
- Maven
- Java 11 (SDK 11)
- Docker

## Build
The server starts on the localhost default 8080 port -> http://localhost:8080/

To run project in command line type:
```
mvn clean install
docker build . -t rnapdbee-backend
docker run -i -t --rm -p 8080:8080 --name rnapdbee-backend rnapdbee-backend
```