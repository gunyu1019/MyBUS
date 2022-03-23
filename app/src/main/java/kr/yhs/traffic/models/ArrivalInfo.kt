package kr.yhs.traffic.models

data class ArrivalInfo(
    val carNumber: String?,
    val congestion: Int?,
    val isArrival: Boolean,
    val isFull: Boolean,
    val lowBus: Boolean,
    val prevCount: Int?,
    val seat: Int?,
    val time: Int?
)
