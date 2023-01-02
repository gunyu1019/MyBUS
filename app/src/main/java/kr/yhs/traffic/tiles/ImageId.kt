package kr.yhs.traffic.tiles


sealed class ImageId(
    val id: String
) {
    object Logo: ImageId("logo")
}