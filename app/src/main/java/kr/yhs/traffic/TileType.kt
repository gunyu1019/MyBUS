package kr.yhs.traffic

import kr.yhs.traffic.tiles.services.ArrivingSoonTileService


sealed class TileType(
    val id: String,
    val title: String
) {
    object ArrivingSoonTile: TileType(ArrivingSoonTileService::class.java.name, "도착 예정 버스")
}