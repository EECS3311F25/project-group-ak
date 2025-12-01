# Database Architecture Documentation

## Complete Data Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT (Compose App)                      │
│                    Makes HTTP Requests                          │
└──────────────────────────────┬──────────────────────────────────┘
                               │ JSON over HTTP
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                        KTOR SERVER                               │
│ ┌─────────────────────────────────────────────────────────────┐ │
│ │  Application.kt - Entry Point                               │ │
│ │  • Calls configureDatabases()                              │ │
│ │  • Calls configureSerialization()                          │ │
│ │  • Calls configureRouting()                                │ │
│ └─────────────────────────────────────────────────────────────┘ │
└───────────────────────────────┬─────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                    DATABASE INITIALIZATION                       │
│ ┌─────────────────────────────────────────────────────────────┐ │
│ │  Database.kt                                                 │ │
│ │  1. DatabaseConfig.fromEnvironment()                        │ │
│ │     → Loads: host, port, dbname, user, password            │ │
│ │  2. Migrations.runMigrations()                             │ │
│ │     → Runs SQL files from resources/db/migration/          │ │
│ │  3. Database.connect()                                     │ │
│ │     → Establishes Exposed connection pool                  │ │
│ └─────────────────────────────────────────────────────────────┘ │
└───────────────────────────────┬─────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                      HTTP REQUEST FLOW                           │
│                                                                  │
│  1. ROUTES LAYER (routes/UserRoutes.kt, routes/TripRoutes.kt)  │
│     • Receives HTTP requests                                    │
│     • Parses JSON → DTOs                                        │
│     • Calls service layer                                       │
│     • Returns HTTP responses with status codes                  │
│                                                                  │
│  2. SERVICE LAYER (service/UserService.kt, service/TripService.kt) │
│     • Input validation                                          │
│     • Business rule enforcement                                 │
│     • Calls repository                                          │
│     • Returns Result<T> (success/failure)                       │
│                                                                  │
│  3. REPOSITORY LAYER (repository/TripRepositoryImpl.kt, etc)   │
│     • Abstraction over database                                 │
│     • Wraps operations in suspendTransaction                    │
│     • Calls DAOs                                                │
│     • Maps DAOs → Domain Models                                 │
│                                                                  │
│  4. DAO LAYER (db/dao/TripDAO.kt, db/dao/EventDAO.kt)          │
│     • Exposed DAO operations                                    │
│     • CRUD on table rows                                        │
│     • Returns DAO entities                                      │
│                                                                  │
│  5. TABLE LAYER (db/tables/TripTable.kt, db/mapping.kt)        │
│     • Exposed table definitions                                 │
│     • Column definitions                                        │
│     • Foreign keys, indexes                                     │
│                                                                  │
└───────────────────────────────┬─────────────────────────────────┘
                                │
                                ▼
                        ┌───────────────┐
                        │  PostgreSQL   │
                        │   Database    │
                        └───────────────┘
