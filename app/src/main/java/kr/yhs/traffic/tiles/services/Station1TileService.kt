package kr.yhs.traffic.tiles.services

import androidx.wear.protolayout.DeviceParametersBuilders
import androidx.wear.protolayout.DimensionBuilders.dp
import androidx.wear.protolayout.DimensionBuilders.sp
import androidx.wear.protolayout.LayoutElementBuilders
import kr.yhs.traffic.R
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.models.StationRoute
import kr.yhs.traffic.tiles.components.busArrivalText
import kr.yhs.traffic.tiles.components.busRouteText
import kr.yhs.traffic.tiles.components.spacer
import kr.yhs.traffic.tiles.components.stationText
import kr.yhs.traffic.tiles.components.textButton

class Station1TileService : BaseStationTileService("Station1Tile", "1") {
    override suspend fun stationTileLayout(
        deviceParameters: DeviceParametersBuilders.DeviceParameters,
        stationInfo: StationInfo,
        routeInfo: List<StationRoute>?
    ): LayoutElementBuilders.LayoutElement
        = LayoutElementBuilders.Column.Builder()
            .apply {
                addContent(busRouteText(routeInfo!![0], sp(19f), dp(6f)))
                addContent(
                    spacer(height = dp(2f))
                )
                addContent(stationText(stationInfo, clickable = stationClickable))
                addContent(
                    spacer(height = dp(30f))
                )
                addContent(
                    busArrivalText(this@Station1TileService.getArrivalText(routeInfo[0]))
                )
                addContent(
                    spacer(height = dp(30f))
                )
                addContent(
                    textButton(
                        this@Station1TileService.baseContext,
                        getString(R.string.station_tile_service_update_button),
                        updateClickable
                    )
                )
            }.build()
}
