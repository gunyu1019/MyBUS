package kr.yhs.traffic

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import androidx.wear.tiles.TileService
import kr.yhs.traffic.ui.ComposeSettingTile
import kr.yhs.traffic.utils.ClientBuilder
import kr.yhs.traffic.utils.TrafficClient

class SettingTileActivity: FragmentActivity() {
    var client: TrafficClient? = null
    private val sharedPreference = BaseEncryptedSharedPreference(this)

    fun getPreferences(filename: String) = sharedPreference.getPreferences(filename)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreference.masterKeyBuild()
        val clickableId = intent.getStringExtra(TileService.EXTRA_CLICKABLE_ID)
        val stationTileType = StationTileType::class.sealedSubclasses.filter {
            it.objectInstance?.id == clickableId
        }[0].objectInstance

        val clientBuilder = ClientBuilder()
        clientBuilder.httpClient = clientBuilder.httpClientBuild()

        val retrofit = clientBuilder.build()
        client = retrofit.create(TrafficClient::class.java)

        setContent {
            ComposeSettingTile(this, stationTileType!!).Content()
        }
    }
}