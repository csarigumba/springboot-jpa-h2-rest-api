# Data Model: User Management REST API

**Feature**: User Management REST API
**Date**: 2025-10-30
**Phase**: 1 - Data Model Design

## Domain Entities

### User Entity

**Purpose**: Represents a registered user account in the system

**Attributes**:

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | Primary Key, Auto-generated | Unique identifier assigned by system |
| name | String | NOT NULL, Max 100 chars | User's full name |
| email | String | NOT NULL, UNIQUE, Valid email format | User's email address (business key) |
| createdAt | LocalDateTime | NOT NULL, Auto-set on creation | Timestamp when user was registered |
| updatedAt | LocalDateTime | NOT NULL, Auto-updated | Timestamp of last modification |

**Business Rules**:
- Email must be unique across all users (enforced at database and application level)
- Email must follow standard email format (validated via Bean Validation @Email)
- Name is required and cannot be blank
- Name length capped at 100 characters (reasonable limit per spec assumption)
- Timestamps are system-managed (users cannot set these)
- ID is auto-generated; users cannot specify IDs

**JPA Annotations Guidance**:
```java
@Entity
@Table(name = "users",
       uniqueConstraints = @UniqueConstraint(columnNames = "email"))
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

**State Lifecycle**:
1. **New** → User object created in memory (no ID yet)
2. **Persisted** → Saved to database, ID assigned, createdAt set
3. **Managed** → JPA tracks changes, updatedAt modified on updates
4. **Removed** → Hard delete, no soft delete (per spec out-of-scope)

## Data Transfer Objects (DTOs)

DTOs decouple API contracts from domain entities, preventing JPA lazy-loading issues and allowing API evolution without entity changes.

### UserCreateRequest

**Purpose**: Capture user input for user creation

**Fields**:
| Field | Type | Validation | Description |
|-------|------|------------|-------------|
| name | String | @NotBlank, @Size(max=100) | User's name |
| email | String | @NotBlank, @Email | User's email address |

**Example JSON**:
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com"
}
```

### UserUpdateRequest

**Purpose**: Capture user input for user updates

**Fields**:
| Field | Type | Validation | Description |
|-------|------|------------|-------------|
| name | String | @NotBlank, @Size(max=100) | Updated user name |
| email | String | @NotBlank, @Email | Updated email address |

**Note**: Full replacement (PUT semantics). Partial updates not required for learning demo.

**Example JSON**:
```json
{
  "name": "John Updated",
  "email": "john.updated@example.com"
}
```

### UserResponse

**Purpose**: Return user data to API clients

**Fields**:
| Field | Type | Description |
|-------|------|-------------|
| id | Long | User's unique identifier |
| name | String | User's name |
| email | String | User's email address |
| createdAt | String | ISO 8601 timestamp of creation (e.g., "2025-10-30T14:30:00") |
| updatedAt | String | ISO 8601 timestamp of last update |

