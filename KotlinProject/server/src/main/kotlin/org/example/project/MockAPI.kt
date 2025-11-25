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
        "location": {
          "latitude": 43.6532,
          "longitude": -79.3832,
          "address": "Toronto, ON",
          "title": "Downtown Toronto"
        },
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
        "location": {
          "latitude": 43.0896,
          "longitude": -79.0849,
          "address": "Niagara Falls, ON",
          "title": "Niagara Falls"
        },
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
        "location": {
          "latitude": 43.0882,
          "longitude": -79.0748,
          "address": "Niagara Falls, ON",
          "title": "Maid of the Mist Dock"
        },
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
        "location": {
          "latitude": 43.0780,
          "longitude": -79.0744,
          "address": "Table Rock, Niagara Falls",
          "title": "Table Rock Welcome Centre"
        },
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
        "location": {
          "latitude": 43.6532,
          "longitude": -79.3832,
          "address": "Highway 401",
          "title": "Highway 401 East"
        },
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
        "location": {
          "latitude": 44.2312,
          "longitude": -76.4860,
          "address": "Kingston, ON",
          "title": "Market Square"
        },
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
        "location": {
          "latitude": 45.4215,
          "longitude": -75.6972,
          "address": "Parliament Hill, Ottawa",
          "title": "Parliament Hill"
        },
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
        "location": {
          "latitude": 45.4270,
          "longitude": -75.6920,
          "address": "ByWard Market, Ottawa",
          "title": "ByWard Market"
        },
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
        "location": {
          "latitude": 48.8584,
          "longitude": 2.2945,
          "address": "Paris, France",
          "title": "Eiffel Tower"
        },
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
        "location": {
          "latitude": 48.8606,
          "longitude": 2.3376,
          "address": "Paris, France",
          "title": "Louvre Museum"
        },
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
        "location": {
          "latitude": 48.8566,
          "longitude": 2.3522,
          "address": "Paris, France",
          "title": "Café de Flore"
        },
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
        "location": {
          "latitude": 48.8534,
          "longitude": 2.3488,
          "address": "Paris, France",
          "title": "Seine Riverbank"
        },
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
        "location": {
          "latitude": 48.8600,
          "longitude": 2.3266,
          "address": "Paris, France",
          "title": "Le Jules Verne"
        },
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
        "location": {
          "latitude": 48.8738,
          "longitude": 2.3540,
          "address": "Paris, France",
          "title": "Rex Club"
        },
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
        "location": {
          "latitude": 48.8440,
          "longitude": 2.3744,
          "address": "Paris to Amsterdam",
          "title": "Gare du Nord"
        },
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
        "location": {
          "latitude": 52.3676,
          "longitude": 4.9041,
          "address": "Amsterdam, Netherlands",
          "title": "Amsterdam Canal Ring"
        },
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
        "location": {
          "latitude": 52.3580,
          "longitude": 4.8811,
          "address": "Amsterdam, Netherlands",
          "title": "Van Gogh Museum"
        },
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
        "location": {
          "latitude": 52.3105,
          "longitude": 4.7683,
          "address": "Amsterdam to Rome",
          "title": "Amsterdam Schiphol Airport"
        },
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
        "location": {
          "latitude": 41.8902,
          "longitude": 12.4922,
          "address": "Rome, Italy",
          "title": "Colosseum"
        },
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
        "location": {
          "latitude": 41.9029,
          "longitude": 12.4534,
          "address": "Vatican City",
          "title": "Vatican Museums"
        },
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
        "location": {
          "latitude": 51.1784,
          "longitude": -115.5708,
          "address": "Banff Lodge",
          "title": "Banff Mountain Lodge"
        },
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
        "location": {
          "latitude": 51.4254,
          "longitude": -116.1773,
          "address": "Lake Louise, Banff",
          "title": "Lake Louise"
        },
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
        "location": {
          "latitude": 51.1486,
          "longitude": -115.5610,
          "address": "Sulphur Mountain, Banff",
          "title": "Banff Gondola"
        },
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
        "location": {
          "latitude": 51.1559,
          "longitude": -115.5585,
          "address": "Banff Upper Hot Springs",
          "title": "Banff Upper Hot Springs"
        },
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
        "location": {
          "latitude": 51.2441,
          "longitude": -115.8393,
          "address": "Johnston Canyon, Banff",
          "title": "Johnston Canyon"
        },
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
        "location": {
          "latitude": 51.3333,
          "longitude": -116.1833,
          "address": "Moraine Lake, Banff",
          "title": "Moraine Lake"
        },
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
        "location": {
          "latitude": 51.2000,
          "longitude": -115.6000,
          "address": "Bow Valley, Banff",
          "title": "Bow Valley Parkway"
        },
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
        "location": {
          "latitude": 51.1783,
          "longitude": -115.5717,
          "address": "Banff Town",
          "title": "Banff Avenue"
        },
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
