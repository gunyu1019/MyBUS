package kr.yhs.traffic.tiles.services

import androidx.wear.tiles.*
import androidx.wear.tiles.DimensionBuilders.dp
import androidx.wear.tiles.DimensionBuilders.sp
import androidx.wear.tiles.LayoutElementBuilders.Spacer
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.models.StationRoute
import kr.yhs.traffic.tiles.components.busArrivalText
import kr.yhs.traffic.tiles.components.busRouteText
import kr.yhs.traffic.tiles.components.spacer
import kr.yhs.traffic.tiles.components.stationText
import kr.yhs.traffic.tiles.textButton

class Station1TileService : BaseStationTileService("Station1Tile", "1") {
    override suspend fun stationTileLayout(
        deviceParameters: DeviceParametersBuilders.DeviceParameters,
        stationInfo: StationInfo,
        busRoute: List<StationRoute>?
    ): LayoutElementBuilders.LayoutElement
        = LayoutElementBuilders.Column.Builder()
            .apply {
                addContent(busRouteText(busRoute!![0], sp(19f), dp(6f)))
                addContent(
                    spacer(height = dp(2f))
                )
                addContent(stationText(stationInfo))
                addContent(
                    spacer(height = dp(30f))
                )
                addContent(
                    busArrivalText(this@Station1TileService.getArrivalText(busRoute[0]))
                )
                addContent(
                    spacer(height = dp(30f))
                )
                addContent(
                    textButton(this@Station1TileService.baseContext, "조회하기", updateClickable)
                )
            }.build()
}
