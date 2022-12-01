package kr.yhs.traffic

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import androidx.wear.tiles.TileService
import kr.yhs.traffic.ui.ComposeSettingTile

class SettingTileActivity: FragmentActivity() {
    private val sharedPreference = BaseEncryptedSharedPreference(this)

    fun getPreferences(filename: String) = sharedPreference.getPreferences(filename)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreference.masterKeyBuild()
        val clickableId = intent.getStringExtra(TileService.EXTRA_CLICKABLE_ID)
        Log.i("clickableId", clickableId.toString())
        val tileType = TileType::class.sealedSubclasses.filter {
            Log.i("debug", it.objectInstance!!.id.toString())
            Log.i("debug", clickableId.toString())
            it.objectInstance?.id == clickableId }[0].objectInstance
        setContent {
            ComposeSettingTile(this, tileType!!).Content()
        }
    }
}