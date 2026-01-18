# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run (Optimized for 512MB RAM)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# CRITICAL: Limit Heap memory to 350MB so it fits in Render's 512MB container
# If we don't do this, Render will kill the app for using too much memory.
ENTRYPOINT ["java", "-Xms256m", "-Xmx350m", "-jar", "app.jar"]