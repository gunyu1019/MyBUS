package kr.yhs.traffic.tiles.components

import androidx.annotation.OptIn
import androidx.compose.ui.graphics.toArgb
import androidx.wear.protolayout.ColorBuilders.ColorProp
import androidx.wear.protolayout.DimensionBuilders.DpProp
import androidx.wear.protolayout.DimensionBuilders.SpProp
import androidx.wear.protolayout.DimensionBuilders.dp
import androidx.wear.protolayout.DimensionBuilders.sp
import androidx.wear.protolayout.LayoutElementBuilders.*
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ModifiersBuilders.Background
import androidx.wear.protolayout.ModifiersBuilders.Padding
import androidx.wear.protolayout.expression.ProtoLayoutExperimental
import kr.yhs.traffic.models.StationRoute
import kr.yhs.traffic.ui.theme.BusColor


@OptIn(ProtoLayoutExperimental::class)
fun busRouteText(
    busInfo: StationRoute, fontSize: SpProp = sp(19f), padding: DpProp = dp(6f)
) = Text.Builder().apply {
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
        FontStyle.Builder().setSize(fontSize).setWeight(FONT_WEIGHT_MEDIUM).build()
    )
    setMaxLines(1)
    setModifiers(
        ModifiersBuilders.Modifiers.Builder().setBackground(
                Background.Builder().setColor(
                        ColorProp.Builder(backgroundColor.color.toArgb()).build()
                    ).setCorner(
                        ModifiersBuilders.Corner.Builder().setRadius(dp(12f)).build()
                    ).build()
            ).setPadding(
                Padding.Builder().setStart(padding).setEnd(padding).build()
            ).build()
    )
}.build()
