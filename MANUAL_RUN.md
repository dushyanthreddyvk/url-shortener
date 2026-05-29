# URL Shortener - Manual Run Instructions

## Overview
This is a URL Shortener backend application built with Spring Boot and MySQL. The application provides REST APIs to shorten long URLs and track analytics.

## Prerequisites

- **Java 17+** installed
- **Maven 3.6+** installed
- **MySQL 8.0+** (optional - H2 in-memory database available for testing)

## Quick Start

### Step 1: Navigate to Project Directory
```bash
cd "/Users/vkdushyanthreddy/Documents/PERSONAL/resume projects/URL Shortener"
```

### Step 2: Build the Project
```bash
mvn clean install
```

### Step 3: Run the Application

#### Option A: Run with H2 In-Memory Database (RECOMMENDED for Testing)
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"
```

#### Option B: Run with MySQL Database
```bash
mvn spring-boot:run
```

#### Option C: Build JAR and Run It
```bash
# Build the JAR file
mvn clean package

# Run the JAR with H2 profile
java -jar target/url-shortener-0.0.1-SNAPSHOT.jar --spring.profiles.active=h2

# Or run with default MySQL configuration
java -jar target/url-shortener-0.0.1-SNAPSHOT.jar
```

## Detailed Commands

### Clean Build
```bash
mvn clean compile
```

### Skip Tests During Build
```bash
mvn clean install -DskipTests
```

### Run with Specific Port
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=9090 --spring.profiles.active=h2"
```

### Run Tests
```bash
mvn test
```

### Stop the Application
```bash
# Press Ctrl+C in the terminal where the application is running
```

## Database Configuration

### H2 In-Memory Database (No Setup Required)
- **URL:** `jdbc:h2:mem:testdb`
- **Username:** `sa`
- **Password:** (empty)
- **Console:** http://localhost:8080/h2-console
- **Profile:** `h2`
- **Data:** Recreated on each restart

### MySQL Database (Persistent)
- **Host:** localhost
- **Port:** 3306
- **Database:** `url_shortener_db`
- **Username:** `root`
- **Password:** (empty by default)
- **Profile:** default (no profile needed)

#### Setup MySQL Database
```bash
# Create database and user (if needed)
mysql -u root -e "CREATE DATABASE IF NOT EXISTS url_shortener_db;"
```

## Access the Application

### Web Interface
Open your browser and navigate to:
```
http://localhost:8080
```

### H2 Database Console
Available only with H2 profile:
```
http://localhost:8080/h2-console
```
- JDBC URL: `jdbc:h2:mem:testdb`
- User Name: `sa`
- Password: (leave empty)

### REST API Endpoints
Check the Postman collection for all available endpoints:
```
postman/URL-Shortener.postman_collection.json
```

## Common Issues & Solutions

### Issue: Port 8080 Already in Use
```bash
# Run on a different port
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=9090 --spring.profiles.active=h2"
```

### Issue: MySQL Connection Refused
```bash
# Use H2 profile instead (no database setup required)
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"
```

### Issue: Java Version Mismatch
```bash
# Check Java version
java -version

# Project requires Java 17 or higher
```

### Issue: Maven Not Found
```bash
# Make sure Maven is in your PATH or use the Maven wrapper if available
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"
```

## Application Properties

### Configuration Files
- **Base Config:** `src/main/resources/application.properties`
- **H2 Config:** `src/main/resources/application-h2.properties`
- **Docker Config:** `src/main/resources/application-docker.properties`

### Key Properties
```properties
server.port=8080
spring.jpa.hibernate.ddl-auto=update
app.base-url=http://localhost:8080
```

## Docker (Optional)

To run with Docker Compose (requires Docker and Docker Compose):
```bash
docker-compose up --build
```

## Project Structure
```
URL Shortener/
├── src/
│   ├── main/
│   │   ├── java/com/urlshortener/
│   │   │   ├── UrlShortenerApplication.java
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── model/
│   │   │   └── repository/
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-h2.properties
│   │       └── static/
│   └── test/
├── pom.xml
├── docker-compose.yml
└── postman/
    └── URL-Shortener.postman_collection.json
```

## Maven Useful Commands

```bash
# List dependencies
mvn dependency:tree

# Check for dependency updates
mvn versions:display-dependency-updates

# Format code
mvn spotless:apply

# Build without running tests
mvn clean install -DskipTests

# Run specific test class
mvn test -Dtest=UrlServiceImplTest

# Clean all build artifacts
mvn clean
```

## Troubleshooting

### View Application Logs
Logs are displayed in the terminal with the format:
```
2026-05-29 13:11:51 INFO  c.u.UrlShortenerApplication - Starting UrlShortenerApplication
```

### Enable Debug Mode
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--debug --spring.profiles.active=h2"
```

### Check Application Health
```bash
curl http://localhost:8080/actuator/health
```

## API Testing

### Using cURL
```bash
# Shorten a URL
curl -X POST http://localhost:8080/api/shorten \
  -H "Content-Type: application/json" \
  -d '{"longUrl":"https://www.example.com/very/long/url"}'

# Redirect to original URL
curl -L http://localhost:8080/abc123

# Get analytics
curl http://localhost:8080/api/analytics/abc123
```

### Using Postman
1. Import the collection from `postman/URL-Shortener.postman_collection.json`
2. Use the provided requests to test all endpoints

## Performance & Production

### For Production Deployment
```bash
# Build optimized JAR
mvn clean package -DskipTests -Pproduction

# Run with production settings
java -jar target/url-shortener-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --server.port=8080 \
  --spring.datasource.url=jdbc:mysql://db-server:3306/url_shortener_db
```

### JVM Tuning
```bash
java -Xmx512m -Xms256m -jar target/url-shortener-0.0.1-SNAPSHOT.jar
```

## Support & Documentation

- **Spring Boot Docs:** https://spring.io/projects/spring-boot
- **Spring Data JPA:** https://spring.io/projects/spring-data-jpa
- **MySQL JDBC Driver:** https://dev.mysql.com/doc/connector-j/
- **H2 Database:** https://www.h2database.com/

---

**Last Updated:** May 29, 2026
