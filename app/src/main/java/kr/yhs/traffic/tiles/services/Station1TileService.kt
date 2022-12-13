package kr.yhs.traffic.tiles.services

import android.util.Log
import androidx.compose.ui.graphics.toArgb
import androidx.wear.tiles.*
import androidx.wear.tiles.DimensionBuilders.dp
import androidx.wear.tiles.LayoutElementBuilders.Spacer
import androidx.wear.tiles.ModifiersBuilders.Clickable
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.models.StationRoute
import kr.yhs.traffic.tiles.components.busRouteText
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
                addContent(busRouteText(busRoute!![0]))
                addContent(
                    Spacer.Builder()
                        .setHeight(dp(2f))
                        .build()
                )
                addContent(stationText(stationInfo))
                addContent(
                    Spacer.Builder()
                        .setHeight(dp(30f))
                        .build()
                )
                val time = if (busRoute[0].arrivalInfo.isNotEmpty()) busRoute[0].arrivalInfo[0].time.toString() else "-"
                addContent(
                    LayoutElementBuilders.Text.Builder()
                        .setText("${time}분")
                        .setFontStyle(
                            LayoutElementBuilders.FontStyle.Builder()
                                .setSize(DimensionBuilders.sp(22f))
                                .setWeight(LayoutElementBuilders.FONT_WEIGHT_BOLD)
                                .build()
                            )
                        .setMaxLines(1)
                        .build()
                )
                addContent(
                    Spacer.Builder()
                        .setHeight(dp(30f))
                        .build()
                )
                addContent(
                    textButton(this@Station1TileService.baseContext, "조회하기", updateClickable)
                )
            }.build()
}
