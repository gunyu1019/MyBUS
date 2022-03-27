package kr.yhs.traffic.ui.pages

const val STATION_TYPE = "stationType"
const val STATION_REGION = "stationRegionType"
const val STATION_ID = "stationId"


sealed class Screen(
    val route: String
) {
    object MainScreen: Screen("mainScreen")
    object StationList: Screen("stationList")
    object StationInfo: Screen("stationInfo")
}