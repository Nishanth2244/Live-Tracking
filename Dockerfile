# Stage 1: Build the application (Java 21)
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy Maven project files
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Download dependencies (cached unless pom.xml changes)
RUN mvn dependency:go-offline -B

# Copy source and build JAR
COPY src src
RUN mvn clean package -DskipTests

# Stage 2: Run the application (Java 21 runtime)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
