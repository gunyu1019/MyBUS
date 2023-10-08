package kr.yhs.traffic.tiles.services

import androidx.wear.protolayout.DeviceParametersBuilders
import androidx.wear.protolayout.DimensionBuilders.DpProp
import androidx.wear.protolayout.DimensionBuilders.dp
import androidx.wear.protolayout.DimensionBuilders.sp
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.LayoutElementBuilders.VERTICAL_ALIGN_CENTER
import androidx.wear.protolayout.LayoutElementBuilders.VerticalAlignmentProp
import kr.yhs.traffic.R
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.models.StationRoute
import kr.yhs.traffic.tiles.components.busArrivalText
import kr.yhs.traffic.tiles.components.busRouteText
import kr.yhs.traffic.tiles.components.spacer
import kr.yhs.traffic.tiles.components.stationText
import kr.yhs.traffic.tiles.components.textButton

class Station2TileService : BaseStationTileService("Station2Tile", "1") {
    override suspend fun stationTileLayout(
        deviceParameters: DeviceParametersBuilders.DeviceParameters,
        stationInfo: StationInfo,
        routeInfo: List<StationRoute>?
    ): LayoutElementBuilders.LayoutElement = LayoutElementBuilders.Column.Builder()
        .apply {
            addContent(stationText(stationInfo, sp(14f), clickable = stationClickable))
            addContent(
                spacer(height = dp(25f))
            )
            addContent(
                stationTileRow(deviceParameters, routeInfo!!)
            )
            addContent(
                spacer(height = dp(25f))
            )
            addContent(
                textButton(
                    this@Station2TileService.baseContext,
                    getString(R.string.station_tile_service_update_button),
                    updateClickable
                )
            )
        }.build()

    private fun stationTileRow(
        deviceParameters: DeviceParametersBuilders.DeviceParameters,
        busRoute: List<StationRoute>
    ) = LayoutElementBuilders.Row.Builder()
        .apply {
            setVerticalAlignment(
                VerticalAlignmentProp
                    .Builder()
                    .setValue(VERTICAL_ALIGN_CENTER)
                    .build()
            )
            addContent(
                arrivalTileLayout(
                    busRoute[0],
                    dp(((deviceParameters.screenWidthDp - 10) / 2).toFloat())
                )
            )
            addContent(spacer(width = dp(10f)))
            addContent(
                arrivalTileLayout(
                    busRoute[1],
                    dp(((deviceParameters.screenWidthDp - 10) / 2).toFloat())
                )
            )
        }.build()

    private fun arrivalTileLayout(
        busRoute: StationRoute,
        width: DpProp = dp(80f)
    ) = LayoutElementBuilders.Column.Builder().apply {
        setWidth(width)
        addContent(
            busRouteText(busRoute, sp(14f), dp(4f))
        )
        addContent(
            busArrivalText(this@Station2TileService.getArrivalText(busRoute), sp(22f))
        )
    }.build()
}
