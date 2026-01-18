# Stage 1: Build (Using Java 21)
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run (Using Java 21 JRE, Optimized for Render Free Tier)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Limit memory to avoid crashing on Render Free Tier (512MB limit)
ENTRYPOINT ["java", "-Xms256m", "-Xmx350m", "-jar", "app.jar"]