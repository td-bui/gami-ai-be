FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first for caching dependencies
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (optional, improves build cache)
RUN ./mvnw dependency:go-offline

# Copy the rest of the source code
COPY src src

# Build the application
RUN ./mvnw package -DskipTests

# Copy the built jar to the container
COPY target/gami-ai-be-0.0.1-SNAPSHOT.jar app.jar

# Expose the port (default for Spring Boot is 8080)
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]

# Spring datasource configuration
