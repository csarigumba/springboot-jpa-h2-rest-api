# Research: User Management REST API

**Feature**: User Management REST API
**Date**: 2025-10-30
**Phase**: 0 - Technical Research

## Technology Stack Decisions

### Java Version

**Decision**: Java 17 (LTS)

**Rationale**:
- Long-term support through September 2029
- Widely adopted in enterprise and tutorial materials
- Compatible with Spring Boot 3.x
- Good balance between modern features and stability for learning
- Java 21 (newer LTS) is also acceptable but 17 has more learning resources

**Alternatives Considered**:
- Java 11: Still supported but older features; Java 17 is now the recommended baseline for Spring Boot 3
- Java 21: Newest LTS but fewer learning resources; good choice if wanting latest features

### Spring Boot Version

**Decision**: Spring Boot 3.2.x (latest stable 3.x)

**Rationale**:
- Current recommended version for new projects
- Requires Java 17+ (aligns with Java choice)
- Native support for Docker and containerization
- Spring Data JPA well-integrated
- Extensive documentation and community support
- Spring Boot 3.x is the current generation (2.x approaching EOL)

**Alternatives Considered**:
- Spring Boot 2.7.x: Nearing end-of-life, would require migration soon
- Spring Boot 3.3.x: Cutting edge but may have fewer stable examples

### Build Tool

**Decision**: Maven

**Rationale**:
- More beginner-friendly with XML-based configuration
- Ubiquitous in Spring Boot tutorials and documentation
- Spring Initializr default
- Simpler dependency management for learning projects
- Most enterprise Spring Boot projects use Maven

**Alternatives Considered**:
- Gradle: More modern, Kotlin DSL, but steeper learning curve; better for complex builds

### H2 Database Configuration

**Decision**: File-based H2 with persistent storage

**Rationale**:
- Persists data across application restarts (requirement from spec FR-012)
- Simple file-based storage in `./data` directory
- H2 console enabled for debugging and learning
- Zero external dependencies (perfect for Docker deployment)
- Configuration: `spring.datasource.url=jdbc:h2:file:./data/userdb`

**Alternatives Considered**:
- In-memory H2: Simple but loses data on restart (violates FR-012)
- Embedded H2 with absolute path: Less portable across environments

### Docker Strategy

**Decision**: Multi-stage Docker build with JDK 17 base image

**Rationale**:
- Multi-stage build: compile with Maven, run with JRE (smaller final image)
- Use official OpenJDK 17 slim images
- Expose port 8080 for REST API
- Volume mount for H2 database file persistence
- Simple `docker build` and `docker run` commands for portfolio demonstration

**Configuration**:
```dockerfile
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
# ... build steps ...

# Stage 2: Run
FROM eclipse-temurin:17-jre-alpine
# ... runtime setup ...
```

**Alternatives Considered**:
- Single-stage build: Simpler but larger image size
- Buildpacks: More automated but less educational for learning Docker

### REST API Design Patterns

**Decision**: RESTful conventions with standard HTTP verbs and DTOs

**Rationale**:
- POST /api/users - Create (returns 201 Created)
- GET /api/users - List all (returns 200 OK)
- GET /api/users/{id} - Get by ID (returns 200 OK or 404 Not Found)
- PUT /api/users/{id} - Update (returns 200 OK or 404 Not Found)
- DELETE /api/users/{id} - Delete (returns 204 No Content or 404 Not Found)
- Use DTOs to separate API contract from domain model
- JSON only (Spring Boot default)

**Alternatives Considered**:
- PATCH for partial updates: More complex, PUT sufficient for learning
- HATEOAS: Over-engineering for simple CRUD demo
- GraphQL: Different paradigm, REST more fundamental for learning

### Validation Strategy

**Decision**: Bean Validation (Jakarta Validation) with @Valid

**Rationale**:
- Built into Spring Boot Starter Validation
- Declarative validation with annotations (@NotNull, @Email, @Size)
- Automatic 400 Bad Request responses with validation errors
- Industry standard approach
- Examples: `@Email` for email format, `@NotBlank` for required fields

**Alternatives Considered**:
- Manual validation in service layer: More code, less declarative
- Custom validators: Overkill for simple email/name validation

### Exception Handling Pattern

**Decision**: @ControllerAdvice with custom exceptions

**Rationale**:
- Centralized error handling in GlobalExceptionHandler
- Custom exceptions: UserNotFoundException, DuplicateEmailException
- Consistent error response format across all endpoints
- Demonstrates professional Spring Boot error handling
- Returns appropriate HTTP status codes automatically

