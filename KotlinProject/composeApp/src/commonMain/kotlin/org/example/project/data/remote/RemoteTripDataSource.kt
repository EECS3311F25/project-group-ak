// package org.example.project.data.remote

// import io.ktor.client.*
// import io.ktor.client.call.*
// import io.ktor.client.request.*
// import io.ktor.http.*
// import org.example.project.data.source.TripDataSource
// import org.example.project.model.Trip

// class RemoteTripDataSource(
//     private val client: HttpClient,
//     private val baseUrl: String
// ) : TripDataSource {
//     // TODO: use actual endpoints from the server
//     override suspend fun getAllTrips(): List<Trip> =
//         client.get("$baseUrl/trips").body()

//     override suspend fun getTrip(id: String): Trip? =
//         client.get("$baseUrl/trips/$id").body()

//     override suspend fun createTrip(trip: Trip): Trip =
//         client.post("$baseUrl/trips") {
//             contentType(ContentType.Application.Json)
//             setBody(trip)
//         }.body()

//     override suspend fun updateTrip(trip: Trip): Trip =
//         client.put("$baseUrl/trips/${trip.id}") {
//             contentType(ContentType.Application.Json)
//             setBody(trip)
//         }.body()

//     override suspend fun deleteTrip(id: String) =
//         client.delete("$baseUrl/trips/$id")
// }