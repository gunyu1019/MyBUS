package kr.yhs.traffic.models

data class StationRoute(
    val id: String,
    val name: String,
    val type: Int,
    val isEnd: Boolean?,
    val arrivalInfo: List<ArrivalInfo>
)
