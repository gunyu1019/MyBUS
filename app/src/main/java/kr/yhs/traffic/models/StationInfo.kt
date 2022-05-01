package kr.yhs.traffic.models


data class StationInfo(
    val name: String,
    val id: String,
    val posX: Double,
    val posY: Double,
    val displayId: Any?,
    val stationId: Any,
    val type: Int
)
