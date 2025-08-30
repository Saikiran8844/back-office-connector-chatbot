# -------- Build Stage --------
    FROM maven:3.9.6-eclipse-temurin-17 AS build

    # Set working directory
    WORKDIR /app
    
    # Copy the pom.xml and download dependencies
    COPY pom.xml .
    RUN mvn dependency:go-offline
    
    # Copy the source code
    COPY src ./src
    
    # Package the application
    RUN mvn clean package -DskipTests
    
    # -------- Run Stage --------
    FROM openjdk:17-jdk-slim
    
    WORKDIR /app
    
    # Copy jar from the build stage
    COPY --from=build /app/target/*.jar chatbotservices.jar
    
    # Expose the application port
    EXPOSE 8080
    
    # Run the jar
    ENTRYPOINT ["java", "-jar", "chatbotservices.jar"]
    