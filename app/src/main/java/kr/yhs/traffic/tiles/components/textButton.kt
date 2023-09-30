package kr.yhs.traffic.tiles.components

import android.content.Context
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.DimensionBuilders
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.LayoutElementBuilders.Box
import androidx.wear.protolayout.LayoutElementBuilders.FONT_WEIGHT_BOLD
import androidx.wear.protolayout.LayoutElementBuilders.FONT_WEIGHT_NORMAL
import androidx.wear.protolayout.LayoutElementBuilders.FontStyle
import androidx.wear.protolayout.LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER
import androidx.wear.protolayout.LayoutElementBuilders.TEXT_ALIGN_CENTER
import androidx.wear.protolayout.LayoutElementBuilders.Text
import androidx.wear.protolayout.LayoutElementBuilders.VERTICAL_ALIGN_CENTER
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.material.Colors

private
val WIDTH_SIZE = DimensionBuilders.dp(108f)
private val HEIGHT_SIZE = DimensionBuilders.dp(36f)
private val CIRCLE_SIZE = DimensionBuilders.dp(48f)

fun textButton(
    context: Context,
    text: String,
    clickable: ModifiersBuilders.Clickable,
    @ColorRes backgroundColor: Int? = null,
    @ColorRes textColor: Int? = null,
    fontWeight: Int = FONT_WEIGHT_NORMAL,
    maxLines: Int = 1,
    underline: Boolean = false,
    italic: Boolean = false,
    overflow: Int = LayoutElementBuilders.TEXT_OVERFLOW_ELLIPSIZE_END,
    multilineAlignment: Int = TEXT_ALIGN_CENTER
) = Box.Builder().apply {
    setWidth(WIDTH_SIZE)
    setHeight(HEIGHT_SIZE)
    setModifiers(
        ModifiersBuilders.Modifiers.Builder().apply {
            setBackground(
                ModifiersBuilders.Background.Builder().apply {
                    if (backgroundColor != null) {
                        setColor(argb(ContextCompat.getColor(context, backgroundColor)))
                    } else {
                        setColor(argb(Colors.DEFAULT.onPrimary))
                    }
                    setCorner(
                        ModifiersBuilders.Corner.Builder()
                            .setRadius(CIRCLE_SIZE)
                            .build()
                    )
                }.build()
            )
            setClickable(clickable)
        }.build()
    )
    addContent(
        Text.Builder().apply {
            setText(text)
            setFontStyle(
                FontStyle.Builder().apply {
                    if (textColor != null) {
                        setColor(argb(ContextCompat.getColor(context, textColor)))
                    } else {
                        setColor(argb(0xFFFFFFFF.toInt()))
                    }
                    setUnderline(underline)
                    setItalic(italic)
                }.build()
            )
            setMaxLines(maxLines)
            setOverflow(overflow)
            setFontStyle(
                FontStyle.Builder()
                    .setWeight(FONT_WEIGHT_BOLD)
                    .setSize(
                        DimensionBuilders.sp(16f)
                    ).setWeight(fontWeight)
                    .build()
            )
            setMultilineAlignment(multilineAlignment)
            setHorizontalAlignment(HORIZONTAL_ALIGN_CENTER)
            setVerticalAlignment(VERTICAL_ALIGN_CENTER)
        }.build()
    )
}.build()