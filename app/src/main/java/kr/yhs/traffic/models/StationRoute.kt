package kr.yhs.traffic.models

data class StationRoute(
    val id: String,
    val name: String,
    val type: String,
    val arrivalInfo: List<ArrivalInfo>
)
