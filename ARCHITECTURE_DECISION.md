# Architecture Decision Documentation

## Overview

This document explains the architectural decisions made for the University ERP System. The system has been refactored to follow a **layered architecture pattern** as specified in the project requirements.

## Current Architecture

```
UI → API → Service → Data
```

The system uses a **four-layer architecture**:

- **UI Layer** (`edu.univ.erp.ui`): Swing panels and windows - handles user interaction and presentation
- **API Layer** (`edu.univ.erp.api`): Facade for UI - handles request/response mapping and validation
- **Service Layer** (`edu.univ.erp.service`): Business logic - orchestrates repository calls and enforces business rules
- **Data Layer** (`edu.univ.erp.data`): Database access - repositories and database configuration

## Architecture Layers

### 1. UI Layer (`edu.univ.erp.ui`)
- **Purpose**: User interface components (Swing panels)
- **Responsibilities**:
  - Display data to users
  - Capture user input
  - Handle UI events
  - Call API layer methods
- **Key Components**: 22+ UI panels for students, instructors, and admins

### 2. API Layer (`edu.univ.erp.api`)
- **Purpose**: Facade between UI and business logic
- **Responsibilities**:
  - Provide clean interface for UI
  - Map between UI objects and domain models
  - Handle API-level validation
  - Return standardized `ApiResponse` objects
- **Packages**:
  - `api.admin`: Admin operations (users, courses, sections, maintenance, backup)
  - `api.student`: Student operations (catalog, registration, grades, transcript)
  - `api.instructor`: Instructor operations (sections, grade entry, stats, export)
  - `api.catalog`: Course catalog operations
  - `api.auth`: Authentication operations (password change)
  - `api.common`: Shared API utilities (`ApiResponse`)

### 3. Service Layer (`edu.univ.erp.service`)
- **Purpose**: Business logic and orchestration
- **Responsibilities**:
  - Implement business rules
  - Coordinate multiple repository calls
  - Handle transactions (where needed)
  - Validate business constraints
- **Packages**:
  - `service.admin`: Admin business logic
  - `service.student`: Student business logic
  - `service.instructor`: Instructor business logic
  - `service.catalog`: Catalog business logic
  - `service.auth`: Authentication business logic

### 4. Data Layer (`edu.univ.erp.data`)
- **Purpose**: Database access and persistence
- **Responsibilities**:
  - Execute SQL queries
  - Map database results to domain models
  - Manage database connections
  - Handle data access errors
- **Components**:
  - `DatabaseConfig`: Connection pooling (HikariCP) for auth and main databases
  - `repository.*`: Repository classes for each entity (User, Student, Course, Section, Enrollment, Grade, etc.)

### 5. Domain Layer (`edu.univ.erp.domain`)
- **Purpose**: Domain models (DTOs)
- **Responsibilities**:
  - Represent business entities
  - Transfer data between layers
  - Provide domain-specific behavior
- **Key Models**: `UserAccount`, `StudentProfile`, `InstructorProfile`, `CourseDetail`, `SectionDetail`, `GradeComponent`, `TranscriptRow`, etc.

## Design Rationale

### Why This Architecture Was Chosen

1. **Project Requirements**
   - The project specification explicitly requires a `UI → API → Service → Data` architecture
   - This pattern provides clear separation of concerns

2. **Maintainability**
   - Each layer has a single, well-defined responsibility
   - Changes in one layer don't cascade to others
   - Easy to locate and fix bugs

3. **Testability**
   - Each layer can be tested independently
   - Service layer can be tested with mock repositories
   - API layer can be tested with mock services

4. **Scalability**
   - Easy to add new features by extending existing layers
   - Business logic changes don't affect UI
   - Database changes are isolated to data layer

5. **Code Reusability**
   - Services can be reused across different APIs
   - Repositories provide consistent data access patterns
   - Domain models are shared across layers

## Layer Communication

### Data Flow Example: Student Registration

