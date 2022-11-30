package kr.yhs.traffic.tiles

import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import androidx.wear.tiles.*
import androidx.wear.tiles.DimensionBuilders.expand
import androidx.wear.tiles.LayoutElementBuilders.Column
import androidx.wear.tiles.TimelineBuilders.TimelineEntry
import com.google.android.horologist.tiles.images.drawableResToImageResource

class ArrivingSoonTileService : CoroutinesTileService() {
    private val RESOURCES_VERSION = "1"
    lateinit var preferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        preferences = getPreferences("ArrivingSoonTile")
    }

    override suspend fun tileRequest(requestParams: RequestBuilders.TileRequest): TileBuilders.Tile =
        TileBuilders.Tile.Builder().apply {
            setResourcesVersion(RESOURCES_VERSION)
            setTimeline(
                TimelineBuilders.Timeline.Builder()
                    .addTimelineEntry(
                        TimelineEntry.Builder()
                            .setLayout(
                                LayoutElementBuilders.Layout.Builder()
                                    .setRoot(this@ArrivingSoonTileService.tileLayout(requestParams.deviceParameters!!))
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
        }.build()

    override suspend fun resourcesRequest(requestParams: RequestBuilders.ResourcesRequest): ResourceBuilders.Resources =
        ResourceBuilders.Resources.Builder().apply {
            setVersion(RESOURCES_VERSION)
            addIdToImageMapping(
                ImageId.Logo.id,
                drawableResToImageResource(kr.yhs.traffic.R.mipmap.ic_launcher)
            )
        }.build()

    private fun tileLayout(deviceParameters: DeviceParametersBuilders.DeviceParameters) =
        LayoutElementBuilders.Box.Builder().apply {
            setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
            setWidth(expand())
            setHeight(expand())
            addContent (
                Column.Builder().apply {
                    if (!preferences.contains("station")) {
                        SettingRequirement(this).content("곧 도착하는 버스 정보", "곧 도착할 버스 정보를 불러오기 위한 버스 정류장을 등록해주세요.")
                    } else {
                        // 불러와!
                        // addContent ()
                    }
                }.build()
            )
        }.build()

    override fun onDestroy() {
        super.onDestroy()
        preferences.edit {
            remove("station")
            commit()
        }
    }
}
