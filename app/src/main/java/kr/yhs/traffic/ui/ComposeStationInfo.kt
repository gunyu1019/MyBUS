package kr.yhs.traffic.ui

import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kr.yhs.traffic.StationInfoActivity
import kr.yhs.traffic.TileType
import kr.yhs.traffic.utils.StationPreferences


class ComposeStationInfo(
    private val activity: StationInfoActivity,
    private val tileType: TileType
) : BaseComposeStationInfo(activity, activity.client), StationPreferences {
    override fun getPreferences(filename: String): SharedPreferences = activity.getPreferences(filename)

    @Composable
    override fun Content() {
        val coroutineScope = rememberCoroutineScope()
        val preferences = getPreferences(this.tileType.preferenceId)
        val stationInfo = getStationInfo(preferences)
        ComposeStationInfoPage(station = stationInfo, scope = coroutineScope, isTile = true) {
            activity.finish()
        }
    }
}