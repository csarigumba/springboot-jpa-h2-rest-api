<!--
SYNC IMPACT REPORT
==================
Version change: [NEW] → 1.0.0
Modified principles: N/A (initial constitution)
Added sections: Core Principles (5), Code Quality Standards, Development Workflow, Governance
Removed sections: N/A
Templates status:
  ✅ plan-template.md - Constitution Check section compatible with code quality principles
  ✅ spec-template.md - Requirements structure aligns with quality-focused development
  ✅ tasks-template.md - Task organization supports incremental quality improvements
Follow-up TODOs: None - all placeholders filled for learning demo context
==================
-->

# SpringBoot JPA H2 REST API Constitution

## Core Principles

### I. Readable Code First

Code MUST prioritize human readability over cleverness. All classes, methods, and variables MUST have clear, descriptive names that communicate intent. Complex logic MUST be broken into smaller, named methods that explain the "why" behind implementation decisions. Code serves as documentation for learners and MUST be self-explanatory.

**Rationale**: As a learning demo project, code clarity teaches best practices and enables understanding of Spring Boot patterns without requiring extensive documentation.

### II. Consistent Spring Boot Patterns

All features MUST follow standard Spring Boot conventions: `@RestController` for REST endpoints, `@Service` for business logic, `@Repository` for data access, and `@Entity` for domain models. Dependency injection via constructor injection MUST be used consistently. Standard HTTP status codes and response patterns MUST be applied uniformly.

**Rationale**: Consistency demonstrates professional development practices and makes the codebase predictable for learning Spring Boot architecture.

### III. Clear Layered Architecture

Application MUST maintain strict separation of concerns across layers: Controllers handle HTTP concerns only, Services contain business logic, Repositories manage data persistence, and Entities model the domain. No layer may skip or bypass another (e.g., Controllers must not call Repositories directly).

**Rationale**: Layered architecture teaches fundamental design principles and makes the demo project a strong reference for proper separation of concerns.

### IV. Meaningful Error Handling

All endpoints MUST return appropriate HTTP status codes with clear error messages. Custom exceptions SHOULD be used for domain errors. Global exception handlers via `@ControllerAdvice` MUST provide consistent error response formats. Users must understand what went wrong without inspecting logs.

**Rationale**: Proper error handling demonstrates professional API design and teaches how to build user-friendly REST services.

### V. Documentation Where Code Can't Explain

Complex business logic, non-obvious design decisions, or Spring Boot configuration choices MUST be documented via comments or JavaDoc. Simple, self-explanatory code needs no comments. Each REST endpoint SHOULD have a brief JavaDoc explaining its purpose and parameters.

**Rationale**: Targeted documentation reinforces learning by explaining "why" decisions were made while avoiding comment clutter on obvious code.

## Code Quality Standards

### Formatting & Style

- Code MUST be consistently formatted using standard Java conventions (2 or 4-space indentation consistently applied)
- Classes MUST be organized logically: static fields, instance fields, constructors, public methods, private methods
- Line length SHOULD stay under 120 characters for readability
- Unused imports and variables MUST be removed

### Naming Conventions

- Classes: PascalCase (e.g., `UserController`, `OrderService`)
- Methods & variables: camelCase (e.g., `getUserById`, `orderTotal`)
- Constants: UPPER_SNAKE_CASE (e.g., `MAX_RETRY_ATTEMPTS`)
- REST endpoints: kebab-case (e.g., `/api/users`, `/api/order-items`)

### Code Complexity

- Methods SHOULD not exceed 20-30 lines; refactor if longer
- Cyclomatic complexity SHOULD be kept low; avoid deeply nested conditions
- If logic requires explanation, extract into well-named helper methods

## Development Workflow

### Feature Implementation

1. Understand the requirement and identify affected layers
2. Implement from bottom up: Entity → Repository → Service → Controller
3. Test endpoints manually via REST client or curl as you build
4. Ensure error cases return meaningful messages
5. Review for naming consistency and code clarity before committing

### Code Review Standards

- All code changes SHOULD be reviewed for adherence to Spring Boot patterns
- Verify layer separation is maintained
- Check that error handling is present and meaningful
- Confirm naming is clear and consistent with existing code
- Look for opportunities to simplify or clarify complex logic

### When Complexity is Acceptable

Since this is a demo/learning project:
- Simple, straightforward implementations are preferred over "enterprise patterns"
- Design patterns should only be introduced when they clearly demonstrate value
- Avoid over-engineering; keep solutions appropriate to the scope

## Governance

### Amendment Process

This constitution may be updated as the project scope or learning goals evolve. Amendments require:
1. Clear documentation of what is changing and why
2. Review of impact on existing code structure
3. Update to this file with incremented version number

### Version & Compliance

- All code MUST comply with principles defined in this constitution
- Code reviews MUST verify adherence to layered architecture and Spring Boot patterns
- Violations are acceptable only when clearly justified in comments or commit messages

### Priorities for This Project

Given this is a **demo/learning project**:
- **Code quality and clarity**: HIGH priority (core learning value)
- **Performance optimization**: LOW priority (not necessary for demo scale)
- **Comprehensive testing**: LOW priority (manual testing sufficient for learning)
- **Production readiness**: LOW priority (focus on understanding patterns)

**Version**: 1.0.0 | **Ratified**: 2025-10-30 | **Last Amended**: 2025-10-30