```

## Layer Responsibilities

### 1. Routes Layer
**Files:** `routes/UserRoutes.kt`, `routes/TripRoutes.kt`

**Responsibilities:**
- Define HTTP endpoints
- Parse request body (JSON → DTOs)
- Call service methods
- Handle HTTP status codes
- Serialize responses (DTOs → JSON)

**Example:**
```kotlin
post("/trips") {
    val request = call.receive<CreateTripRequest>()
    tripService.createTrip(...)
        .onSuccess { call.respond(HttpStatusCode.Created, ...) }
        .onFailure { call.respond(HttpStatusCode.BadRequest, ...) }
}
```

### 2. Service Layer
**Files:** `service/UserService.kt`, `service/TripService.kt`

**Responsibilities:**
- Input validation (email format, password strength, etc.)
- Business rules (trip dates, user permissions)
- Coordinate between multiple repositories if needed
- Return `Result<T>` for success/failure handling

**Example:**
```kotlin
suspend fun createTrip(trip: Trip): Result<Trip> {
    if (trip.tripTitle.isBlank()) {
        return Result.failure(IllegalArgumentException("..."))
    }
    return try {
        val created = tripRepository.addTrip(trip)
        Result.success(created)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

### 3. Repository Layer
**Files:** `repository/TripRepository.kt` (interface), `repository/TripRepositoryImpl.kt` (implementation)

**Responsibilities:**
- Abstract database operations
- Provide clean API for services
- Wrap operations in `suspendTransaction`
- Map between DAO entities and domain models

**Example:**
```kotlin
override suspend fun allTrips(): List<Trip> = suspendTransaction {
    TripDAO.all().map(::daoToTripModel)
}
```

### 4. DAO Layer
**Files:** `db/dao/TripDAO.kt`, `db/dao/EventDAO.kt`, `db/mapping.kt`

**Responsibilities:**
- Exposed entity classes
- Define how to read/write table rows
- Provide mapping to domain models

**Example:**
```kotlin
class TripDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TripDAO>(TripTable)
    var tripTitle by TripTable.tripTitle
    var tripLocation by TripTable.tripLocation
    // ...
}
```

### 5. Table Layer
**Files:** `db/tables/TripTable.kt`, `db/tables/EventTable.kt`, `db/mapping.kt`

**Responsibilities:**
- Define table schema in code
- Column types, constraints
- Foreign keys
- Indexes

**Example:**
```kotlin
object TripTable : IntIdTable("trip") {
    val tripTitle = varchar("trip_title", 100)
    val tripLocation = varchar("trip_location", 255)
    // ...
}
```

## Data Transformation Flow

```
HTTP Request
    ↓
  JSON String
    ↓
[Routes] Parse with Kotlinx Serialization
    ↓
  DTO (CreateTripRequest)
    ↓
[Routes] Convert to Domain Model
    ↓
  Domain Model (Trip from shared/)
    ↓
[Service] Validate & Apply Rules
    ↓
  Domain Model (validated)
    ↓
[Repository] suspendTransaction
    ↓
[DAO] Create entity & write to DB
    ↓
  DAO Entity (TripDAO)
    ↓
[Repository] daoToTripModel()
    ↓
  Domain Model (Trip)
    ↓
[Service] Wrap in Result<Trip>
    ↓
[Routes] Convert to Response DTO
    ↓
  DTO (TripResponse)
    ↓
[Routes] Serialize to JSON
    ↓
  JSON String
    ↓
HTTP Response
```

## Key Design Patterns

### 1. Repository Pattern
**Why:** Abstracts data access, makes testing easier, decouples business logic from database

**Implementation:**
- Interface defines contract (`TripRepository`)
- Implementation uses Exposed (`TripRepositoryImpl`)
- Can swap implementations (e.g., in-memory for tests)

### 2. Service Layer Pattern
**Why:** Centralizes business logic, validation, and orchestration

**Benefits:**
- Reusable across different routes
- Testable without HTTP layer
- Single place for business rules

### 3. DTO Pattern
**Why:** Separate API contracts from domain models

**Benefits:**
- API can change without affecting domain
- Control what fields are exposed (e.g., hide passwords)
- Validation at API boundary

### 4. DAO Pattern (via Exposed)
**Why:** Object-relational mapping

**Benefits:**
- Type-safe database queries
- Kotlin syntax instead of SQL
- Automatic connection management

## Transaction Management

### suspendTransaction Helper

```kotlin
suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    withContext(Dispatchers.IO) {
        suspendTransaction(statement = block)
    }
```

**What it does:**
1. Switches to IO dispatcher (non-blocking)
2. Opens database transaction
3. Executes block
4. Commits on success
5. Rolls back on exception
6. Returns result

**Used in every repository method:**
```kotlin
override suspend fun addTrip(trip: Trip): Trip = suspendTransaction {
    // This entire block runs in a single DB transaction
    val newTrip = TripDAO.new { ... }
    daoToTripModel(newTrip)
}
```

## Configuration System

```
Environment Variables (or defaults)
    ↓
DatabaseConfig.fromEnvironment()
    ↓
DatabaseConfig data class
    ├── jdbcUrl
    ├── username
    ├── password
    ├── driver
    └── ...
    ↓
Used by:
├── Migrations.runMigrations(config)
└── Database.connect(config.jdbcUrl, ...)
```

## Migration System (Flyway)

```
Server Startup
    ↓
Migrations.runMigrations(config)
    ↓
Flyway scans resources/db/migration/
    ↓
Finds: V1__create_tables.sql
    ↓
Checks if already applied (flyway_schema_history table)
    ↓
If not applied: Execute SQL
    ↓
Record in flyway_schema_history
    ↓
Database schema ready
```

**Migration file naming:**
- `V1__create_tables.sql` ✅
- `V2__add_user_roles.sql` ✅
- `V3__add_indexes.sql` ✅
- Pattern: `V{version}__{description}.sql`

## Error Handling Strategy

### Service Layer Returns Result<T>

```kotlin
// Success case
Result.success(trip)

// Failure case
Result.failure(IllegalArgumentException("Invalid input"))
```

### Routes Handle Results

```kotlin
tripService.createTrip(trip)
    .onSuccess { trip ->
        call.respond(HttpStatusCode.Created, trip)
    }
    .onFailure { error ->
        when (error) {
            is IllegalArgumentException -> 
                call.respond(HttpStatusCode.BadRequest, ...)
            is NoSuchElementException -> 
                call.respond(HttpStatusCode.NotFound, ...)
            else -> 
                call.respond(HttpStatusCode.InternalServerError, ...)
        }
    }
```

## Security Considerations

### Current State ⚠️
- Passwords stored in **plain text**
- No authentication/authorization
- No rate limiting
- No input sanitization

### Future Implementation ✅
- [ ] BCrypt password hashing
- [ ] JWT authentication
- [ ] Role-based access control
- [ ] Input validation/sanitization
- [ ] Rate limiting
- [ ] Audit logging

## Performance Optimizations

1. **Connection Pooling** - Exposed manages automatically
2. **Async Operations** - All DB calls are suspend functions
3. **Indexes** - Created on frequently queried columns
4. **Transactions** - Batch operations for consistency
5. **Lazy Loading** - Exposed loads data only when needed

## Testing Strategy

### Unit Tests
- Service layer: Mock repositories
- Repository layer: Use test database

### Integration Tests
- Full stack: Routes → Services → Repositories → Real DB
- Use test containers for PostgreSQL

### Example Test Structure
```kotlin
class TripServiceTest {
    private val mockRepository = mockk<TripRepository>()
    private val service = TripService(mockRepository)
    
    @Test
    fun `should validate trip title`() {
        val result = service.createTrip(Trip(..., tripTitle = ""))
        assertTrue(result.isFailure)
    }
}
```

## Common Operations

### Add New Endpoint
1. Create DTO in `dto/`
2. Add method to service in `service/`
3. Add route in `routes/`
4. Test with curl/Postman

### Add New Table
1. Create `V{n}__add_table.sql` migration
2. Create `SomeTable.kt` in `db/tables/`
3. Create `SomeDAO.kt` in `db/dao/`
4. Create repository interface/implementation
5. Create service
6. Create routes

### Modify Existing Table
1. Create `V{n}__alter_table.sql` migration
2. Update table definition in code
3. Update DAO
4. Update mappers
5. Test thoroughly

## Debugging Tips

### View SQL Queries
Add to `Database.kt`:
```kotlin
addLogger(StdOutSqlLogger)
```

### Check Migration Status
```sql
SELECT * FROM flyway_schema_history;
```

### Inspect Database
```bash
psql -d navi_db
\dt              # List tables
\d trip          # Describe trip table
SELECT * FROM trip;
```

### Server Logs
Look for:
- "Database connected successfully"
- "Database migrations completed successfully"
- SQL queries (if logging enabled)
- Error stack traces

## References

- [Ktor Documentation](https://ktor.io/docs/)
- [Exposed Documentation](https://github.com/JetBrains/Exposed)
- [Flyway Documentation](https://flywaydb.org/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)

