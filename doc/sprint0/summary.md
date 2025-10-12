# **Summary.md**

## Project Objective

The project is a trip planner for group travels. The main functionalities of the app include a calendar and a map view:

* Calendar view  
  * A trip would have a timeline view of its itinerary. This uses timeline cards (e.g Figma) to interact with itinerary events.  
  * Interaction with timeline cards updates in real-time.  
- Timeline cards functions can include: moving them to different times, add notes  
* Map view  
  * For each trip, the map view provides a map visualization of the itinerary from the calendar view  
  * Map functions can include: plotting destinations; calculate distance, time, and plotting them onto the timeline in the calendar view

The user must create their own user account in order to create, edit, and save trips; as well as adding other users as collaborating members (i.e editors and viewers).

* The app should have web login functionalities (e.g username, password, OAuth, password recovery…)  
* Trips can be saved on a user account

## Key Personas

**Norah** \- a 24-year-old junior recruiter at a tech startup. She shares a modern apartment with 2 roommates in the lively part of the city, close to the new restaurants and bars. She is responsible for sourcing candidates for engineering roles, a job she finds fast-paced and rewarding.  

Norah is very social and maintains a tight-knit college friend group, whom she meets almost every week for brunch, workout, or a night out.  

As an avid outdoor traveler, her Instagram feed is filled with pictures from weekend hiking trips and the two major international vacations she budgets for each year. She uses a budgeting app to manage her student loans and living expenses, and her phone is her primary tool for everything from banking and investing to coordinating group trips with friends.  

Norah is a digital native who expects instant, seamless service.

**Alex** \- a 27-years-old marketing specialist at a travel agency in downtown Toronto. He lives alone in a small condo close to public transit for easy access to Toronto’s nightlife and cultural events. He spends his workdays creating engaging campaigns to promote travel destinations, while his free time is dedicated to planning his next solo backpacking trip.  

He is an avid photographer who documents his journeys through both DSLR shots and Instagram stories. He enjoys spontaneity but is also very budget-conscious, constantly hunting for cheap flights and accommodation. He subscribes to multiple deal-alert newsletters and follows travel vloggers for hidden gem recommendations.  

Alex values independence and flexibility in his travels. He prefers customizable itineraries over packaged tours and wants apps that allow him to compare options quickly and book in a few taps. He often faces challenges balancing affordability with quality, and worries about booking scams.  

Alex is a tech-savvy user who expects transparency and real-time updates. His ideal travel app provides reliable reviews, visual inspiration, and a community of like-minded travelers.

## Key Scenarios

**Norah** and her college friend group have been compiling a list of travel destinations for their international travel tour in Toronto. Near the time of their trip, the group wants to see which parts of the city they would be visiting, and collaboratively plan out the trip in the upcoming days.

**Alex** is planning his solo bike trip to Elora, a small town 100 km away from downtown Toronto where he lives. Wary of the empty rural stretches along the way, Alex wants to plan out stops on his route for rests and gear checks, and save his schedule for later editing.

## Key Principles (for the current iteration)

* Prioritize **functionality**: the calendar and map views’ should be able to perform their key functionalities in the application  
* Prioritize **fault tolerance**: the application should be able to handle unexpected user input safely  
* Prioritize **ease of use**: the interface should be intuitive and accessible for the user

