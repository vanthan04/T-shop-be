FROM maven:3.8.6-eclipse-temurin-17-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -B dependency:resolve dependency:resolve-plugins

COPY src ./src
RUN mvn -B -DskipTests clean package

#Run stage
FROM eclipse-temurin:17-jre-alpine AS run
COPY --from=build /app/target/*.jar /run/inventory.jar
EXPOSE 8003
ENTRYPOINT ["java", "-Duser.timezone=Asia/Ho_Chi_Minh", "-jar", "/run/inventory.jar"]
