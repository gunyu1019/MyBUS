package kr.yhs.traffic.ui.pages

const val STATION_TYPE = "stationType"
const val SCROLL_TYPE_NAV_ARGUMENT = "scrollType"


sealed class Screen(
    val route: String
) {
    object MainScreen: Screen("mainScreen")
    object StationList: Screen("stationList")
    object StationInfo: Screen("stationInfo")
}