**Example JSON**:
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john.doe@example.com",
  "createdAt": "2025-10-30T14:30:00",
  "updatedAt": "2025-10-30T14:30:00"
}
```

**Mapping Logic**:
- User entity → UserResponse: Map all fields, format LocalDateTime to ISO 8601 strings
- UserCreateRequest → User entity: Map name and email, system sets timestamps and ID
- UserUpdateRequest → User entity: Update name and email, preserve ID and createdAt, update updatedAt

## Error Response Format

**Purpose**: Consistent error structure across all endpoints

**Fields**:
| Field | Type | Description |
|-------|------|-------------|
| timestamp | String | ISO 8601 timestamp when error occurred |
| status | Integer | HTTP status code (e.g., 404, 400, 409) |
| error | String | HTTP status reason phrase (e.g., "Not Found") |
| message | String | Human-readable error description |
| path | String | API endpoint path that caused the error |

**Example JSON (404 Not Found)**:
```json
{
  "timestamp": "2025-10-30T14:35:12",
  "status": 404,
  "error": "Not Found",
  "message": "User with ID 999 not found",
  "path": "/api/users/999"
}
```

**Example JSON (409 Conflict - Duplicate Email)**:
```json
{
  "timestamp": "2025-10-30T14:36:20",
  "status": 409,
  "error": "Conflict",
  "message": "User with email 'john@example.com' already exists",
  "path": "/api/users"
}
```

**Example JSON (400 Bad Request - Validation)**:
```json
{
  "timestamp": "2025-10-30T14:37:45",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed: email must be a valid email address",
  "path": "/api/users"
}
```

## Database Schema

**Table**: `users`

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Auto-generated |
| name | VARCHAR(100) | NOT NULL | User name |
| email | VARCHAR(255) | NOT NULL, UNIQUE | Unique email |
| created_at | TIMESTAMP | NOT NULL | Auto-set on insert |
| updated_at | TIMESTAMP | NOT NULL | Auto-updated |

**Indexes**:
- Primary key index on `id` (automatic)
- Unique index on `email` (enforces uniqueness, speeds up lookups)

**H2-Specific Notes**:
- AUTO_INCREMENT used for ID generation
- TIMESTAMP maps to Java LocalDateTime
- File location: `./data/userdb.mv.db` (H2 file format)
- No migration scripts needed (ddl-auto: update handles schema creation)

## Validation Rules Summary

### Field-Level Validation (Bean Validation)

| Field | Rules | Error Message |
|-------|-------|---------------|
| name | @NotBlank | "Name is required" |
| name | @Size(max=100) | "Name must not exceed 100 characters" |
| email | @NotBlank | "Email is required" |
| email | @Email | "Email must be a valid email address" |

### Business-Level Validation (Service Layer)

| Rule | Check Location | Exception Thrown |
|------|---------------|------------------|
| Email uniqueness | UserService | DuplicateEmailException → 409 Conflict |
| User exists for update | UserService | UserNotFoundException → 404 Not Found |
| User exists for delete | UserService | UserNotFoundException → 404 Not Found |
| User exists for retrieval | UserService | UserNotFoundException → 404 Not Found |

## Entity Relationships

**Current State**: Single entity, no relationships

**Future Considerations** (Out of Scope for Current Feature):
- User → UserRole (many-to-one) for role-based access
- User → Address (one-to-many) for multiple addresses
- User → AuditLog (one-to-many) for change tracking

These are explicitly out of scope per spec, mentioned here only for architectural awareness.

## Repository Interface

**Interface**: `UserRepository`

**Extends**: `JpaRepository<User, Long>`

**Custom Query Methods** (Spring Data JPA derived queries):

| Method | Query | Purpose |
|--------|-------|---------|
| findByEmail(String email) | SELECT u FROM User u WHERE u.email = :email | Check email uniqueness, retrieve by email |
| existsByEmail(String email) | SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email | Fast email existence check |

**Inherited Methods** (from JpaRepository):
- save(User user) → Insert or update
- findById(Long id) → Retrieve by ID (returns Optional<User>)
- findAll() → Retrieve all users
- deleteById(Long id) → Delete by ID
- existsById(Long id) → Check if user exists

## Data Flow Examples

### Create User Flow
1. Client sends UserCreateRequest JSON
2. Controller validates (@Valid), converts to User entity
3. Service checks email uniqueness via `existsByEmail()`
4. If unique: Repository saves entity, returns persisted User with ID
5. Service converts User → UserResponse DTO
6. Controller returns 201 Created with UserResponse JSON

### Update User Flow
1. Client sends UserUpdateRequest JSON to /api/users/{id}
2. Controller validates, passes ID and DTO to service
3. Service retrieves existing User by ID (or throws UserNotFoundException)
4. Service checks if new email conflicts with other users
5. If valid: Update entity fields, repository saves changes
6. Service converts User → UserResponse DTO
7. Controller returns 200 OK with UserResponse JSON

### Delete User Flow
1. Client sends DELETE to /api/users/{id}
2. Controller passes ID to service
3. Service checks if user exists (throws UserNotFoundException if not)
4. Repository deletes user
5. Controller returns 204 No Content

## Consistency Guarantees

- **ACID Transactions**: All service methods transactional (@Transactional)
- **Isolation Level**: Default (READ_COMMITTED) sufficient for demo
- **Optimistic Locking**: Not required (low concurrency expected)
- **Referential Integrity**: N/A (single table)
- **Email Uniqueness**: Enforced at database level (UNIQUE constraint) and application level (service validation)

## Testing Data Considerations

### Sample Test Data (Optional data.sql)

```sql
INSERT INTO users (name, email, created_at, updated_at)
VALUES
  ('Alice Johnson', 'alice@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Bob Smith', 'bob@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Charlie Brown', 'charlie@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
```

**Note**: Optional for demo purposes. Application should work with empty database.

### Edge Case Data for Manual Testing

- Empty database (test listing returns empty array)
- Long names (99-100 characters)
- Various email formats (with +, -, .)
- Invalid emails (missing @, malformed)
- Duplicate emails (expect 409 Conflict)
- Non-existent IDs (expect 404 Not Found)