1. **UI Layer** (`StudentRegisterPanel`):
   - User clicks "Register" button
   - Calls `StudentApi.registerSection(studentId, sectionCode)`

2. **API Layer** (`StudentApi`):
   - Validates input
   - Checks maintenance mode
   - Calls `EnrollmentService.registerStudent(studentId, sectionCode)`
   - Returns `ApiResponse` to UI

3. **Service Layer** (`EnrollmentService`):
   - Checks if student already enrolled
   - Checks section capacity
   - Calls `EnrollmentRepository.enrollStudent(studentId, sectionId)`
   - Handles business logic exceptions

4. **Data Layer** (`EnrollmentRepository`):
   - Executes SQL: `INSERT INTO enrollments ...`
   - Returns success/failure

## Package Structure

```
edu.univ.erp/
├── ui/                    # UI Layer (22+ panels)
│   ├── Student*           # Student UI panels
│   ├── Instructor*        # Instructor UI panels
│   ├── Admin*             # Admin UI panels
│   └── Common             # Shared UI components
├── api/                   # API Layer
│   ├── admin/             # Admin APIs
│   ├── student/           # Student APIs
│   ├── instructor/        # Instructor APIs
│   ├── catalog/           # Catalog APIs
│   ├── auth/              # Auth APIs
│   └── common/            # Shared API utilities
├── service/               # Service Layer
│   ├── admin/             # Admin services
│   ├── student/           # Student services
│   ├── instructor/        # Instructor services
│   ├── catalog/           # Catalog services
│   └── auth/              # Auth services
├── data/                  # Data Layer
│   ├── DatabaseConfig     # Connection pooling
│   └── repository/        # Repository classes
├── domain/                # Domain Models (DTOs)
│   ├── UserAccount
│   ├── StudentProfile
│   ├── CourseDetail
│   └── ... (18+ models)
├── auth/                  # Authentication
│   └── AuthService        # Login authentication
├── util/                  # Utilities
│   └── MaintenanceManager # Maintenance mode checker
└── App.java              # Entry point
```

## Database Design

**Two-Database Architecture:**
- `erp_auth`: User credentials only (bcrypt password hashes, account lockout)
- `erp_main`: All application data (students, courses, sections, enrollments, grades)

**Connection Pooling:**
- HikariCP for efficient connection management
- Separate connection pools for auth and main databases
- Configured in `DatabaseConfig`

## Key Features

✅ **Layered Architecture**: Clear separation between UI, API, Service, and Data layers  
✅ **Domain Models**: DTOs for all business entities  
✅ **Repository Pattern**: Consistent data access across all entities  
✅ **Service Layer**: Business logic separated from data access  
✅ **API Facade**: Clean interface for UI layer  
✅ **Error Handling**: Standardized `ApiResponse` for consistent error handling  
✅ **Security**: Two-database architecture, bcrypt password hashing, account lockout  
✅ **Maintenance Mode**: System-wide maintenance toggle  
✅ **Backup/Restore**: Database backup and restore functionality  

## Benefits of This Architecture

1. **Separation of Concerns**: Each layer has a single responsibility
2. **Maintainability**: Easy to locate and modify code
3. **Testability**: Layers can be tested independently
4. **Scalability**: Easy to add new features
5. **Code Reusability**: Services and repositories can be reused
6. **Type Safety**: Strong typing with domain models
7. **Error Handling**: Consistent error responses via `ApiResponse`

## Migration Notes

The system was refactored from a direct UI-to-database architecture to the current layered architecture. All UI panels now interact exclusively with the API layer, and no direct JDBC calls remain in the UI layer.

## Conclusion

This layered architecture successfully implements all required features while maintaining:
- Clear separation of concerns
- Easy maintainability
- Strong testability
- Scalable design
- Code reusability

The architecture follows industry best practices and provides a solid foundation for future enhancements.

---

**Document Version**: 2.0  
**Last Updated**: After architectural refactoring  
**Architecture Status**: Implemented and validated
