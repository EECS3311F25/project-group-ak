1. TestDataFactory.kt — Test utilities
    Creates mock Trip, Event, User, and Duration objects
    Simplifies test data setup

2. HomeViewModelTest.kt — HomeView tests
    7 test cases covering:
    Initial loading state
    Trip list display
    Empty state handling
    Error handling
    Trip deletion
    User profile loading
    Reactive state updates
    Documentation at the top explains each test

3. CalendarViewModelTest.kt — CalendarView tests
    5 test cases covering:
    Trip loading
    Date selection and event filtering
    Multi-day events
    Adding events
    Deleting events
    Empty day handling
    Documentation at the top explains each test



Test type : 
    1. Standard Kotlin Unit Testing
    2. Testing bussiness login, not UI components 
    3. Test cases verify ViewModel state and logic, not Compos UI tests that render and interact with UI



What Test Cases really handles :
 1) Does app loads trips from the server
 2) Does it show correct list of trips

 StateManagement :
    1. Are error messages displayed when something fails
    2. Does the UI update when data changes?
    3. Does the loading spinner show/hide at the right times?

User Actions :
    1. When you delete a trip, is it removed from the list?
    2. When you select a date in the calendar, are the correct events shown?
    3. When you add an event, does it appear in the timeline?

Data Correctness :
   1. Is the user's name loaded and displayed correctly?
   2. Are events filtered correctly by date?
   Are multi-day events shown on all relevant days?