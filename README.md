# URL Shortener

A Spring Boot and MySQL URL Shortener with REST APIs, click tracking, validation, global exception handling, a simple frontend, Postman examples, and project documentation.

## Run Locally

### Easiest: H2 In-Memory Database (No Setup Required!)

This is the quickest way to get started without installing any database:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"
```

Then open: http://localhost:8080

**H2 Features:**
- No database installation needed
- Data persists in memory during this session
- Resets on app restart
- H2 Console at: http://localhost:8080/h2-console (User: `sa`, Password: empty)

### Docker MySQL

1. Start the project MySQL container:

```bash
docker compose up -d
```

2. Start the Spring Boot app with the Docker profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=docker
```

3. Open:

```text
http://localhost:8080
```

The Docker profile uses:

```text
MySQL port: 3307
Database: url_shortener_db
Username: urluser
Password: urlpass
```

### Existing Local MySQL

1. Start MySQL.

On macOS with Homebrew:

```bash
brew services start mysql
```

If you installed MySQL with Docker Desktop instead, open Docker Desktop first and use the Docker section above.

2. Run with your MySQL credentials:

```bash
MYSQL_USERNAME=root MYSQL_PASSWORD=your_password mvn spring-boot:run
```

If your local MySQL `root` user has no password, run:

```bash
mvn spring-boot:run
```

If you use a custom database URL:

```bash
MYSQL_URL='jdbc:mysql://localhost:3306/url_shortener_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC' \
MYSQL_USERNAME=root \
MYSQL_PASSWORD=your_password \
mvn spring-boot:run
```

The configured MySQL user must have permission to create `url_shortener_db`, or create it manually first:

```sql
CREATE DATABASE url_shortener_db;
```

Recommended local project user:

```sql
CREATE USER 'urluser'@'localhost' IDENTIFIED BY 'urlpass';
GRANT ALL PRIVILEGES ON url_shortener_db.* TO 'urluser'@'localhost';
FLUSH PRIVILEGES;
```

Then run:

```bash
MYSQL_USERNAME=urluser MYSQL_PASSWORD=urlpass mvn spring-boot:run
```

Then open:

```text
http://localhost:8080
```

The app creates tables automatically with Spring Data JPA.

## Troubleshooting

### Docker not running

If you get an error about Docker daemon not running:

```text
failed to connect to the docker API
```

Either:
1. **Install and start Docker Desktop** from https://www.docker.com/products/docker-desktop
2. **OR use the H2 profile instead** (recommended for quick testing):
   ```bash
   mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"
   ```

### If startup fails with:

```text
Access denied for user 'root'@'localhost'
```

the MySQL username or password is wrong. Run with the correct environment variables:

```bash
MYSQL_USERNAME=your_user MYSQL_PASSWORD=your_password mvn spring-boot:run
```

### If startup fails with:

```text
Communications link failure
```

MySQL is not running or is not listening on the configured port. Either:
1. Start MySQL: `brew services start mysql`
2. Or use H2: `mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"`

### If Docker fails with:

```text
Cannot connect to the Docker daemon
```

open Docker Desktop first, wait until it says Docker is running, then run:

```bash
docker compose up -d
mvn spring-boot:run -Dspring-boot.run.profiles=docker
```

## Main APIs

- `POST /api/urls/shorten`
- `GET /{shortCode}`
- `GET /api/urls/{shortCode}`
- `GET /api/urls/{shortCode}/analytics`
