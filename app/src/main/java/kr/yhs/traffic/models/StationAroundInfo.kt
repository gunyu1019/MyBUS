package kr.yhs.traffic.models


data class StationAroundInfo(
    val name: String,
    val id: Int,
    val posX: Double,
    val posY: Double,
    val displayId: Any?,
    val stationId: Int,
    val type: Int,
    val distance: Int
) {
    fun convertToStationInfo(): StationInfo {
        return StationInfo(
            name = name,
            id = id,
            posX = posX,
            posY = posY,
            displayId = displayId,
            stationId = stationId,
            type = type
        )
    }
}