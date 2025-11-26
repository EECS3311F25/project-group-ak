##  PRIORITY #1 (please finish ASAP): change Event and Trip locations from type String -> Location
    Example modifying steps (by dependency): Trip -> Trip DAO + TripDTOs -> TripRepo -> PostgresTripRepo + TripService -> TripRoutes (see below) -> tests
    *   NOTE: track module dependency carefully, and please fix from bottom up strategically. Consider writing it out
    Warning: SQL doesn't have custom types. Refer to how the Duration type is handled from the backend to the DB, to derive potential steps (consider Google + LLM)

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