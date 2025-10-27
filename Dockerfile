FROM maven:3.9.11-eclipse-temurin-25-alpine AS build

WORKDIR /app

COPY pom.xml /app/pom.xml

RUN mvn dependency:resolve -DskipTests -B

COPY src /app/src

RUN mvn -Pdev clean install

#######################################

FROM eclipse-temurin:25-alpine

COPY --from=build /app/target/*.jar /app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+ExitOnOutOfMemoryError", "-jar", "/app.jar"]
