package kr.yhs.traffic

import androidx.wear.tiles.TileService
import kr.yhs.traffic.tiles.services.Station1TileService
import kr.yhs.traffic.tiles.services.Station2TileService


sealed class StationTileType(
    val title: String,
    val type: TileType,
    val maxBusSelect: Int
): TileType(type.preferenceId, type.classJava) {
    object Station1: StationTileType("실시간 버스 정보(1개)", TileType.Station1, 1)
    object Station2: StationTileType("실시간 버스 정보(2개)", TileType.Station2, 2)
}