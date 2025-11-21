package org.example.project

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.mockApiRoutes() {
    
    get("/mocktrips") {
        val jsonResponse = """
[
  {
    "id": "trip_summer_getaway",
    "title": "Summer Getaway",
    "description": "Road trip across Ontario",
    "location": "Toronto to Ottawa",
    "duration": {
      "startDate": "2025-07-01",
      "startTime": "09:00",
      "endDate": "2025-07-10",
      "endTime": "17:00"
    },
    "users": [
      {"name": "Klodiana"},
      {"name": "Alex"}
    ],
    "events": [
      {
        "id": "1",
        "title": "Departure from Toronto",
        "duration": {
          "startDate": "2025-07-01",
          "startTime": "09:00",
          "endDate": "2025-07-01",
          "endTime": "10:00"
        },
        "description": "Start our journey from downtown Toronto",
        "location": "Toronto, ON",
        "imageUrl": null
      },
      {
        "id": "2",
        "title": "Niagara Falls Stop",
        "duration": {
          "startDate": "2025-07-01",
          "startTime": "12:00",
          "endDate": "2025-07-01",
          "endTime": "16:00"
        },
        "description": "Visit the famous Niagara Falls and take photos",
        "location": "Niagara Falls, ON",
        "imageUrl": null
      },
      {
        "id": "3",
        "title": "Niagara Boat Tour",
        "duration": {
          "startDate": "2025-07-02",
          "startTime": "10:00",
          "endDate": "2025-07-02",
          "endTime": "11:30"
        },
        "description": "Experience the falls up close on the Maid of the Mist",
        "location": "Niagara Falls, ON",
        "imageUrl": null
      },
      {
        "id": "4",
        "title": "Table Rock Lunch",
        "duration": {
          "startDate": "2025-07-02",
          "startTime": "12:30",
          "endDate": "2025-07-02",
          "endTime": "14:00"
        },
        "description": "Traditional Canadian lunch with a view",
        "location": "Table Rock, Niagara Falls",
        "imageUrl": null
      },
      {
        "id": "5",
        "title": "Drive to Kingston",
        "duration": {
          "startDate": "2025-07-03",
          "startTime": "08:00",
          "endDate": "2025-07-03",
          "endTime": "12:00"
        },
        "description": "Scenic drive along Lake Ontario",
        "location": "Highway 401",
        "imageUrl": null
      },
      {
        "id": "6",
        "title": "Kingston Market Square",
        "duration": {
          "startDate": "2025-07-03",
          "startTime": "14:00",
          "endDate": "2025-07-03",
          "endTime": "17:00"
        },
        "description": "Browse local crafts and food vendors",
        "location": "Kingston, ON",
        "imageUrl": null
      },
      {
        "id": "7",
        "title": "Ottawa Parliament Tour",
        "duration": {
          "startDate": "2025-07-04",
          "startTime": "10:00",
          "endDate": "2025-07-04",
          "endTime": "12:00"
        },
        "description": "Guided tour of Canada's Parliament buildings",
        "location": "Parliament Hill, Ottawa",
        "imageUrl": null
      },
      {
        "id": "8",
        "title": "ByWard Market Dinner",
        "duration": {
          "startDate": "2025-07-04",
          "startTime": "18:00",
          "endDate": "2025-07-04",
          "endTime": "20:00"
        },
        "description": "Farewell dinner at famous ByWard Market",
        "location": "ByWard Market, Ottawa",
        "imageUrl": null
      }
    ],
    "imageHeaderUrl": "https://images.pexels.com/photos/1285625/pexels-photo-1285625.jpeg",
    "createdDate": "2025-06-01"
  },
  {
    "id": "trip_european_adventure",
    "title": "European Adventure",
    "description": "Backpacking through Europe",
    "location": "Paris to Rome",
    "duration": {
      "startDate": "2025-08-15",
      "startTime": "10:00",
      "endDate": "2025-08-30",
      "endTime": "18:00"
    },
    "users": [
      {"name": "Alice"},
      {"name": "Bob"}
    ],
    "events": [
      {
        "id": "9",
        "title": "Eiffel Tower Visit",
        "duration": {
          "startDate": "2025-08-15",
          "startTime": "14:00",
          "endDate": "2025-08-15",
          "endTime": "17:00"
        },
        "description": "Climb the iconic Eiffel Tower and enjoy panoramic views",
        "location": "Paris, France",
        "imageUrl": null
      },
      {
        "id": "10",
        "title": "Louvre Museum",
        "duration": {
          "startDate": "2025-08-16",
          "startTime": "10:00",
          "endDate": "2025-08-16",
          "endTime": "15:00"
        },
        "description": "See the Mona Lisa and other masterpieces",
        "location": "Paris, France",
        "imageUrl": null
      },
      {
        "id": "11",
        "title": "Coffee & Pastries",
        "duration": {
          "startDate": "2025-08-16",
          "startTime": "15:15",
          "endDate": "2025-08-16",
          "endTime": "16:00"
        },
        "description": "Relax with coffee and fresh pastries",
        "location": "Paris, France",
        "imageUrl": null
      },
      {
        "id": "12",
        "title": "Seine River Walk",
        "duration": {
          "startDate": "2025-08-16",
          "startTime": "16:30",
          "endDate": "2025-08-16",
          "endTime": "17:30"
        },
        "description": "Leisurely walk along the Seine",
        "location": "Paris, France",
        "imageUrl": null
      },
      {
        "id": "13",
        "title": "Dinner at Le Jules",
        "duration": {
          "startDate": "2025-08-16",
          "startTime": "18:00",
          "endDate": "2025-08-16",
          "endTime": "19:00"
        },
        "description": "Sit-down dinner with local cuisine",
        "location": "Paris, France",
        "imageUrl": null
      },
      {
        "id": "14",
        "title": "Clubbing",
        "duration": {
          "startDate": "2025-08-16",
          "startTime": "23:00",
          "endDate": "2025-08-17",
          "endTime": "04:00"
        },
        "description": "All night long",
        "location": "Paris, France",
        "imageUrl": null
      },
      {
        "id": "15",
        "title": "Train to Amsterdam",
        "duration": {
          "startDate": "2025-08-17",
          "startTime": "09:00",
          "endDate": "2025-08-17",
          "endTime": "13:00"
        },
        "description": "High-speed train through European countryside",
        "location": "Paris to Amsterdam",
        "imageUrl": null
      },
      {
        "id": "16",
        "title": "Canal Tour Amsterdam",
        "duration": {
          "startDate": "2025-08-18",
          "startTime": "11:00",
          "endDate": "2025-08-18",
          "endTime": "13:00"
        },
        "description": "Explore Amsterdam's famous canals by boat",
        "location": "Amsterdam, Netherlands",
        "imageUrl": null
      },
      {
        "id": "17",
        "title": "Van Gogh Museum",
        "duration": {
          "startDate": "2025-08-19",
          "startTime": "10:00",
          "endDate": "2025-08-19",
          "endTime": "14:00"
        },
        "description": "World's largest collection of Van Gogh artworks",
        "location": "Amsterdam, Netherlands",
        "imageUrl": null
      },
      {
        "id": "18",
        "title": "Flight to Rome",
        "duration": {
          "startDate": "2025-08-20",
          "startTime": "08:00",
          "endDate": "2025-08-20",
          "endTime": "12:00"
        },
        "description": "Morning flight to the Eternal City",
        "location": "Amsterdam to Rome",
        "imageUrl": null
      },
      {
        "id": "19",
        "title": "Colosseum Tour",
        "duration": {
          "startDate": "2025-08-21",
          "startTime": "09:00",
          "endDate": "2025-08-21",
          "endTime": "12:00"
        },
        "description": "Explore ancient Roman architecture and history",
        "location": "Rome, Italy",
        "imageUrl": null
      },
      {
        "id": "20",
        "title": "Vatican City Visit",
        "duration": {
          "startDate": "2025-08-22",
          "startTime": "10:00",
          "endDate": "2025-08-22",
          "endTime": "16:00"
        },
        "description": "Sistine Chapel and St. Peter's Basilica",
        "location": "Vatican City",
        "imageUrl": null
      }
    ],
    "imageHeaderUrl": "https://images.pexels.com/photos/532826/pexels-photo-532826.jpeg",
    "createdDate": "2025-06-15"
  },
  {
    "id": "trip_mountain_retreat",
    "title": "Mountain Retreat",
    "description": "Peaceful getaway in the mountains",
    "location": "Banff National Park",
    "duration": {
      "startDate": "2025-09-05",
      "startTime": "08:00",
      "endDate": "2025-09-12",
      "endTime": "16:00"
    },
    "users": [
      {"name": "Charlie"},
      {"name": "Diana"}
    ],
    "events": [
      {
        "id": "21",
        "title": "Arrival & Check-in",
        "duration": {
          "startDate": "2025-09-05",
          "startTime": "15:00",
          "endDate": "2025-09-05",
          "endTime": "17:00"
        },
        "description": "Check into mountain lodge and settle in",
        "location": "Banff Lodge",
        "imageUrl": null
      },
      {
        "id": "22",
        "title": "Lake Louise Hike",
        "duration": {
          "startDate": "2025-09-06",
          "startTime": "08:00",
          "endDate": "2025-09-06",
          "endTime": "14:00"
        },
        "description": "Morning hike around the stunning turquoise lake",
        "location": "Lake Louise, Banff",
        "imageUrl": null
      },
      {
        "id": "23",
        "title": "Gondola Ride",
        "duration": {
          "startDate": "2025-09-07",
          "startTime": "10:00",
          "endDate": "2025-09-07",
          "endTime": "13:00"
        },
        "description": "Banff Gondola for breathtaking mountain views",
        "location": "Sulphur Mountain, Banff",
        "imageUrl": null
      },
      {
        "id": "24",
        "title": "Hot Springs Relaxation",
        "duration": {
          "startDate": "2025-09-07",
          "startTime": "16:00",
          "endDate": "2025-09-07",
          "endTime": "19:00"
        },
        "description": "Soak in natural hot springs after hiking",
        "location": "Banff Upper Hot Springs",
        "imageUrl": null
      },
      {
        "id": "25",
        "title": "Johnston Canyon Walk",
        "duration": {
          "startDate": "2025-09-08",
          "startTime": "09:00",
          "endDate": "2025-09-08",
          "endTime": "13:00"
        },
        "description": "Easy walk to see beautiful waterfalls and canyon",
        "location": "Johnston Canyon, Banff",
        "imageUrl": null
      },
      {
        "id": "26",
        "title": "Moraine Lake Sunrise",
        "duration": {
          "startDate": "2025-09-09",
          "startTime": "06:00",
          "endDate": "2025-09-09",
          "endTime": "10:00"
        },
        "description": "Early morning visit to catch the perfect sunrise",
        "location": "Moraine Lake, Banff",
        "imageUrl": null
      },
      {
        "id": "27",
        "title": "Wildlife Safari",
        "duration": {
          "startDate": "2025-09-10",
          "startTime": "07:00",
          "endDate": "2025-09-10",
          "endTime": "12:00"
        },
        "description": "Guided tour to spot elk, bears, and mountain goats",
        "location": "Bow Valley, Banff",
        "imageUrl": null
      },
      {
        "id": "28",
        "title": "Farewell Dinner",
        "duration": {
          "startDate": "2025-09-11",
          "startTime": "18:00",
          "endDate": "2025-09-11",
          "endTime": "21:00"
        },
        "description": "Final dinner with mountain views and local cuisine",
        "location": "Banff Town",
        "imageUrl": null
      }
    ],
    "imageHeaderUrl": null,
    "createdDate": "2025-07-01"
  }
]
        """.trimIndent()
        
        call.respondText(jsonResponse, ContentType.Application.Json)
    }

    get("/user/{id}") {
        val id = call.parameters["id"]
        val jsonResponse = """
            {
                "name": "User_$id",
                "id": "$id",
                "pfpUrl": ""
            }
        """.trimIndent()
        
        call.respondText(jsonResponse, ContentType.Application.Json)
    }
}
