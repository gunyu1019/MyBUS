package kr.yhs.traffic.tiles.components

import androidx.wear.tiles.DimensionBuilders.sp
import androidx.wear.tiles.LayoutElementBuilders
import androidx.wear.tiles.LayoutElementBuilders.*
import kr.yhs.traffic.models.StationInfo


fun stationText(stationInfo: StationInfo) = LayoutElementBuilders.Text.Builder()
    .setText(stationInfo.name)
    .setOverflow(TEXT_OVERFLOW_ELLIPSIZE_END)
    .setFontStyle(
        FontStyle.Builder()
            .setSize(sp(13f))
            .setWeight(FONT_WEIGHT_BOLD)
            .build()
    ).setMaxLines(1)
    .build()