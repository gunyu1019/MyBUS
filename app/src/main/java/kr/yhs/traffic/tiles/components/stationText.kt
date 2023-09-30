package kr.yhs.traffic.tiles.components

import androidx.wear.protolayout.DimensionBuilders.SpProp
import androidx.wear.protolayout.DimensionBuilders.sp
import androidx.wear.protolayout.LayoutElementBuilders.FONT_WEIGHT_BOLD
import androidx.wear.protolayout.LayoutElementBuilders.FontStyle
import androidx.wear.protolayout.LayoutElementBuilders.TEXT_OVERFLOW_ELLIPSIZE_END
import androidx.wear.protolayout.LayoutElementBuilders.Text
import androidx.wear.protolayout.ModifiersBuilders.Clickable
import androidx.wear.protolayout.ModifiersBuilders.Modifiers
import kr.yhs.traffic.models.StationInfo


fun stationText(
    stationInfo: StationInfo, fontSize: SpProp = sp(13f), clickable: Clickable? = null
) = Text.Builder().apply {
        if (clickable != null) {
            setModifiers(
                Modifiers.Builder().setClickable(clickable).build()
            )
        }
        setText(stationInfo.name)
        setOverflow(TEXT_OVERFLOW_ELLIPSIZE_END)
        setFontStyle(
            FontStyle.Builder().setSize(fontSize).setWeight(FONT_WEIGHT_BOLD).build()
        )
        setMaxLines(1)
    }.build()