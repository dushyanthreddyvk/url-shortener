# URL Shortener

## Project Overview

A **Spring Boot-based URL Shortener** system that converts long URLs into compact, unique short links and provides redirect and analytics capabilities. Similar to real-world services like Bit.ly and TinyURL, this project demonstrates core backend engineering concepts including REST APIs, database design, scalability, and performance optimization.

### Why URL Shortening Matters

URL shortening is critical in modern backend systems for:
- **Scalability**: Handling millions of redirects efficiently with minimal storage
- **Analytics**: Tracking user engagement, click patterns, and traffic sources
- **User Experience**: Sharing short, memorable links across platforms
- **Resource Optimization**: Reducing bandwidth usage in data-constrained environments

Real-world applications include social media link sharing, QR codes, marketing campaigns, and API response optimization.

---

## Features

✅ **Generate Short URLs** - Convert any long URL into a 7-character unique short code  
✅ **Redirect to Original URLs** - Fast redirect mechanism with click tracking  
✅ **Unique Short Code Generation** - Collision-resistant algorithm with fallback retry logic  
✅ **Click Count Tracking** - Track every redirect and generate analytics  
✅ **REST API Support** - Full-featured REST endpoints for all operations  
✅ **Database Persistence** - Supports MySQL, H2 (in-memory), and Docker deployments  
✅ **Global Exception Handling** - Centralized error management with meaningful error responses  
✅ **Responsive Frontend** - Simple web interface for testing and interaction  

---

## Backend Concepts Used

| Concept | Implementation |
|---------|-----------------|
| **REST APIs** | POST/GET endpoints following REST conventions |
| **Spring Boot** | Dependency injection, auto-configuration, embedded Tomcat |
| **Layered Architecture** | Controller → Service → Repository → Database pattern |
| **Hashing & Encoding** | Base62 encoding for short code generation |
| **Database Integration** | Spring Data JPA with Hibernate ORM |
| **API Routing** | Spring routing with path variables and request mapping |
| **Transaction Management** | @Transactional annotations for data consistency |

---

## System Design Concepts

- **URL Mapping Pattern**: One-to-one mapping between short codes and original URLs
- **Redirect Handling**: HTTP 302 redirects for flexibility and tracking
- **Scalability Basics**: Stateless service design for horizontal scaling
- **Database Indexing**: Indexed short codes and long URLs for O(1) lookups
- **Caching Concepts**: Potential for Redis caching to reduce database hits
- **Idempotency**: Same long URL returns same short code (no duplicate mappings)

---

## Project Architecture

```
┌─────────────────┐
│   Client/UI     │
└────────┬────────┘
         │ HTTP Requests
         ↓
┌─────────────────────────┐
│   UrlController         │  → REST API Layer
│  (Request Handler)      │
└────────┬────────────────┘
         │ Delegate
         ↓
┌─────────────────────────┐
│   UrlService            │  → Business Logic Layer
│   (Core Logic)          │
└────────┬────────────────┘
         │ Query/Save
         ↓
┌─────────────────────────┐
│   Repository            │  → Data Access Layer
│   (JPA Interface)       │
└────────┬────────────────┘
         │ Execute
         ↓
┌─────────────────────────┐
│   Database              │  → Persistence Layer
│   (MySQL / H2)          │
└─────────────────────────┘
```

---

## How It Works

### 1. **URL Shortening Process**
- User submits a long URL via REST API
- Service checks if URL already exists in database (idempotency)
- If new, generates a unique 7-character short code using Base62 encoding
- Stores mapping in database (shortCode → originalURL)
- Returns short URL to client

### 2. **Short Code Generation**
- Uses `ShortCodeGenerator` for random Base62 encoding
- Generates codes like: `abc123d`, `xyz9876`, etc.
- Retry logic prevents collision (max 10 attempts)
- Time-based uniqueness for distributed systems

### 3. **Redirect Mechanism**
- User clicks short link or makes GET request
- System finds original URL in database (O(1) index lookup)
- Increments click counter in single transaction
- Returns HTTP 302 redirect to original URL
- Click tracking captured for analytics

### 4. **Click Analytics**
- Every redirect increments click counter
- Tracks creation date and last access time
- Calculates total clicks per short URL
- Useful for marketing attribution and user behavior analysis

---

## Sample API Flow

### Example: Shortening a URL

**Request:**
```bash
POST /api/urls/shorten
Content-Type: application/json

{
  "longUrl": "https://www.github.com/dushyanthreddyvk/url-shortener"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Short URL generated successfully",
  "data": {
    "shortCode": "abc123d",
    "shortUrl": "http://localhost:8080/abc123d",
    "longUrl": "https://www.github.com/dushyanthreddyvk/url-shortener",
    "clickCount": 0,
    "createdAt": "2026-05-29T14:30:00"
  }
}
```

### Example: Redirecting via Short Code

**Request:**
```bash
GET /abc123d
```

**Response:**
- HTTP 302 Found
- Location: https://www.github.com/dushyanthreddyvk/url-shortener
- Click counter incremented in database

