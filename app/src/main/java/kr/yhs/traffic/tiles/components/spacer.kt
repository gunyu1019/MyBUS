package kr.yhs.traffic.tiles.components

import androidx.wear.tiles.DimensionBuilders
import androidx.wear.tiles.DimensionBuilders.SpProp
import androidx.wear.tiles.DimensionBuilders.sp
import androidx.wear.tiles.LayoutElementBuilders
import androidx.wear.tiles.LayoutElementBuilders.*
import kr.yhs.traffic.models.StationInfo


fun spacer(
    width: DimensionBuilders.DpProp? = null,
    height: DimensionBuilders.DpProp? = null
) = Spacer.Builder().apply {
    if (width != null) setWidth(width)
    if (height != null) setHeight(height)
}.build()