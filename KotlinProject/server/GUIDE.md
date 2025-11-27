#   Sending HTTPS Requests and Receiving HTTPS Responses

##  What's happening in the background?
    See the "PostgreSQL DB" section in this file (Docker instructions are there, too). This section will skip all the extra details.

##  DAOs, DTOs, and types
    Here are some scenarios:
    -   Not all fields on a user account should be passed in the frontend (e.g passwords).
    -   ID is assigned by the DB, so it is not known until, for example, a user account is created.
    As such, DTOs (data transfer objects) are used for frontend <-> backend communications, so that 'just enough' fields are used.

    User, Trip, and Event will each have some DTOs (other ops don't need it / Toni's too lazy to implement / it's probably okay not to have):
    -   create / post: __CreateRequest (e.g UserCreateRequest...). This is the ONLY data type accepted for create requests
    -   response: __Response (e.g TripResponse). This is the ONLY data type sent back in HTTPS responses (see below for how to use)
    
##  (!!!) Supported HTTPS request routes
    User (WORKING):   prepend the following with "/user"
    -   GET     /{id}                   -> get user by user ID ("/user")
    -   POST    /register               -> create new user ("/user/register")
    -   PUT     /{id}/password          -> update user password (requires user entry's ID in the parameter)
    -   DELETE  /{id}/delete            -> delete user (requires user entry's ID in the parameter)

    Trip:   /user/{userId}/trip...
    -   GET    /                                -> list all trips associated with the user of ID userId
    -   (WORKING) GET    /{id}                            -> get trip
    -   (WORKING) POST   /                      -> create new trip (belonging to user of ID userId)
    -   (WORKING) PUT    /{id}                            -> update trip
    -   (WORKING) DELETE /{id}                            -> delete trip

    Event:  /user/{userId}/trip/{tripId}/event...
    -   GET    /                                -> list all events associated with the trip of ID tripId
    -   GET    /{id}                            -> get event
    -   POST   /                                -> create event (under trip of ID tripId)
    -   PUT    /{id}                            -> update event
    -   DELETE /{id}                            -> delete event

##  How can HTTPS requests + responses pass data?
    More elaborate examples are under /server/src/test.
    Here is an example on how HTTPS requests / responses can pass their data in the JSON body:
```
application {                       //  call to 'start up' the app, which includes the DB (that's all Toni knows about it)
    module()
}

val client = createClient {         //  create client for HTTPS requests + responses
    install(ContentNegotiation) {   
        json()                      //  can take JSON body
    }
}

val response: HttpResponse = client.post("/user/register") {                //  matching the HTTPS request for creating a user account  
    contentType(ContentType.Application.Json)
    setBody(UserCreateRequest("user1", "user1@gmail.com", "password1"))     //  data that the request will send, type UserCreateRequest
}

assertEquals(HttpStatusCode.Created, response.status)                       //  response.status: HTTPS response's status code (Not Found, Created...)

val responseBody = response.body<UserRetrieveResponse>()                    //  get response's body (message + data). Type UserRetrieveResponse
assertEquals("User registered successfully", responseBody.message)

val responseData = responseBody.data                                        //  get body's data
assertEquals("user1", responseData.userName)
assertEquals("user1@gmail.com", responseData.userEmail)
```


#   PostgreSQL DB (what's happening under the hood)

##  Docker
    The docker-compose.yml is stored under /KotlinProject. It contains instructions to build the Docker image for PostgreSQL.
    I don't know the technical details, but in essence, running the Docker image (in what is called a container) helps any device (not just your own) 'pretend' to have the necessary installations. For now, PostgreSQL is the only external installation needed for backend + server operations.

### (!!!) How to Docker?
    1)  Download Docker from the internet
    2)  Open the Docker app
    3)  cd to /KotlinProject
    4)  '''$    docker-compose up -d'''                                 //  pulls PostgreSQL image, create container + data volume
    5)  '''$    docker ps'''                                            //  should see a navi_postgres container running on port 5432
    6)  '''$    psql -h localhost -p 5432 -U postgres -d navi_db'''     //  psql: run + access the DB. All set now!

## Where do the operations happen?
    /server/src/main/kotlin/org/example/project/db

###  How does the DB connection + setup happen?
    1)  Application.kt configures the DB (connect + migrate), then its routing and serialization


### Local-hosted
    This is a local-hosted PostgreSQL DB.
    Meaning, only your device has this data. That is why PostgreSQL is dockerized (so that any developer can have it readily set up).
    I will upload sample data insertions, so that you can give your local DB sample data to work with.

### Serialization.kt
    The data to be sent in HTTPS requests and responses (e.g creating a User account with xyz field data) are written in JSON format.

###  DB Schema
    To see all data tables' creation queries (users, trips, events), see /server/src/main/resources/db_create-tables.
    This will help with, for example, diagnosing migration failure (more on this later) when changing the schema (e.g adding the correct Location type).

    If you see something like:

'''
CREATE TABLE IF NOT EXISTS users (
id SERIAL PRIMARY KEY,
user_name VARCHAR(50) NOT NULL,
user_email VARCHAR(50) NOT NULL UNIQUE,
user_password VARCHAR(255) NOT NULL
);
'''

    It means:
    -   The table's name in the database is "users" ;
    -   Every entry is automatically assigned an ascending (serial) id, to uniquely identify it (primary key)
    -   It has three fields (not to be used as primary keys): user_name, user_email, user_password

    Here's a sample users table (run psql to access the DB):
$   \dt
List of relations
Schema |         Name          | Type  |  Owner   
--------+-----------------------+-------+----------
public | events                | table | postgres
public | flyway_schema_history | table | postgres
public | trips                 | table | postgres
public | users                 | table | postgres

$   SELECT * FROM users;
id | user_name |   user_email    | user_password
----+-----------+-----------------+---------------
1  | user0     | user0@gmail.com | password0

### (!!!) Migration.kt
    This file tells the app to run certain .sql files to set up the DB tables and data.

    runMigrations(config: DatabaseConfig)
    -   Flyway.configure.locations("classpath:db_create-tables"): find the .sql files in /server/src/main/resources/db_create-tables
    -   Every time you want to change the schema, write a new version Vx__create_tables.sql (in number sequence) to migrate to it
        *   DO NOT MODIFY existing .sql files. Your device 'remembers' existing .sql files by checksum, and will not run if it sees changes

    What if there are too many versions or I don't remember where I've changed existing .sql files by accident?
    1)  Run + access the DB in your terminal: docker exec -it navi_postgres psql -U postgres -d navi_db
    2)  Delete all existing tables in this order: flyway_schema_history -> events -> trips -> users
    3)  Uncomment flyway.repair()
    4)  Re-initialize the DB connection, so that flyway.repair() wipes the local device's checksums, and make new migration history

    There will be changes to the DB schema (e.g Location type changed from String -> Object), so you might find calling .repair() to be better than writing new .sql files.