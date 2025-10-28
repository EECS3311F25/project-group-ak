// class User(
//     var id: String,
//     var name: String,
//     var trips: Array<Trip> = emptyArray()
// ) {

//     fun toMap(): Map<String, Any?> = mapOf(
//         "id" to id,
//         "name" to name,
//         "trips" to trips
//     )

// /*
//     This is a sample Javadoc. The thing that every developer dread
//     hurrdurr refactor this refactor that i love people leaving messes

//     @param utter cringe
//     @return pain
// */
//     override fun toString(): String =
//         "User(id=$id, name=$name, numTrips=${trips.size})"

//     override fun hashCode(): Int =
//         listOf(id, name).hashCode()
// }