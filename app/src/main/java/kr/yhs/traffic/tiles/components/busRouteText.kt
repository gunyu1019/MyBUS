package kr.yhs.traffic.tiles.components

import androidx.compose.ui.graphics.toArgb
import androidx.wear.tiles.ColorBuilders.ColorProp
import androidx.wear.tiles.DimensionBuilders.dp
import androidx.wear.tiles.DimensionBuilders.sp
import androidx.wear.tiles.LayoutElementBuilders
import androidx.wear.tiles.LayoutElementBuilders.FontStyle
import androidx.wear.tiles.LayoutElementBuilders.TEXT_OVERFLOW_ELLIPSIZE_END
import androidx.wear.tiles.ModifiersBuilders
import androidx.wear.tiles.ModifiersBuilders.Background
import kr.yhs.traffic.models.StationRoute
import kr.yhs.traffic.ui.theme.BusColor


fun busRouteText(busInfo: StationRoute) = LayoutElementBuilders.Text.Builder().apply {
    var backgroundColor = BusColor.Default
    for (busColor in BusColor.values()) {
        if (busInfo.type == busColor.typeCode) {
            backgroundColor = busColor
            break
        }
    }

    setText(busInfo.name)
    setOverflow(TEXT_OVERFLOW_ELLIPSIZE_END)
    setFontStyle(
        FontStyle.Builder()
            .setSize(sp(16f))
            .build()
    )
    setMaxLines(1)
    setModifiers(
        ModifiersBuilders.Modifiers.Builder()
            .setBackground(
                Background.Builder()
                    .setColor(
                        ColorProp.Builder()
                            .setArgb(backgroundColor.color.toArgb())
                            .build()
                    ).setCorner(
                        ModifiersBuilders.Corner.Builder()
                            .setRadius(dp(3f))
                            .build()
                    ).build()
            )
            .build()
    )
}.build()
