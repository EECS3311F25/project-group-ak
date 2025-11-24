#   Routing

##   HIGH PRIORITY: Write dockerization code
    TL;DR: the PostgreSQL server is hosted locally - only the device running has the data.
    A Docker image holds all the relevant details (e.g OS, installations...) for ANY device to 'pretend' like they have everything installed
    Instructions for building the Docker image is stored in a Dockerfile.
    However, for a software with many containers (the environment to run images), instructions will instead be stored on docker-compose.yml

    Right now, Toni is writing docker-compose.yml for the PostgreSQL 'fake' installation for everyone to use

##   HIGH PRIORITY: Communicate on writing sample data into the local DB
    Will the frontend or backend write the entry inserts?
    What should be implemented for efficient dev + testing (e.g util methods for insert, wipe)?

##   HIGH PRIORITY: do something about src/main/kotlin/.../trip/TripModels.kt
    It seems to have a lot of relevant DAOs and DTOs... which Toni has rewritten in his previous pushes.
    Sort out whether everything there is needed, and whether they can be discarded / reorganized.

##   Modify /server to meet frontend needs
    This is especially true for the eventual map view integration

##   Decide on whether Event DTOs and DAOs need User's id for integrity check
    This is a strange valley, please take it slow and map out requirements + functionalities. Every decision is a lot of labor.

##   Event's routes can be buggy, please look into it
    Eh, we can find out later or now?


#   Map view

##   Roughly understand frontend architecture + app flow to write according to their needs
    Don't find out quietly. Ask.


#   Session persistence
    This is of secondary priority. Not now.


#   External OAuth services
    This is of... like... tertiary priority. Definitely not now. No questions will be entertained.