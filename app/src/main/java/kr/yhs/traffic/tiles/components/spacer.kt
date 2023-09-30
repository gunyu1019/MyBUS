package kr.yhs.traffic.tiles.components

import androidx.wear.protolayout.DimensionBuilders
import androidx.wear.protolayout.LayoutElementBuilders.Spacer


fun spacer(
    width: DimensionBuilders.DpProp? = null, height: DimensionBuilders.DpProp? = null
) = Spacer.Builder().apply {
    if (width != null) setWidth(width)
    if (height != null) setHeight(height)
}.build()