package kr.yhs.traffic

import androidx.wear.tiles.TileService
import kr.yhs.traffic.tiles.services.Station1TileService
import kr.yhs.traffic.tiles.services.Station2TileService


sealed class TileType(
    open val preferenceId: String,
    open val classJava: Class<out TileService?>,
) {
    object Station1: TileType("Station1Tile", Station1TileService::class.java)
    object Station2: TileType("Station2Tile", Station2TileService::class.java)

    val id: String
        get() = this.classJava.name
}