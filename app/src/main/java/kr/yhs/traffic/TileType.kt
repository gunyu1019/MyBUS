package kr.yhs.traffic

import androidx.wear.tiles.TileService
import kr.yhs.traffic.tiles.services.Station2TileService


sealed class TileType(
    val title: String,
    val preferenceId: String,
    val classJava: Class<out TileService?>
) {
    object Station2: TileType("도착 예정 버스", "Station2Tile", Station2TileService::class.java)

    val id: String
        get() = this.classJava.name
}