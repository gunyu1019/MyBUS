package kr.yhs.traffic.tiles.components

import androidx.wear.tiles.DimensionBuilders.SpProp
import androidx.wear.tiles.DimensionBuilders.sp
import androidx.wear.tiles.LayoutElementBuilders
import androidx.wear.tiles.LayoutElementBuilders.*
import androidx.wear.tiles.ModifiersBuilders
import androidx.wear.tiles.ModifiersBuilders.Clickable
import androidx.wear.tiles.ModifiersBuilders.Modifiers
import kr.yhs.traffic.models.StationInfo


fun stationText(
    stationInfo: StationInfo,
    fontSize: SpProp = sp(13f),
    clickable: Clickable? = null
) = Text.Builder()
    .apply {
        if (clickable != null) {
            setModifiers(
                Modifiers.Builder()
                    .setClickable(clickable)
                    .build()
            )
        }
        setText(stationInfo.name)
        setOverflow(TEXT_OVERFLOW_ELLIPSIZE_END)
        setFontStyle(
            FontStyle.Builder()
                .setSize(fontSize)
                .setWeight(FONT_WEIGHT_BOLD)
                .build()
        )
        setMaxLines(1)
    }.build()