FROM maven:3-eclipse-temurin-11 AS build
COPY src /src
COPY pom.xml pom.xml
RUN mvn -Pdev clean install

FROM eclipse-temurin:11
ARG JAR_FILE=target/*.jar
COPY --from=build ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","./app.jar"]
