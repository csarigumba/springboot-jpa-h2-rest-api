# Quickstart Guide: User Management REST API

**Feature**: User Management REST API
**Purpose**: Quick reference for building, running, and testing the Spring Boot application
**Audience**: Developers implementing the feature

## Prerequisites

- Java 17 or later (JDK)
- Maven 3.6+ or Gradle 7+
- Docker and Docker Compose (for containerized deployment)
- REST client (curl, Postman, or HTTPie) for testing
- Text editor or IDE (IntelliJ IDEA, VS Code, Eclipse)

## Project Initialization

### Option 1: Spring Initializr (Recommended for Learning)

1. Visit https://start.spring.io
2. Configure project:
   - **Project**: Maven
   - **Language**: Java
   - **Spring Boot**: 3.2.x (latest stable)
   - **Packaging**: Jar
   - **Java**: 17
   - **Group**: com.example
   - **Artifact**: user-api
   - **Dependencies**:
     - Spring Web
     - Spring Data JPA
     - H2 Database
     - Validation
     - Lombok (optional)

3. Click "Generate" and extract zip to project directory
4. Import into your IDE

### Option 2: Manual Maven Setup

Create `pom.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>

    <groupId>com.example</groupId>
    <artifactId>user-api</artifactId>
    <version>1.0.0</version>
    <name>User Management API</name>
    <description>Spring Boot REST API for user management with JPA and H2</description>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Web for REST controllers -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Spring Data JPA for repository layer -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <!-- H2 in-memory database -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Bean Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- Lombok (optional but recommended) -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

## Configuration

Create `src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: user-api

  datasource:
    url: jdbc:h2:file:./data/userdb
    driver-class-name: org.h2.Driver
    username: sa
    password:

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true

  h2:
    console:
      enabled: true
      path: /h2-console

server:
  port: 8080

logging:
  level:
    com.example.userapi: DEBUG
    org.springframework.web: INFO
```

**Configuration Notes**:
- `jdbc:h2:file:./data/userdb` - File-based H2 database in `./data` directory
- `ddl-auto: update` - Auto-creates/updates database schema on startup
- `show-sql: true` - Logs SQL queries (useful for learning)
- H2 console enabled at `/h2-console` for database inspection

## Implementation Order

Follow this bottom-up sequence per constitution:

### 1. Create Entity (User.java)

```java
package com.example.userapi.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "users",
       uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

### 2. Create Repository (UserRepository.java)

```java
package com.example.userapi.repository;

import com.example.userapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

### 3. Create DTOs

**UserCreateRequest.java**:
```java
package com.example.userapi.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserCreateRequest {
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;
}
```

**UserUpdateRequest.java**: (Same structure as UserCreateRequest)

**UserResponse.java**:
```java
package com.example.userapi.dto;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String createdAt;
    private String updatedAt;
}
```

### 4. Create Custom Exceptions

**UserNotFoundException.java**:
```java
package com.example.userapi.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("User with ID " + id + " not found");
    }
}
```

**DuplicateEmailException.java**:
```java
package com.example.userapi.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super("User with email '" + email + "' already exists");
    }
}
```

### 5. Create Global Exception Handler

```java
package com.example.userapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(
            UserNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateEmail(
            DuplicateEmailException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, request);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            String message, HttpStatus status, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(body, status);
    }
}
```

### 6. Create Service Interface and Implementation

**UserService.java**:
```java
package com.example.userapi.service;

import com.example.userapi.dto.*;
import java.util.List;

public interface UserService {
    UserResponse createUser(UserCreateRequest request);
    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
    UserResponse updateUser(Long id, UserUpdateRequest request);
    void deleteUser(Long id);
}
```

**UserServiceImpl.java**: Implement business logic with validation

### 7. Create Controller

```java
package com.example.userapi.controller;

