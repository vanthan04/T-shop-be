#build stage
FROM maven:3.8.6-eclipse-temurin-17-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -B dependency:resolve dependency:resolve-plugins

COPY src ./src
RUN mvn -B -DskipTests clean package

#Run stage
FROM openjdk:17-oracle AS run
COPY --from=build /app/target/*.jar /run/app.jar
EXPOSE 8004
ENTRYPOINT ["java", "-jar", "/run/app.jar"]