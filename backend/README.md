# Personal Assistant Backend (chatbotservices)

This is the backend service for a Personal Assistant/Chatbot application. It provides REST APIs for user management, conversation handling, and subscription services.

## Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
  - [Configuration](#configuration)
  - [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Monitoring](#monitoring)

## Features

- **User Authentication & Management**: Secure user registration and login.
- **Conversation Management**: APIs to create, retrieve, and manage user conversations with the assistant.
- **Subscription Plans**: Manages different subscription tiers for users.
- **Payment Integration**: Integrated with Razorpay for handling subscription payments.
- **Email Notifications**: Uses Sendinblue for sending emails (e.g., registration confirmation).
- **API Documentation**: Auto-generated API documentation using Springdoc (Swagger UI).

## Technologies Used

- **Framework**: [Spring Boot](https://spring.io/projects/spring-boot) 3.1.0
- **Language**: [Java](https://www.java.com/) 17
- **Database**: [MySQL](https://www.mysql.com/) with [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- **Security**: [Spring Security](https://spring.io/projects/spring-security)
- **API Documentation**: [Springdoc OpenAPI](https://springdoc.org/)
- **Payments**: [Razorpay Java SDK](https://github.com/razorpay/razorpay-java)
- **Email**: [Sendinblue (Brevo) API SDK](https://github.com/sendinblue/sib-api-v3-sdk)
- **Monitoring**: [New Relic Java Agent](https://newrelic.com/platform/application-monitoring)
- **Build Tool**: [Maven](https://maven.apache.org/)

## Prerequisites

Before you begin, ensure you have the following installed:
- [JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or later
- [Apache Maven](https://maven.apache.org/download.cgi)
- [MySQL Server](https://dev.mysql.com/downloads/mysql/)

## Getting Started

Follow these instructions to get the project up and running on your local machine.

### 1. Clone the repository

```bash
git clone <your-repository-url>
cd backend
```

### 2. Configuration

The main configuration is in `src/main/resources/application.properties`. You need to provide your own credentials for the database, email service, and payment gateway.

Update `src/main/resources/application.properties` with your details.

**`src/main/resources/application.properties`:**
```properties
# Spring Datasource
spring.datasource.url=jdbc:mysql://localhost:3306/chatbotservices_db?createDatabaseIfNotExist=true
spring.datasource.username=<your_mysql_username>
spring.datasource.password=<your_mysql_password>
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Server Port
server.port=8080

# Email (Sendinblue/Brevo)
sendinblue.api.key=<your_sendinblue_api_key>

# Razorpay
razorpay.key.id=<your_razorpay_key_id>
razorpay.key.secret=<your_razorpay_key_secret>
```

**Note**: It is highly recommended to use environment variables or a secrets management tool for sensitive data instead of hardcoding them in the properties file.

### 3. Running the Application

You can build and run the application using Maven:

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

Alternatively, you can run the application from your IDE by running the `main` method in `ChatbotservicesApplication.java`.

The application will start on `http://localhost:8080`.

## API Documentation

Once the application is running, you can access the Swagger UI for interactive API documentation at:

http://localhost:8080/swagger-ui.html

This interface provides detailed information about all available endpoints, their parameters, and allows you to test them directly from your browser.

## Monitoring

This project is configured to use the **New Relic Java Agent** for application performance monitoring (APM). The configuration is located in `src/main/resources/newrelic.yml`.

To enable monitoring:
1.  Sign up for a New Relic account.
2.  Replace the placeholder `license_key` in `newrelic.yml` with your actual New Relic license key.
    ```yaml
    # src/main/resources/newrelic.yml
    common: &default_settings
      license_key: 'YOUR_NEW_RELIC_LICENSE_KEY'
      app_name: 'chatbotservices'
    ```
3.  When running the application, attach the New Relic agent using the `-javaagent` flag. You will need to download the `newrelic.jar` file from your New Relic account.
    ```bash
    java -javaagent:/path/to/newrelic.jar -jar target/chatbotservices-0.0.1-SNAPSHOT.jar
    ```
**Note**: Monitoring is still in development.
**Security Warning**: Do not commit your actual license keys or other secrets to the repository.