package kr.yhs.traffic.models


data class StationInfo(
    val name: String,
    val id: Int,
    val posX: Float,
    val posY: Float,
    val displayId: Any?,
    val stationId: Int,
    val type: Int
)
