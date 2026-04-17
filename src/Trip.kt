data class Trip(
    val id: Int,
    val startCity: String,
    val stopCity: String,
    val date: String, // Format: dd/MM/yyyy-HH:mm
    var seats: Int
) {
    override fun toString(): String {
        return "ID: $id | $startCity -> $stopCity | Data: $date | Locuri: $seats"
    }
}