# ---- Stage 1: Build ----
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app

# Copy source code
COPY . .

# Build the fat JAR
RUN ./gradlew spotlessApply
RUN ./gradlew build --no-daemon

# ---- Stage 2: Run ----
FROM eclipse-temurin:17.0.10_7-jre
WORKDIR /app

# Copy built JAR from previous stage
COPY --from=builder /app/build/libs/*-all.jar app.jar


# Expose the app port (Micronaut default is 8080)
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