import com.example.userapi.dto.*;
import com.example.userapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse user = userService.createUser(request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
```

## Running the Application

### Local Development

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run

# Application starts on http://localhost:8080
```

### Docker Deployment

Create `Dockerfile`:

```dockerfile
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/user-api-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and run:

```bash
# Build Docker image
docker build -t user-api:1.0 .

# Run container
docker run -p 8080:8080 -v $(pwd)/data:/app/data user-api:1.0
```

Create `docker-compose.yml` (optional):

```yaml
version: '3.8'
services:
  user-api:
    build: .
    ports:
      - "8080:8080"
    volumes:
      - ./data:/app/data
    environment:
      - SPRING_PROFILES_ACTIVE=prod
```

Run with Docker Compose:
```bash
docker-compose up
```

## Testing the API

### Using curl

```bash
# Create a user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com"}'

# Get all users
curl http://localhost:8080/api/users

# Get user by ID
curl http://localhost:8080/api/users/1

# Update user
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"John Updated","email":"john.updated@example.com"}'

# Delete user
curl -X DELETE http://localhost:8080/api/users/1
```

### Using HTTPie

```bash
# Create a user
http POST :8080/api/users name="John Doe" email="john@example.com"

# Get all users
http :8080/api/users

# Get user by ID
http :8080/api/users/1

# Update user
http PUT :8080/api/users/1 name="John Updated" email="john.updated@example.com"

# Delete user
http DELETE :8080/api/users/1
```

### H2 Console Access

1. Navigate to http://localhost:8080/h2-console
2. Use connection settings:
   - JDBC URL: `jdbc:h2:file:./data/userdb`
   - Username: `sa`
   - Password: (leave empty)
3. Click "Connect"
4. Run SQL queries: `SELECT * FROM users;`

## Validation Testing

Test edge cases manually:

```bash
# Test invalid email
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","email":"invalid-email"}'
# Expect: 400 Bad Request

# Test duplicate email
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice","email":"john@example.com"}'
# Expect: 409 Conflict (if john@example.com exists)

# Test missing required field
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com"}'
# Expect: 400 Bad Request

# Test non-existent user
curl http://localhost:8080/api/users/999
# Expect: 404 Not Found
```

## Common Issues & Solutions

| Issue | Cause | Solution |
|-------|-------|----------|
| Port 8080 already in use | Another process using port | Change `server.port` in application.yml or stop conflicting process |
| Database file locked | H2 file in use by another instance | Stop all running instances, delete `./data/userdb.lock` file |
| Lombok not working | IDE plugin missing | Install Lombok plugin for your IDE and enable annotation processing |
| Validation not triggered | Missing @Valid annotation | Add @Valid to controller method parameters |
| H2 console won't connect | Wrong JDBC URL | Ensure URL matches application.yml: `jdbc:h2:file:./data/userdb` |

## Development Workflow Checklist

- [ ] Entity created with JPA annotations
- [ ] Repository interface extends JpaRepository
- [ ] Service interface and implementation completed
- [ ] DTOs created with validation annotations
- [ ] Custom exceptions defined
- [ ] Global exception handler implemented
- [ ] Controller with REST endpoints created
- [ ] application.yml configured
- [ ] Application runs without errors
- [ ] All endpoints tested manually
- [ ] Validation scenarios tested
- [ ] Error responses verified
- [ ] H2 console accessible and data visible
- [ ] Dockerfile created and tested
- [ ] Docker image builds successfully
- [ ] Container runs and API accessible

## Next Steps

After implementation:
1. Verify all functional requirements from spec.md are met
2. Test all acceptance scenarios from user stories
3. Validate error handling for all edge cases
4. Document any deviations or additional features
5. Prepare demo for portfolio showcase

## References

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA Reference](https://spring.io/projects/spring-data-jpa)
- [H2 Database Documentation](https://h2database.com/html/main.html)
- [OpenAPI Specification](./contracts/openapi.yaml)
- [Data Model](./data-model.md)
- [Implementation Plan](./plan.md)
