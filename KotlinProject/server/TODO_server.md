##  PRIORITY #1 (please finish ASAP): write the Location table, add locationId as a foreign key in Event
    locations will be stored as a table in the DB. Suggestion: location ID is a foreign key stored in events. CRUD ops (maybe not delete) can, for example, work as follow:
    -   Get event's location: get location ID from the events table, then call some kinda getLocation(locationId) that queries the locations table
    -   Update event's location: get location ID from the events table, then call some kinda updateLocation(locationId) that queries the locations table

    Remarks:
    -   The frontend doesn't really make direct CRUD ops to the locations table. It happens as a result of the frontend interacting with events
    -   locations is in a 1:1 relation with events. A location belongs to an event, and an event has one location.

events
id  |   name    |   description |   ... |   locationId

locations
id  |   name    |   description |   ...

    TODO:
    -   location ID is a foreign key in event (some 'location' column in event is type int, refer to Location type)
    -   location DAO and DTO will probably be needed
    -   steps beyond that, will revise design

    Design to think over: CRUD ops on location 


    *   WARNING: track module dependency carefully, and please fix from bottom up strategically. Consider writing it out. Every small bit can result in a lot more labor.

##  PRIORITY #2: add image URL field into Trip


##  PRIORITY #3 (please finish ASAP): fixing HTTPS requests routes
    See GUIDE.md for routes that are currently working correctly. Those will have relatively correct code, to help you derive how to fix the other ones.
    *CONSIDER* writing tests and run them to check correctness. Only CreateUser and CreateTrip are working.
    *   Testing is optional (and maybe just do it after a lot of fixing) - it is a lot of labor

##  PRIORITY #3: Writing DB entry insertions
    What should be implemented for efficient dev + testing (e.g util methods for insert, wipe)?

##  Extra steps: modify /server to meet frontend needs
    Potential tasks:
    *   Implement additional fields and methods for Trip, Event (e.g Location type with latitude, longitude...)
    *   Implement owner User and collaborator Users for a particular Trip

##  Extra steps: write documentations








##  Session persistence
    This is of secondary priority. Not now.

##  External OAuth services
    This is of... like... tertiary priority. Definitely not now. No questions will be entertained.