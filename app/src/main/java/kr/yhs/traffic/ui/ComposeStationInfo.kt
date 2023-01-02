package kr.yhs.traffic.ui

import android.content.SharedPreferences
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.LocalContentColor
import kr.yhs.traffic.R
import kr.yhs.traffic.StationInfoActivity
import kr.yhs.traffic.TileType
import kr.yhs.traffic.ui.components.NextButton
import kr.yhs.traffic.ui.theme.StationInfoSelection
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