# Quick Start Guide

## 🚀 Get Started in 5 Minutes

### Step 1: Install PostgreSQL (if not already installed)

**macOS:**
```bash
brew install postgresql@15
brew services start postgresql@15
```

**Linux:**
```bash
sudo apt install postgresql
sudo systemctl start postgresql
```

### Step 2: Create Database

```bash
# Method 1: Simple (use default postgres user)
createdb navi_db

# Method 2: Custom user
psql postgres
CREATE DATABASE navi_db;
CREATE USER navi_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE navi_db TO navi_user;
\q
```

### Step 3: Set Environment Variables (Optional)

If using default settings (localhost, postgres user, postgres password), skip this step.

Otherwise:
```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=navi_db
export DB_USER=postgres
export DB_PASSWORD=your_password
```

### Step 4: Run the Server

```bash
cd server
./gradlew run
```

✅ That's it! The server will:
- Automatically run migrations
- Create all tables
- Start accepting requests

### Step 5: Test It

```bash
# Health check
curl http://localhost:8080/health

# Create a trip
curl -X POST http://localhost:8080/trips \
  -H "Content-Type: application/json" \
  -d '{
    "tripTitle": "My First Trip",
    "tripLocation": "Paris",
    "tripStartDate": "2025-08-01",
    "tripEndDate": "2025-08-10"
  }'

# Get all trips
curl http://localhost:8080/trips
```

## 📋 Available Endpoints

### Users
- `GET /users` - List all
- `POST /users/register` - Create
- `PUT /users/password` - Update password
- `DELETE /users/{username}` - Delete

### Trips
- `GET /trips` - List all
- `GET /trips/{id}` - Get one
- `POST /trips` - Create
- `PUT /trips/{id}` - Update
- `DELETE /trips/{id}` - Delete

### System
- `GET /health` - Health check

## 🗂️ Project Structure

```
server/src/main/kotlin/org/example/project/
├── config/
│   └── DatabaseConfig.kt          # DB settings
├── db/
│   ├── tables/                    # Table definitions
│   │   ├── TripTable.kt
│   │   └── EventTable.kt
│   ├── dao/                       # Data access
│   │   ├── TripDAO.kt
│   │   └── EventDAO.kt
│   ├── mapping.kt                 # User DAO
│   └── Migrations.kt              # Migration runner
├── repository/                    # Data layer
│   ├── TripRepository.kt
│   └── TripRepositoryImpl.kt
├── service/                       # Business logic
│   ├── TripService.kt
│   └── UserService.kt
├── routes/                        # HTTP endpoints
│   ├── UserRoutes.kt
│   └── TripRoutes.kt
├── dto/                          # API models
│   ├── UserDto.kt
│   └── TripDto.kt
├── Database.kt                   # DB connection
├── Routing.kt                    # Route setup
└── Application.kt                # Main entry point

server/src/main/resources/
└── db/migration/
    └── V1__create_tables.sql     # Initial schema
```

## 🔍 How It Works

1. **Application.kt** starts the server
2. **Database.kt** connects to PostgreSQL
3. **Migrations.kt** creates/updates tables
4. **Routing.kt** sets up all endpoints
5. **Routes** receive HTTP requests
6. **Services** validate and apply business rules
7. **Repositories** perform database operations
8. **DAOs** map between database and domain models

## 🆘 Troubleshooting

### "Connection refused"
PostgreSQL isn't running:
```bash
# macOS
brew services start postgresql@15

# Linux
sudo systemctl start postgresql
```

### "Database does not exist"
Create it:
```bash
createdb navi_db
```

### "Password authentication failed"
Check your password or use environment variables:
```bash
export DB_PASSWORD=your_actual_password
```

### See migration errors
Check server logs - they'll show SQL errors if migrations fail.

## 📚 Learn More

- **DATABASE_SETUP.md** - Detailed setup guide
- **IMPLEMENTATION_SUMMARY.md** - What was implemented
- Code comments explain each component

## 💡 Tips

1. Use Postman or Insomnia for easier API testing
2. Check PostgreSQL with: `psql -d navi_db`
3. View tables: `\dt` in psql
4. Query data: `SELECT * FROM trip;`
5. Server logs show all database operations

## Next Steps

1. ✅ Backend database complete
2. 🔄 Add HTTP client to Compose frontend
3. 🔄 Connect UI to real API endpoints
4. 🔄 Implement authentication
5. 🔄 Add password hashing

