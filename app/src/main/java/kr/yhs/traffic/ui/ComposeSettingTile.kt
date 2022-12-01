package kr.yhs.traffic.ui

import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kr.yhs.traffic.R
import kr.yhs.traffic.SettingTileActivity
import kr.yhs.traffic.ui.pages.StationListPage


class ComposeSettingTile(private val activity: SettingTileActivity): BaseCompose(activity) {
    override fun getPreferences(filename: String): SharedPreferences = activity.getPreferences(filename)

    @Composable
    override fun Content() {
        val coroutineScope = rememberCoroutineScope()
        val bookmarkStation = getStationBookmarkList()
        StationListPage(
            "등록할 정류장", bookmarkStation, null, coroutineScope
        ) {

        }
    }
}