package kr.yhs.traffic.tiles

const val STATION_TYPE = "stationType"


sealed class ImageId(
    val id: String
) {
    object Logo: ImageId("logo")
}