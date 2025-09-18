# Use Java 21 runtime
FROM eclipse-temurin:21-jre-jammy

# Set working directory inside the container
WORKDIR /app

# Copy your JAR into the container
COPY backend/target/demo-0.0.1-SNAPSHOT.jar app.jar

# Run the JAR
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
