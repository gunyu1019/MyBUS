package kr.yhs.traffic.tiles.components

import androidx.wear.tiles.DimensionBuilders.sp
import androidx.wear.tiles.LayoutElementBuilders
import androidx.wear.tiles.LayoutElementBuilders.FontStyle
import androidx.wear.tiles.LayoutElementBuilders.TEXT_OVERFLOW_ELLIPSIZE_END
import kr.yhs.traffic.models.StationInfo


fun titleText(stationInfo: StationInfo) = LayoutElementBuilders.Text.Builder()
    .setText(stationInfo.name)
    .setOverflow(TEXT_OVERFLOW_ELLIPSIZE_END)
    .setFontStyle(
        FontStyle.Builder()
            .setSize(sp(14F))
            .build()
    ).setMaxLines(1)
    .build()