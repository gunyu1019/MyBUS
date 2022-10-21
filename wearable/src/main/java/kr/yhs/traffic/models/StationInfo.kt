package kr.yhs.traffic.models


data class StationInfo(
    val name: String,
    val id: String,
    val ids: String? = null,
    val posX: Double,
    val posY: Double,
    val displayId: Any?,
    val stationId: Any,
    val type: Int
) {
    val routeId: String
        get() {
            if (id == "-2" && ids != null)
                return ids
            return id
        }
}
