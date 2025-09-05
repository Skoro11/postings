# ---- Stage 1: Build the app ----
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy only pom.xml first for caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Package the app (skip tests for faster build)
RUN mvn clean package -DskipTests

# ---- Stage 2: Runtime ----
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# Copy the built jar
COPY --from=build /app/target/*.jar app.jar

# Expose port (Spring Boot will read from ENV variable if set)
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
