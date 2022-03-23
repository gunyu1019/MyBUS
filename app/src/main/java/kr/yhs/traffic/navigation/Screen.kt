package kr.yhs.traffic.navigation

const val STATION_TYPE = "stationType"


sealed class Screen(
    val route: String
) {
    object MainScreen: Screen("mainScreen")
    object StationList: Screen("stationList")
}