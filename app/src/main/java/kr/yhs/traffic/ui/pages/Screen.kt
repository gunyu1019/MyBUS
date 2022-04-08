package kr.yhs.traffic.ui.pages

const val STATION_TYPE = "stationType"


sealed class Screen(
    val route: String
) {
    object MainScreen: Screen("mainScreen")
    object StationList: Screen("stationList")
    object StationInfo: Screen("stationInfo")
}