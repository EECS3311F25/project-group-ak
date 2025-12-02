# SR2.md

##  **Retrospective**

|  Start doing |  Stop doing |  Keep doing |
| ----- | ----- | ----- |
| Follow Model-View-Presenter Software Architecture | Forgetting to add Jira Ticket Name to commit message | Biweekly standups |
| Merge dev-sprint-n into your feature branch before pulling. (When there’s conflicts) | Branching from older dev-sprint-n’s | Good communication |
| Have other people review your pull requests | Forgetting to fetch \+ pull |  |
| Branch off of dev-sprint-n, merge into dev-sprint-n. | Sharing branches to work on. |  |

## **Best/worst experiences**

Klodiana

* Worst: Dealing with Git Conflicts  
* Best: Quickly creating new buttons in TripView

Tony

* Best: Learning HTTPS request and response testing  
* Worst: Installing dependencies, establishing connection to local database, updating data types

Kai

* Best: Learning how to refactor the routes  
* Worst: Redoing the logic several times based on new feedback, so it was a bit confusing and time-consuming.

Andrew

* Best: Learning git  
* Worst: Resolving conflicts

## **New User Stories**

To do:

**Integrate with Backend**

As a traveler, I want to access my trip information in a database so that I can view and manage my trips from any device.

* Trip data is stored persistently in a local database on the device (e.g., SQLite/Room).  
* Trip list and trip details are loaded from the local database (not hardcoded or in-memory).  
* Creating, updating, and deleting a trip correctly modifies the local database and is reflected in the UI.  
* The app handles data access errors gracefully (e.g., shows an error message instead of crashing).