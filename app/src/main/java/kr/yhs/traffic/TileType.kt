package kr.yhs.traffic

import androidx.wear.tiles.TileService
import kr.yhs.traffic.tiles.services.Station1TileService
import kr.yhs.traffic.tiles.services.Station2TileService


sealed class TileType(
    val title: String,
    val preferenceId: String,
    val classJava: Class<out TileService?>,
    val maxBusSelect: Int
) {
    object Station1: TileType("실시간 버스 정보(1개)", "Station1Tile", Station1TileService::class.java, 1)
    object Station2: TileType("실시간 버스 정보(2개)", "Station2Tile", Station2TileService::class.java, 2)

    val id: String
        get() = this.classJava.name
}