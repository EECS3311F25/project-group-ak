# Database Setup Guide

This guide explains how to set up and use the database for the Navi travel app backend.

## Architecture Overview

The database layer follows a clean architecture pattern:

```
Routes → Services → Repositories → DAOs → Database
```

### Components

1. **Database.kt** - Establishes PostgreSQL connection using Exposed ORM
2. **DatabaseConfig.kt** - Loads connection settings from environment variables
3. **Migrations.kt** - Runs Flyway migrations to create/update schema
4. **Tables** (`db/tables/`) - Exposed table definitions (UserTable, TripTable, EventTable)
5. **DAOs** (`db/dao/`) - Data Access Objects for database operations
6. **Repositories** (`repository/`) - Business logic abstraction over DAOs
7. **Services** (`service/`) - Validation and business rules
8. **Routes** (`routes/`) - HTTP endpoints that use services

## Prerequisites

1. **PostgreSQL** installed and running
2. **Java/Kotlin** environment set up
3. **Gradle** for dependency management

## Setup Instructions

### 1. Install PostgreSQL

**macOS:**
```bash
brew install postgresql@15
brew services start postgresql@15
```

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
```

**Windows:**
Download and install from [postgresql.org](https://www.postgresql.org/download/windows/)

### 2. Create Database

```bash
# Connect to PostgreSQL
psql postgres

# Create database
CREATE DATABASE navi_db;

# Create user (optional - or use default postgres user)
CREATE USER navi_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE navi_db TO navi_user;

# Exit psql
\q
```

### 3. Configure Environment Variables

Create a `.env` file in the project root or set environment variables:

```bash
# Database connection settings
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=navi_db
export DB_USER=postgres
export DB_PASSWORD=your_password
```

**Default values** (used if environment variables not set):
- Host: `localhost`
- Port: `5432`
- Database: `navi_db`
- User: `postgres`
- Password: `postgres`

### 4. Run the Server

The server will automatically:
1. Load database configuration from environment
2. Run Flyway migrations to create tables
3. Connect Exposed to PostgreSQL
4. Start accepting requests

```bash
cd server
./gradlew run
```

## Database Schema

### Tables

#### user
```sql
id              SERIAL PRIMARY KEY
user_name       VARCHAR(50) NOT NULL
user_email      VARCHAR(50) NOT NULL UNIQUE
user_password   VARCHAR(255) NOT NULL
```

#### trip
```sql
id              SERIAL PRIMARY KEY
trip_title      VARCHAR(100) NOT NULL
trip_location   VARCHAR(255) NOT NULL
trip_start_date VARCHAR(50) NOT NULL
trip_end_date   VARCHAR(50) NOT NULL
```

#### event
```sql
id                  SERIAL PRIMARY KEY
event_title         VARCHAR(100) NOT NULL
event_description   VARCHAR(500)
event_location      VARCHAR(255) NOT NULL
trip_start_date     VARCHAR(50) NOT NULL
trip_end_date       VARCHAR(50) NOT NULL
trip_id             INTEGER NOT NULL (FK to trip.id)
```

## API Endpoints

### User Endpoints

- `GET /users` - Get all users
- `POST /users/register` - Register new user
  ```json
  {
    "userName": "john",
    "userEmail": "john@example.com",
    "userPassword": "password123"
  }
  ```
- `PUT /users/password` - Update password
- `DELETE /users/{username}` - Delete user

### Trip Endpoints

- `GET /trips` - Get all trips
- `GET /trips/{id}` - Get trip by ID
- `POST /trips` - Create new trip
  ```json
  {
    "tripTitle": "Summer Vacation",
    "tripLocation": "Hawaii",
    "tripStartDate": "2025-07-01",
    "tripEndDate": "2025-07-10"
  }
  ```
- `PUT /trips/{id}` - Update trip
- `DELETE /trips/{id}` - Delete trip

### Health Check

- `GET /health` - Server health status

## Development

### Running Migrations

Migrations run automatically on server startup. To manually control:

```kotlin
// Run migrations
Migrations.runMigrations(config)

// Clean and re-run (CAUTION: deletes all data!)
Migrations.cleanAndMigrate(config)
```

### Adding New Tables

1. Create table definition in `db/tables/`:
```kotlin
object NewTable : IntIdTable("new_table") {
    val field = varchar("field", 100)
}
```

2. Create DAO in `db/dao/`:
```kotlin
class NewDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<NewDAO>(NewTable)
    var field by NewTable.field
}
```

3. Create migration SQL in `resources/db/migration/`:
```sql
-- V2__add_new_table.sql
CREATE TABLE new_table (
    id SERIAL PRIMARY KEY,
    field VARCHAR(100)
);
```

4. Create repository, service, and routes as needed

### Testing Database Connection

```bash
# Test connection
psql -h localhost -U postgres -d navi_db

# View tables
\dt

# Query data
SELECT * FROM "user";
SELECT * FROM trip;
SELECT * FROM event;
```

## Troubleshooting

### Connection Refused
- Ensure PostgreSQL is running: `brew services list` (macOS) or `sudo systemctl status postgresql` (Linux)
- Check port 5432 is not blocked by firewall

### Migration Errors
- Check SQL syntax in migration files
- Ensure migration file names follow Flyway convention: `V{version}__{description}.sql`
- Review server logs for detailed error messages

### Permission Errors
- Grant proper privileges: `GRANT ALL PRIVILEGES ON DATABASE navi_db TO your_user;`

### Schema Already Exists
If you need to reset the database:
```sql
DROP DATABASE navi_db;
CREATE DATABASE navi_db;
```

## TODO / Future Improvements

- [ ] Add password hashing (BCrypt)
- [ ] Implement authentication/authorization (JWT)
- [ ] Change date fields from String to proper date types
- [ ] Add foreign key from Trip to User (createdBy)
- [ ] Add connection pooling configuration
- [ ] Add database transaction error handling
- [ ] Create indexes for frequently queried fields
- [ ] Add database seed data for development
- [ ] Implement soft deletes instead of hard deletes
- [ ] Add audit fields (createdAt, updatedAt)

## References

- [Ktor Database Integration](https://ktor.io/docs/server-integrate-database.html)
- [Exposed Documentation](https://github.com/JetBrains/Exposed)
- [Flyway Documentation](https://flywaydb.org/documentation/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

