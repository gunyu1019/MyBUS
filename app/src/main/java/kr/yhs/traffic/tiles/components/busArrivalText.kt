package kr.yhs.traffic.tiles.components

import androidx.wear.protolayout.DimensionBuilders.SpProp
import androidx.wear.protolayout.DimensionBuilders.sp
import androidx.wear.protolayout.LayoutElementBuilders.*


@androidx.annotation.OptIn(androidx.wear.tiles.TilesExperimental::class)
fun busArrivalText(
    timeText: String, fontSize: SpProp = sp(26f)
) = Text.Builder().apply {
    setText(timeText)
    setFontStyle(
        FontStyle.Builder().setSize(fontSize).setWeight(FONT_WEIGHT_BOLD).build()
    )
    setMaxLines(1)
}.build()
