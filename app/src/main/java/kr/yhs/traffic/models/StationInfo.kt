package kr.yhs.traffic.models


data class StationInfo(
    val name: String,
    val id: Int,
    val posX: Double,
    val posY: Double,
    val displayId: Any?,
    val stationId: Int,
    val type: Int
)