### Example: Fetching Analytics

**Request:**
```bash
GET /api/urls/abc123d/analytics
```

**Response:**
```json
{
  "success": true,
  "message": "URL analytics fetched successfully",
  "data": {
    "shortCode": "abc123d",
    "shortUrl": "http://localhost:8080/abc123d",
    "longUrl": "https://www.github.com/dushyanthreddyvk/url-shortener",
    "clickCount": 42,
    "createdAt": "2026-05-29T14:30:00",
    "lastAccessedAt": "2026-05-29T15:45:30"
  }
}
```

---

## API Endpoints

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| **POST** | `/api/urls/shorten` | Generate short URL | `{ "longUrl": "..." }` |
| **GET** | `/{shortCode}` | Redirect to original URL | N/A |
| **GET** | `/api/urls/{shortCode}` | Fetch URL details | N/A |
| **GET** | `/api/urls/{shortCode}/analytics` | Fetch click analytics | N/A |

**Status Codes:**
- `201 Created` - Short URL successfully generated
- `200 OK` - Request successful
- `404 Not Found` - Short code does not exist
- `400 Bad Request` - Invalid input

---

## Database Design

### URL Mapping Table

```sql
CREATE TABLE url_mapping (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  short_code VARCHAR(10) UNIQUE NOT NULL,      -- Index for fast lookups
  long_url LONGTEXT NOT NULL,                   -- Original URL
  click_count BIGINT DEFAULT 0,                 -- Analytics tracking
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  last_accessed_at TIMESTAMP,
  INDEX idx_short_code (short_code),
  INDEX idx_long_url (long_url(100))
);
```

### Key Design Decisions

- **short_code**: 7-character unique identifier (index for O(1) redirect)
- **long_url**: Stored as LONGTEXT to support arbitrary URLs
- **click_count**: Incremented atomically on each access
- **Timestamps**: Track creation and usage patterns
- **Indexes**: Enable fast lookups and prevent duplicate short codes

---

## Technologies Used

| Technology | Purpose | Version |
|-----------|---------|---------|
| **Java** | Programming Language | 17+ |
| **Spring Boot** | Web Framework | 3.3.5 |
| **Spring Data JPA** | ORM & Database Abstraction | 3.3.5 |
| **MySQL** | Production Database | 8.0+ |
| **H2** | In-Memory Testing Database | 2.2+ |
| **Maven** | Build & Dependency Management | 3.8+ |
| **Tomcat** | Web Server | 10.1+ (Embedded) |
| **Jakarta** | Java Enterprise APIs | 10.0+ |

---

## Project Structure

```
url-shortener/
├── src/main/java/com/urlshortener/
│   ├── UrlShortenerApplication.java     # Spring Boot entry point
│   ├── config/
│   │   └── AppProperties.java           # Configuration properties
│   ├── controller/
│   │   └── UrlController.java           # REST API endpoints
│   ├── service/
│   │   ├── UrlService.java              # Service interface
│   │   ├── UrlServiceImpl.java           # Core business logic
│   │   └── ShortCodeGenerator.java      # Short code generation
│   ├── repository/
│   │   └── UrlMappingRepository.java    # Data access layer
│   ├── model/
│   │   └── UrlMapping.java              # Entity model
│   ├── dto/
│   │   ├── ShortenUrlRequest.java       # API request DTO
│   │   ├── UrlResponse.java             # API response DTO
│   │   ├── UrlAnalyticsResponse.java    # Analytics response DTO
│   │   ├── ApiResponse.java             # Generic API response wrapper
│   │   └── ErrorResponse.java           # Error response DTO
│   └── exception/
│       ├── UrlNotFoundException.java     # Custom exception
│       └── GlobalExceptionHandler.java  # Centralized error handling
│
├── src/main/resources/
│   ├── application.properties           # Default MySQL config
│   ├── application-h2.properties        # H2 in-memory config
│   ├── application-docker.properties    # Docker MySQL config
│   └── static/
│       ├── index.html                   # Frontend UI
│       ├── script.js                    # Frontend JavaScript
│       └── styles.css                   # Frontend styles
│
├── src/test/java/
│   └── com/urlshortener/service/
│       └── UrlServiceImplTest.java      # Unit tests
│
├── pom.xml                              # Maven configuration
├── docker-compose.yml                   # Docker MySQL setup
└── README.md                            # This file
```

---

## How to Run

### Option 1: H2 In-Memory Database (Fastest - No Setup Required)

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"
```

Then open: **http://localhost:8080**

**H2 Features:**
- No database installation needed
- Data persists in memory during this session
- Resets on app restart
- H2 Console: http://localhost:8080/h2-console (User: `sa`, Password: empty)

### Option 2: Docker MySQL (Production-like Setup)

```bash
# Start MySQL container
docker compose up -d

# Start Spring Boot app
APP_BASE_URL=http://localhost:8080 mvn spring-boot:run -Dspring-boot.run.profiles=docker
```

Then open: **http://localhost:8080**

Docker MySQL Configuration:
- Port: 3307
- Database: `url_shortener_db`
- Username: `urluser`
- Password: `urlpass`

### Option 3: Local MySQL

```bash
# Start MySQL (macOS with Homebrew)
brew services start mysql

