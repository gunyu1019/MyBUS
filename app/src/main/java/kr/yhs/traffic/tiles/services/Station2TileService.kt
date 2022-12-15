package kr.yhs.traffic.tiles.services

import android.util.Log
import androidx.wear.tiles.*
import androidx.wear.tiles.DimensionBuilders.*
import androidx.wear.tiles.LayoutElementBuilders.VERTICAL_ALIGN_CENTER
import androidx.wear.tiles.LayoutElementBuilders.VerticalAlignmentProp
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.models.StationRoute
import kr.yhs.traffic.tiles.components.busArrivalText
import kr.yhs.traffic.tiles.components.busRouteText
import kr.yhs.traffic.tiles.components.spacer
import kr.yhs.traffic.tiles.components.stationText
import kr.yhs.traffic.tiles.textButton

class Station2TileService : BaseStationTileService("Station2Tile", "1") {
    override suspend fun stationTileLayout(
        deviceParameters: DeviceParametersBuilders.DeviceParameters,
        stationInfo: StationInfo,
        routeInfo: List<StationRoute>?
    ): LayoutElementBuilders.LayoutElement
        = LayoutElementBuilders.Column.Builder()
            .apply {
                addContent(stationText(stationInfo, sp(14f), clickable = stationClickable))
                addContent(
                    spacer(height = dp(30f))
                )
                addContent(
                    stationTileRow(deviceParameters, routeInfo!!)
                )
                addContent(
                    spacer(height = dp(30f))
                )
                addContent(
                    textButton(this@Station2TileService.baseContext, "조회하기", updateClickable)
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
            addContent(arrivalTileLayout(busRoute[0], dp(((deviceParameters.screenWidthDp - 10) / 2).toFloat())))
            addContent(spacer(width = dp(10f)))
            addContent(arrivalTileLayout(busRoute[1], dp(((deviceParameters.screenWidthDp - 10) / 2).toFloat())))
        }.build()

    private fun arrivalTileLayout(
        busRoute: StationRoute,
        width: DpProp = dp(80f)
    ) = LayoutElementBuilders.Column.Builder().apply {
        setWidth(width)
        addContent (
            busRouteText(busRoute, sp(14f), dp(4f))
        )
        addContent (
            busArrivalText(this@Station2TileService.getArrivalText(busRoute), sp(22f))
        )
    }.build()
}
