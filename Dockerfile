# -------- Build Stage --------
    FROM maven:3.9.6-eclipse-temurin-17 AS build

    WORKDIR /app
    
    COPY pom.xml .
    RUN mvn dependency:go-offline

    COPY src ./src

    # Package the application
    RUN mvn clean package -DskipTests

    RUN mvn dependency:copy -Dartifact=com.newrelic.agent.java:newrelic-agent:8.18.0:jar -DoutputDirectory=./target/newrelic

    # -------- Run Stage --------
    FROM openjdk:17-jdk-slim

    WORKDIR /app

    # Create a directory for the New Relic agent
    RUN mkdir -p /app/newrelic

    # Copy New Relic agent from the build stage
    COPY --from=build /app/target/newrelic/newrelic-agent-*.jar /app/newrelic/newrelic.jar

    # Copy the newrelic.yml configuration file.
    # This allows overriding it with a volume mount.
    COPY --from=build /app/src/main/resources/newrelic.yml /app/newrelic/newrelic.yml

    COPY --from=build /app/target/*.jar chatbotservices.jar

    # Expose the application port
    EXPOSE 8080

    ARG NEW_RELIC_LICENSE_KEY
    ENV NEW_RELIC_LICENSE_KEY=${NEW_RELIC_LICENSE_KEY}

    ENTRYPOINT ["java", "-javaagent:/app/newrelic/newrelic.jar", "-jar", "chatbotservices.jar"]