package kr.yhs.traffic.tiles

import android.content.SharedPreferences
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import androidx.wear.tiles.*
import androidx.wear.tiles.DimensionBuilders.expand
import androidx.wear.tiles.LayoutElementBuilders.Column
import androidx.wear.tiles.TimelineBuilders.TimelineEntry

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
        }.build()

    private fun tileLayout(deviceParameters: DeviceParametersBuilders.DeviceParameters) =
        Column.Builder().apply {
            if (preferences.contains("station")) {
                // 등록해!
                // addContent()
            } else {
                // 불러와!
                // addContent ()
            }
            setModifiers(
                ModifiersBuilders.Modifiers.Builder().apply {
                    setHeight(expand())
                    setWidth(expand())
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