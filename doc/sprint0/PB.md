**EPIC 001 \- ACCOUNT SERVICES**

**1\.	As any traveler, I want to create an account and login with my email, so I can save my trips and access them privately.**

1. Given a valid name, email, and strong password, when I submit, then the account is created and a verification email is sent.  
2. Given the verification link, when I click it within 24 hours, then my account is marked verified and I can log in.  
3. Given a verified account, when I enter the correct email/password, then I’m logged in and redirected to the trip view.  
4. Given an email already in use, when I submit, then I see a clear error and no account is created.  
5. Password rules are displayed inline and enforced (length, complexity).

**2\. 	As a traveler, I want to create an account and log in with OAuth so I can access my trips.**

1. Given I choose OAuth (Google, Apple, etc.), when the provider authenticates me, then I’m logged in or prompted to link to an existing email account if one matches.  
2. The session is created server-side and is secure.  
3. Given the OAuth succeeded, I will see the Home view where I can start using the app.

**3\.	As any traveler I want my session to persist so I don’t have to log in repeatedly.**

1. Given “Remember me” checked, when I return within 30 days, then I’m still logged in.Given no activity for 30 minutes without “Remember me,” then the session expires and I’m sent to the login screen on next action.  
2. Refresh tokens rotate and are invalidated on logout.  
3. Manually logging out invalidates all tokens on the device.

**EPIC 002 \- HOME VIEW**

**4\.	As a traveler, I want to create a trip so I can start planning.**

1. Given I am in the Home View, I can see the add trip button.  
2. Given I tapped the add trip button, I will be prompted to fill out basic information about the trip.  
3. Given I put in valid data, I can create the trip and start adding events.

**5\.	As Priya and Noah, I want to see all my trips in an intuitive way so I don’t waste time.**

1. Given I am logged in, when I open the trip page, then I see all my saved itineraries listed with titles, destinations, and last modified dates.  
2. Given I have many trips, when I use search or sorting, then results update instantly without reloading the page.  
3. Given I have no trips, then I see a clear empty-state message with a “Create Trip” button.  
4. Given I click on a trip, then I am redirected to its trip view.

**6\.	As a traveler, I want to invite my friends on the trip and plan it together.**

1. Given a trip is active, I can access the Trip view.  
2. Given I am in the Trip view, I can see the invite members button.  
3. Given I tapped the invite members button, I can see and copy a link that I can share to my friends.  
4. Given my friends click the link, they will be asked to register / log in and be added to the trip.

**EPIC 003 \- TRIP VIEW**  
**7\. 	As Noah, I want to see all my events in a ‘Trip View’, where I can see an overview of the trip.**

1. Given I am on the Trip tab,  I can see the details of my trip, such as date range, and number of events.  
2. Given I am on the Trip tab, I can see a list of members that are in this trip.

**8\.	As a traveler, I want to browse through the list of events in each day.**

1. Given I am on the Trip tab, I can scroll down to see event cards by day.  
2. Given I have the event cards on the screen, I can swipe left or right to browse through event cards in each day.  
3. Given I taped on an event card, it will take me to the edit event view.

**EPIC 004 \- CALENDAR VIEW**  
**9\. 	As any traveler I want to manage events in my itinerary (create, read, update, delete, and reorder) so my plans stay accurate.**

1. Given valid event details, when I save, then the event appears at the correct chronological position.  
2. Given I open an event, then full details (title, time, location, notes) are displayed.  
3. Given I edit an event and save, then the updates appear instantly for all members.  
4. Given I delete an event, then it is removed from the itinerary after confirmation.  
5. Given I hold-and-drag and reorder events, then the new order is saved and synced for all members.

**10\. 	As any traveler, I want to see my itinerary in a calendar, so I can get a good idea of the timing of my events.**

1. Given a trip is active, I will have three tabs available at the bottom of my screen.  
2. Given I tap on the Calendar Tab button, it will take me to the Calendar View.  
3. Given I am in the Calendar view, I can see a timeline with destinations laid out throughout.

**11\. 	As any traveler, I want travel-time checks during planning that block saves and show an error if there isn’t enough time between events so my schedule stays feasible.**

1. Given an event conflicts with travel time, then the app blocks saving and explains the issue.  
2. Given I adjust event times to be feasible, then the save succeeds.  
3. Given buffer settings exist, then the check accounts for them before approval.  
4. Given I change transport mode, then travel-time recalculates automatically.

**12\.	As Noah, I want to navigate by transit efficiently with timetables taken into account so I catch the right connections.**

1. Given I am on the Calendar tab, I can see the transit icon.  
2. Given I tap on one of the transit icons, I will be redirected to the Transit view.  
3. Given I am in the Transit view, I can see links to external transit apps.  
4. Given I tap on one of the links, it will open the external app and automatically fill in the locations.

**EPIC 005 \- MAP VIEW**  
**13\. 	As any traveler, I want to see my itinerary in a map view, so I can get a good idea of the location of my events.**

1. Given I am in the Map View, I can see location pins with numbers indicating the timely order of the event in a day.  
2. Given I am in the Map View, I can switch into a different day to see the events in that day.  
3. Given I tap on one of the pins, a detail of the event will pop up.

**14\. 	As Alex, I want to see in the map the locations of my events, and see the transit route between any two locations so I can better decide the order of the events.**

1. Given I am in the Map view, I can tap-and-hold on a location pin and it will prompt me to select another location to see the route between.  
2. Given I tap the other location pin, it will show me the details of the route.

**15\. 	As Priya, I want to know how far away an event is from the next.**

1. Given I am on the Calendar tab, I can see the transit icon.  
2. Given I tap on one of the transit icons, I will be redirected to the Transit view.  
3. Given I am in the Transit view, I can see estimated transit times and distances.

**16\.	As Noah, I want to see the location of others on my trip so we can coordinate in real time.**

1. Given members enable location sharing, then I see them on the map updated every few seconds.  
2. Given someone disables sharing, then their icon disappears immediately.  
3. Given I tap a member, then I see their ETA to the next event.

**17\.	As Priya, I want reminders and nudges to leave on time so I keep the group on schedule.**

1. Given travel time and buffer, then a leave-time reminder triggers before departure.  
2. Given I snooze, then the reminder adjusts and recalculates ETA.  
3. Given I ignore the reminder, then a late warning appears with catch-up advice.

**EPIC \- 006 AI ANALYTICS**

**18\.	As Priya, I want an AI summary of what I like so suggestions match my preferences.**

1. Given I’ve made multiple selections, then the AI infers my common interests.  
2. Given I review the summary, then I can edit or reset preferences.  
3. Given I change the summary, then future recommendations update immediately.

**19\. As Priya, I want AI analytics (on-time rate, total travel time, distance, activity vs transit) so I can understand how the trip is going.**

1. Given I open the analytics tab, then charts display key metrics.  
2. Given I hover over a metric, then detailed info or definitions appear.  
3. Given I export data, then a CSV or image matches what I see in the dashboard.

**20\. As Priya, I want the app to remember my preferences (pace, budget, preferred/avoided transport modes, favorite categories, default buffer time) so planning feels personalized.**

1. Given I save preferences, then they persist across sessions and devices.  
2. Given I update preferences, then future routes and recommendations use the new values.  
3. Given I reset defaults, then all personalized settings clear successfully