# Run with credentials
APP_BASE_URL=http://localhost:8080 MYSQL_USERNAME=root MYSQL_PASSWORD=your_password mvn spring-boot:run
```

### Option 4: Build & Run JAR

```bash
# Build
mvn clean package

# Run
java -jar target/url-shortener-0.0.1-SNAPSHOT.jar
```

---

## Screenshots

### Web Interface

**Homepage - URL Shortening Dashboard**
```
[Screenshot: Main page with URL input field and Generate button]
```

**Features Shown:**
- URL input field with placeholder text
- Generate button for shortening URLs
- Result section displaying short URL with copy functionality
- Redirect test section
- Click analytics section

### API Examples

**POST Request - Shorten URL**
```
[Screenshot: Postman POST request to /api/urls/shorten]
```

**GET Request - Redirect**
```
[Screenshot: Browser showing redirect functionality]
```

**Analytics Response**
```
[Screenshot: Analytics data with click count and timestamps]
```

### Database Structure

**H2 Console**
```
[Screenshot: H2 database console showing url_mapping table]
```

---

## Screenshots Directory

<img width="1470" height="956" alt="Screenshot 2026-05-29 at 2 34 37 PM" src="https://github.com/user-attachments/assets/8262a320-3654-487d-9f6c-1c75b773bfa4" />

<img width="1470" height="956" alt="Screenshot 2026-05-29 at 2 34 48 PM" src="https://github.com/user-attachments/assets/f3185c40-f0f3-44ed-a282-4ba45ad4cf64" />


---

## Postman Collection

Import the provided Postman collection for API testing:
- File: `postman/URL-Shortener.postman_collection.json`
- Contains pre-configured requests for all endpoints

---

## Troubleshooting

### Docker not running
```bash
# Use H2 instead
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"
```

### MySQL connection error
```bash
# Start MySQL
brew services start mysql

# Or use H2 profile
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"
```

### Short URL shows localhost instead of domain
```bash
# Set APP_BASE_URL when running
APP_BASE_URL=http://localhost:8080 mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"
```

---

## Future Improvements

### Performance & Scalability
- **Redis Caching**: Cache frequently accessed URLs to reduce database queries
- **Connection Pooling**: Optimize database connection management
- **Read Replicas**: Distribute read-heavy operations across multiple databases
- **Asynchronous Processing**: Queue analytics updates for high-traffic scenarios

### Feature Enhancements
- **Custom Aliases**: Allow users to set custom short codes
- **URL Expiration**: Set TTL for short URLs
- **Rate Limiting**: Prevent abuse with per-IP request limits
- **User Authentication**: Track URLs per user
- **Batch Operations**: Shorten multiple URLs in one request
- **URL Validation**: Verify URL accessibility before shortening

### Observability & Security
- **Structured Logging**: ELK stack integration for centralized logging
- **Metrics & Monitoring**: Prometheus metrics for system health
- **Security**: HTTPS, CORS configuration, input sanitization
- **API Versioning**: Support multiple API versions for backward compatibility
- **Rate Limiting**: Prevent abuse and DDoS attacks

### Deployment & Infrastructure
- **Docker Containerization**: Production-ready Docker images
- **Kubernetes Orchestration**: Deploy across multiple pods
- **CI/CD Pipeline**: GitHub Actions for automated testing and deployment
- **Analytics Dashboard**: Real-time visualization of click data
- **Admin Panel**: Manage URLs, view statistics, user management

---

## Key Learning Outcomes

This project demonstrates:

✅ **REST API Design** - Proper endpoint design, HTTP status codes, request/response handling  
✅ **Layered Architecture** - Clean separation of concerns (Controller → Service → Repository)  
✅ **Database Design** - Indexing strategies, normalization, query optimization  
✅ **Transaction Management** - ACID properties and data consistency  
✅ **Exception Handling** - Custom exceptions and global error handling  
✅ **Spring Boot Best Practices** - Dependency injection, configuration management  
✅ **System Design** - Scalability, performance, and reliability considerations  
✅ **Backend Engineering** - Production-ready code patterns and practices  

---

## Conclusion

The URL Shortener project is a comprehensive backend system that demonstrates core software engineering principles. It showcases how real-world services like Bit.ly handle URL shortening at scale with features like click tracking, database optimization, and REST API design.

This project is ideal for:
- **Portfolio Building**: Demonstrating backend engineering skills
- **Interview Preparation**: Understanding system design and architecture
- **Learning**: Practical experience with Spring Boot, MySQL, and REST APIs
- **Production Deployment**: Can be extended for real-world applications

---

## License

This project is open-source and available under the MIT License.

## Author

**Dushyanth Reddy V K**  
GitHub: [@dushyanthreddyvk](https://github.com/dushyanthreddyvk)  
Project: [url-shortener](https://github.com/dushyanthreddyvk/url-shortener)
