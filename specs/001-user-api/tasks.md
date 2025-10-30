---

description: "Task list for User Management REST API implementation"
---

# Tasks: User Management REST API

**Input**: Design documents from `/specs/001-user-api/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Testing is LOW priority per constitution - manual testing via REST client is sufficient for learning demo.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3, US4)
- Include exact file paths in descriptions

## Path Conventions

- Single Spring Boot project: `src/main/java/com/example/userapi/`, `src/main/resources/`
- Maven configuration: `pom.xml` at repository root
- Docker: `Dockerfile`, `docker-compose.yml` at repository root

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [X] T001 Initialize Spring Boot project with Maven using Spring Initializr configuration (Java 17, Spring Boot 3.2.x)
- [X] T002 Create pom.xml with dependencies: spring-boot-starter-web, spring-boot-starter-data-jpa, h2, spring-boot-starter-validation, lombok
- [X] T003 Create src/main/resources/application.yml with H2 file-based database configuration
- [X] T004 [P] Create package structure: entity/, repository/, service/, controller/, dto/, exception/ under src/main/java/com/example/userapi/
- [X] T005 [P] Create main application class UserApiApplication.java in src/main/java/com/example/userapi/
- [X] T006 Verify application starts successfully with `mvn spring-boot:run`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T007 [P] Create User entity class in src/main/java/com/example/userapi/entity/User.java with JPA annotations (@Entity, @Table, @Id, @GeneratedValue)
- [X] T008 [P] Add User entity fields: id (Long), name (String), email (String), createdAt (LocalDateTime), updatedAt (LocalDateTime) with appropriate column constraints
- [X] T009 [P] Add @PrePersist and @PreUpdate lifecycle methods to User entity for automatic timestamp management
- [X] T010 Create UserRepository interface in src/main/java/com/example/userapi/repository/UserRepository.java extending JpaRepository<User, Long>
- [X] T011 Add custom query methods to UserRepository: findByEmail(String email) and existsByEmail(String email)
- [X] T012 [P] Create UserNotFoundException in src/main/java/com/example/userapi/exception/UserNotFoundException.java
- [X] T013 [P] Create DuplicateEmailException in src/main/java/com/example/userapi/exception/DuplicateEmailException.java
- [X] T014 Create GlobalExceptionHandler in src/main/java/com/example/userapi/exception/GlobalExceptionHandler.java with @ControllerAdvice
- [X] T015 Add exception handler methods in GlobalExceptionHandler for UserNotFoundException (404) and DuplicateEmailException (409)
- [X] T016 Add validation error handler in GlobalExceptionHandler for MethodArgumentNotValidException (400)
- [X] T017 [P] Create UserCreateRequest DTO in src/main/java/com/example/userapi/dto/UserCreateRequest.java with validation annotations
- [X] T018 [P] Create UserUpdateRequest DTO in src/main/java/com/example/userapi/dto/UserUpdateRequest.java with validation annotations
- [X] T019 [P] Create UserResponse DTO in src/main/java/com/example/userapi/dto/UserResponse.java
- [X] T020 Create UserService interface in src/main/java/com/example/userapi/service/UserService.java with method signatures for CRUD operations

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Create New User (Priority: P1) 🎯 MVP

**Goal**: Enable user registration with name and email, enforcing email uniqueness and validation

**Independent Test**: Submit POST request to /api/users with valid JSON (name, email), verify 201 Created response with user ID and timestamps; test duplicate email returns 409 Conflict; test invalid data returns 400 Bad Request

### Implementation for User Story 1

- [X] T021 [US1] Implement createUser method in UserServiceImpl.java in src/main/java/com/example/userapi/service/UserServiceImpl.java
- [X] T022 [US1] Add email uniqueness check in createUser using existsByEmail before save
- [X] T023 [US1] Add entity-to-DTO mapping logic in createUser (User → UserResponse)
- [X] T024 [US1] Add DTO-to-entity mapping logic in createUser (UserCreateRequest → User)
- [X] T025 [US1] Create UserController in src/main/java/com/example/userapi/controller/UserController.java with @RestController and @RequestMapping("/api/users")
- [X] T026 [US1] Implement POST /api/users endpoint in UserController with @Valid UserCreateRequest, returning 201 Created
- [X] T027 [US1] Add constructor injection of UserService in UserController
- [X] T028 [US1] Add JavaDoc comments to createUser endpoint explaining purpose and parameters
- [X] T029 [US1] Test createUser endpoint manually: valid user creation returns 201 with user details
- [X] T030 [US1] Test createUser endpoint manually: duplicate email returns 409 Conflict
- [X] T031 [US1] Test createUser endpoint manually: invalid email format returns 400 Bad Request
- [X] T032 [US1] Test createUser endpoint manually: missing required fields return 400 Bad Request

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - Retrieve User Information (Priority: P2)

**Goal**: Enable retrieval of individual users by ID and listing of all users

**Independent Test**: Create test users via POST, then GET /api/users/{id} returns 200 OK with user details; GET /api/users returns 200 OK with array of all users; GET non-existent ID returns 404 Not Found

### Implementation for User Story 2

- [X] T033 [P] [US2] Implement getUserById method in UserServiceImpl.java throwing UserNotFoundException if not found
- [X] T034 [P] [US2] Implement getAllUsers method in UserServiceImpl.java returning list of UserResponse DTOs
- [X] T035 [US2] Add entity-to-DTO mapping for getUserById and getAllUsers
- [X] T036 [US2] Implement GET /api/users/{id} endpoint in UserController returning 200 OK or 404 Not Found
- [X] T037 [US2] Implement GET /api/users endpoint in UserController returning 200 OK with list
- [X] T038 [US2] Add JavaDoc comments to retrieval endpoints
- [X] T039 [US2] Test getUserById endpoint manually: existing user returns 200 OK with complete profile
- [X] T040 [US2] Test getUserById endpoint manually: non-existent user returns 404 Not Found with error message
- [X] T041 [US2] Test getAllUsers endpoint manually: returns all users in consistent JSON format
- [X] T042 [US2] Test getAllUsers endpoint manually: empty database returns empty array

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase 5: User Story 3 - Update User Information (Priority: P3)

**Goal**: Enable updating user name and email while maintaining uniqueness constraints

**Independent Test**: Create user, submit PUT /api/users/{id} with updated data, verify 200 OK with updated fields and new updatedAt timestamp; test email conflict returns 409; test non-existent ID returns 404

### Implementation for User Story 3

- [X] T043 [US3] Implement updateUser method in UserServiceImpl.java checking user existence first
- [X] T044 [US3] Add email uniqueness validation in updateUser (skip check if email unchanged)
- [X] T045 [US3] Add logic to update only name and email fields, preserving id and createdAt
- [X] T046 [US3] Ensure updatedAt timestamp is automatically updated via @PreUpdate
- [X] T047 [US3] Implement PUT /api/users/{id} endpoint in UserController with @Valid UserUpdateRequest
- [X] T048 [US3] Add JavaDoc comments to updateUser endpoint
- [X] T049 [US3] Test updateUser endpoint manually: valid update returns 200 OK with updated user
- [X] T050 [US3] Test updateUser endpoint manually: email conflict with another user returns 409 Conflict
- [X] T051 [US3] Test updateUser endpoint manually: non-existent user returns 404 Not Found
- [X] T052 [US3] Test updateUser endpoint manually: invalid data returns 400 Bad Request
- [X] T053 [US3] Verify updatedAt timestamp changes after update while createdAt remains unchanged

**Checkpoint**: At this point, User Stories 1, 2, AND 3 should all work independently

---

## Phase 6: User Story 4 - Delete User (Priority: P4)

**Goal**: Enable permanent removal of user accounts from system

**Independent Test**: Create user, send DELETE /api/users/{id}, verify 204 No Content; subsequent GET for same ID returns 404 Not Found

### Implementation for User Story 4

- [X] T054 [US4] Implement deleteUser method in UserServiceImpl.java checking user existence before delete
- [X] T055 [US4] Use deleteById from JpaRepository to remove user permanently
- [X] T056 [US4] Implement DELETE /api/users/{id} endpoint in UserController returning 204 No Content
- [X] T057 [US4] Add JavaDoc comments to deleteUser endpoint
- [X] T058 [US4] Test deleteUser endpoint manually: existing user deletion returns 204 No Content
- [X] T059 [US4] Test deleteUser endpoint manually: deleted user cannot be retrieved (404 Not Found)
- [X] T060 [US4] Test deleteUser endpoint manually: non-existent user deletion returns 404 Not Found

**Checkpoint**: All user stories should now be independently functional - full CRUD operations complete

---

## Phase 7: Docker Deployment

**Purpose**: Package application in Docker container for portfolio demonstration

- [X] T061 [P] Create Dockerfile in repository root with multi-stage build (maven build + JRE runtime)
- [X] T062 [P] Create docker-compose.yml in repository root for simple container orchestration
- [X] T063 Configure volume mount in docker-compose.yml for H2 database persistence at ./data:/app/data
- [X] T064 Build Docker image with `docker build -t user-api:1.0 .`
- [X] T065 Test Docker container with `docker-compose up` and verify API accessible at http://localhost:8080
- [X] T066 Verify H2 database persists data across container restarts

---

## Phase 8: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories and final validation

- [X] T067 [P] Review all controller methods for consistent HTTP status code usage (201, 200, 204, 404, 409, 400)
- [X] T068 [P] Review all exception messages for clarity and user-friendliness
- [X] T069 [P] Verify all Java classes follow naming conventions: PascalCase for classes, camelCase for methods
- [X] T070 [P] Verify REST endpoints follow kebab-case convention: /api/users
- [X] T071 [P] Add JavaDoc to User entity explaining domain model
- [X] T072 [P] Add JavaDoc to service interface methods explaining business operations
- [X] T073 Review application.yml configuration and add comments for H2 and JPA settings
- [X] T074 Create README.md in repository root with quick start instructions and API usage examples
- [X] T075 [P] Test complete CRUD workflow end-to-end: Create → Read → Update → Delete
- [X] T076 [P] Verify H2 console is accessible at /h2-console for database inspection
- [X] T077 Run quickstart.md validation: follow all curl examples and verify expected responses
- [X] T078 Verify application meets all functional requirements from spec.md (FR-001 through FR-014)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-6)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3 → P4)
- **Docker (Phase 7)**: Depends on at least User Story 1 (MVP) being complete for testing
- **Polish (Phase 8)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Technically independent but benefits from US1 for test data
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - Needs US1 or US2 to have users to update
- **User Story 4 (P4)**: Can start after Foundational (Phase 2) - Needs US1 or US2 to have users to delete

### Within Each User Story

- Service implementation before controller endpoints
- Manual testing tasks run sequentially after implementation complete
- All [P] marked tasks can run in parallel within their phase

### Parallel Opportunities

- **Setup Phase**: T004 (package structure) and T005 (main class) can run in parallel
- **Foundational Phase**: T007-T009 (User entity), T012-T013 (exceptions), T017-T019 (DTOs) can all run in parallel as they are independent files
- **User Story 1**: T029-T032 (manual tests) can be executed in parallel if using multiple test clients
- **User Story 2**: T033-T034 (service methods) can be implemented in parallel; T039-T042 (tests) can run in parallel
- **User Story 3**: Implementation tasks are sequential, but tests (T049-T053) can run in parallel
- **User Story 4**: Tests (T058-T060) can run in parallel
- **Docker Phase**: T061-T062 (Dockerfile and docker-compose) can be created in parallel
- **Polish Phase**: T067-T068, T069-T072, T075-T076 all can run in parallel (different concerns)

---

## Parallel Example: Foundational Phase

```bash
# Launch entity, exceptions, and DTOs together:
# Terminal 1: Create User entity (T007-T009)
# Terminal 2: Create custom exceptions (T012-T013)
# Terminal 3: Create DTO classes (T017-T019)
```

---

## Parallel Example: User Story 1 Testing

```bash
# Launch all manual tests for User Story 1 together:
# Terminal 1: Test valid user creation (T029)
# Terminal 2: Test duplicate email (T030)
# Terminal 3: Test invalid email (T031)
# Terminal 4: Test missing fields (T032)
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1 (Create User)
4. **STOP and VALIDATE**: Test User Story 1 independently with all manual tests
5. Optionally complete Phase 7: Docker for portfolio demonstration
6. Deploy/demo if ready

**MVP Deliverable**: A working Spring Boot REST API that can create users, validate input, enforce uniqueness, and return appropriate responses - fully demonstrable for portfolio.

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 → Test independently → Deploy/Demo (CRUD Read operations)
4. Add User Story 3 → Test independently → Deploy/Demo (CRUD Update)
5. Add User Story 4 → Test independently → Deploy/Demo (Full CRUD complete)
6. Add Docker → Containerized deployment → Portfolio ready
7. Complete Polish → Professional quality

Each story adds value without breaking previous stories.

### Sequential Development Strategy

Recommended order for solo developer:

1. **Week 1**: Setup + Foundational + User Story 1 + Docker → MVP deployed
2. **Week 2**: User Story 2 → Read operations complete
3. **Week 3**: User Story 3 + User Story 4 → Full CRUD
4. **Week 4**: Polish + Documentation → Portfolio ready

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Manual testing is sufficient per constitution (LOW testing priority for learning demo)
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Follow bottom-up implementation order: Entity → Repository → Service → Controller
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence
