package kr.yhs.traffic


sealed class Screen(
    val route: String
) {
    object StationList:Screen("stationList")
    object MyPositionStationList:Screen("myPositionStationList")
}