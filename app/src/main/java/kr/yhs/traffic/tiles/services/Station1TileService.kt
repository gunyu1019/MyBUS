package kr.yhs.traffic.tiles.services

import android.util.Log
import androidx.wear.tiles.DeviceParametersBuilders
import androidx.wear.tiles.LayoutElementBuilders
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.models.StationRoute
import kr.yhs.traffic.tiles.components.stationText

class Station1TileService : BaseStationTileService("Station1Tile", "1") {
    override suspend fun stationTileLayout(
        deviceParameters: DeviceParametersBuilders.DeviceParameters,
        stationInfo: StationInfo,
        busRoute: List<StationRoute>?
    ): LayoutElementBuilders.LayoutElement
        = LayoutElementBuilders.Column.Builder()
            .apply {
                // addContent(busRouteText(busRoute!![0]))
                addContent(stationText(stationInfo))
                if (busRoute != null) {
                    Log.i("bus", busRoute.toString())
                    addContent(
                        LayoutElementBuilders.Text.Builder()
                            .setText(busRoute[0].name)
                            .build()
                    )
                    addContent(
                        LayoutElementBuilders.Text.Builder()
                            .setText(busRoute[0].arrivalInfo[0].time.toString())
                            .build()
                    )
                }
            }.build()
}
