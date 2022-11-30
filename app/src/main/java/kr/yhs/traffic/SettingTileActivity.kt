package kr.yhs.traffic

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.rememberCoroutineScope
import androidx.fragment.app.FragmentActivity
import androidx.wear.tiles.TileService
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.ui.ComposeApp
import kr.yhs.traffic.ui.ComposeSettingTile
import kr.yhs.traffic.ui.pages.StationListPage

class SettingTileActivity: FragmentActivity() {
    private val sharedPreference = BaseEncryptedSharedPreference(this)

    fun getPreferences(filename: String) = sharedPreference.getPreferences(filename)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreference.masterKeyBuild()
        val clickableId = intent.getStringExtra(TileService.EXTRA_CLICKABLE_ID)
        setContent {
            ComposeSettingTile(this).Content()
        }
    }
}