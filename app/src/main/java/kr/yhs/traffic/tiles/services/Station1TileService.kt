package kr.yhs.traffic.tiles.services

import androidx.wear.tiles.*
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.tiles.components.titleText

class Station1TileService : BaseStationTileService("Station1Tile", "1") {
    override suspend fun stationTileLayout(deviceParameters: DeviceParametersBuilders.DeviceParameters, stationInfo: StationInfo): LayoutElementBuilders.LayoutElement
        = LayoutElementBuilders.Column.Builder()
            .apply {
                addContent(titleText(stationInfo))
            }.build()
}