**Error Response Format**:
```json
{
  "timestamp": "2025-10-30T...",
  "status": 404,
  "error": "Not Found",
  "message": "User with ID 123 not found",
  "path": "/api/users/123"
}
```

**Alternatives Considered**:
- ResponseEntity everywhere: Scattered error handling logic
- Problem Details (RFC 7807): More complex, overkill for demo

### Dependency Injection Pattern

**Decision**: Constructor injection (no @Autowired annotation)

**Rationale**:
- Modern Spring Boot best practice (recommended over field injection)
- Immutable dependencies (final fields)
- Better testability (can pass mocks in constructor)
- Required dependencies explicit
- Lombok @RequiredArgsConstructor can generate constructor

**Example**:
```java
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

**Alternatives Considered**:
- Field injection (@Autowired on fields): Deprecated pattern, not recommended
- Setter injection: Allows mutable dependencies, less clear

### Package Structure

**Decision**: Layer-based package organization

**Rationale**:
- Clear separation: entity, repository, service, controller, dto, exception
- Easy to navigate for learners
- Matches Spring Boot documentation examples
- Demonstrates layered architecture visually in package structure

**Alternatives Considered**:
- Feature-based: All user-related classes in one package (better for larger apps)
- Flat structure: No organization, confusing for learning

## Spring Boot Dependencies (Minimal Set)

### Required Starters

1. **spring-boot-starter-web**
   - Provides Spring MVC for REST controllers
   - Embedded Tomcat server
   - Jackson for JSON serialization

2. **spring-boot-starter-data-jpa**
   - Spring Data JPA repositories
   - Hibernate as JPA implementation
   - Transaction management

3. **spring-boot-starter-validation**
   - Bean Validation (Jakarta Validation)
   - Hibernate Validator implementation

4. **h2**
   - H2 database engine
   - H2 console (optional, useful for debugging)

### Optional but Recommended

5. **spring-boot-starter-actuator** (optional)
   - Health checks (/actuator/health)
   - Useful for Docker health monitoring
   - Info endpoint for application metadata

6. **lombok** (optional but highly recommended)
   - Reduces boilerplate (@Data, @RequiredArgsConstructor)
   - Makes code more readable
   - Widely used in professional Spring Boot projects

### Explicitly Excluded

- spring-boot-starter-security: Out of scope (no authentication required)
- spring-boot-starter-test: Testing optional per constitution
- spring-boot-devtools: Not needed in Docker deployment

## Configuration Best Practices

### application.yml Structure

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

**Key Decisions**:
- `ddl-auto: update` - Auto-create tables on startup (good for demo/learning)
- `show-sql: true` - See SQL queries (educational)
- H2 console enabled - Allows database inspection during development
- Debug logging for application package - Helps understand Spring Boot flow

## Implementation Order (Bottom-Up)

Per constitution Development Workflow section:

1. **Entity Layer**: User.java with JPA annotations
2. **Repository Layer**: UserRepository interface (Spring Data JPA)
3. **Service Layer**: UserService interface → UserServiceImpl
4. **Controller Layer**: UserController with REST endpoints
5. **Exception Handling**: Custom exceptions + GlobalExceptionHandler
6. **DTOs**: Request/Response objects
7. **Configuration**: application.yml final tuning
8. **Docker**: Dockerfile and docker-compose.yml

## Learning Resources Referenced

- Spring Boot Official Documentation: https://spring.io/projects/spring-boot
- Spring Data JPA Reference: https://spring.io/projects/spring-data-jpa
- H2 Database Documentation: https://h2database.com
- Docker Best Practices: https://docs.docker.com/develop/dev-best-practices/

## Risk Mitigation

| Risk | Mitigation |
|------|------------|
| H2 file corruption | Use `DB_CLOSE_ON_EXIT=FALSE` and proper shutdown hooks |
| Port conflicts in Docker | Document port mapping, use docker-compose for clarity |
| Validation edge cases | Test email formats, empty strings, null values manually |
| Docker volume persistence | Use named volumes or explicit volume mounts in documentation |
| JPA lazy loading issues | Use DTOs to avoid serialization problems with entities |

## Open Questions Resolved

1. **Q: Maven or Gradle?** → A: Maven (simpler for learning)
2. **Q: Java 17 or 21?** → A: Java 17 (more learning resources)
3. **Q: In-memory or file-based H2?** → A: File-based (persistence requirement)
4. **Q: Separate service interface?** → A: Yes (demonstrates good practice, dependency inversion)
5. **Q: Use Lombok?** → A: Yes (reduces boilerplate, industry standard)
6. **Q: Include Actuator?** → A: Optional, recommended for Docker health checks
