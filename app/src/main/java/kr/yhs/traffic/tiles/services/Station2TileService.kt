package kr.yhs.traffic.tiles.services

import androidx.wear.tiles.*
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.models.StationRoute
import kr.yhs.traffic.tiles.components.stationText
import kr.yhs.traffic.tiles.textButton

class Station2TileService : BaseStationTileService("Station2Tile", "1") {
    override suspend fun stationTileLayout(
        deviceParameters: DeviceParametersBuilders.DeviceParameters,
        stationInfo: StationInfo,
        busRoute: List<StationRoute>?
    ): LayoutElementBuilders.LayoutElement
        = LayoutElementBuilders.Column.Builder()
            .apply {
                addContent(stationText(stationInfo))
                addContent(
                    LayoutElementBuilders.Spacer.Builder()
                        .setHeight(DimensionBuilders.dp(30f))
                        .build()
                )
                addContent(
                    LayoutElementBuilders.Spacer.Builder()
                        .setHeight(DimensionBuilders.dp(30f))
                        .build()
                )
                addContent(
                    textButton(this@Station2TileService.baseContext, "조회하기", ModifiersBuilders.Clickable.Builder().build())
                )
            }.build()
}
