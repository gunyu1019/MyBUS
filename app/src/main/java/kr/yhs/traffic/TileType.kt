package kr.yhs.traffic

import androidx.wear.tiles.TileService
import kr.yhs.traffic.tiles.services.ArrivingSoonTileService


sealed class TileType(
    val title: String,
    val preferenceId: String,
    val classJava: Class<out TileService?>
) {
    object ArrivingSoonTile: TileType("도착 예정 버스", "ArrivingSoonTile", ArrivingSoonTileService::class.java)

    val id: String
        get() = this.classJava.name
}