package kr.yhs.traffic.tiles.components

import androidx.compose.ui.graphics.toArgb
import androidx.wear.tiles.ColorBuilders.ColorProp
import androidx.wear.tiles.DimensionBuilders.DpProp
import androidx.wear.tiles.DimensionBuilders.SpProp
import androidx.wear.tiles.DimensionBuilders.dp
import androidx.wear.tiles.DimensionBuilders.sp
import androidx.wear.tiles.LayoutElementBuilders
import androidx.wear.tiles.LayoutElementBuilders.*
import androidx.wear.tiles.ModifiersBuilders
import androidx.wear.tiles.ModifiersBuilders.Background
import androidx.wear.tiles.ModifiersBuilders.Padding
import kr.yhs.traffic.models.StationRoute
import kr.yhs.traffic.ui.theme.BusColor


@androidx.annotation.OptIn(androidx.wear.tiles.TilesExperimental::class)
fun busArrivalText(
    timeText: String,
    fontSize: SpProp = sp(26f)
) = Text.Builder().apply {
    setText(timeText)
    setFontStyle(
        FontStyle.Builder()
            .setSize(fontSize)
            .setWeight(FONT_WEIGHT_BOLD)
            .build()
    )
    setMaxLines(1)
}.build()
