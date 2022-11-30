package kr.yhs.traffic.tiles

import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.tiles.DimensionBuilders
import androidx.wear.tiles.LayoutElementBuilders.*
import com.google.android.horologist.tiles.images.drawableResToImageResource


class SettingRequirement(private val builder: Column.Builder) {
    fun content(title: String, description: String) {
        builder.addContent(title(title))
        builder.addContent(
            Spacer.Builder()
                .setHeight(
                    DimensionBuilders.DpProp.Builder()
                        .setValue(20f)
                        .build()
                )
                .build()
        )
        builder.addContent(description(description))
    }

    private fun title (text: String) =
        Row.Builder().apply {
            val imageSize = DimensionBuilders.DpProp.Builder()
                    .setValue(9f)
                    .build()
            addContent  (
                Image.Builder()
                    .setWidth(imageSize)
                    .setHeight(imageSize)
                    .setResourceId(ImageId.Logo.id)
                    .build()
            )
            addContent (
                Text.Builder()
                    .setText(text)
                    .setFontStyle(
                        FontStyle.Builder()
                            .setWeight(FONT_WEIGHT_BOLD)
                            .setSize(
                                DimensionBuilders.SpProp.Builder()
                                    .setValue(18f)
                                    .build()
                            ).build()
                    ).setMaxLines(1)
                    .build()
            )
        }.build()

    private fun description (text: String) = Text.Builder()
        .setText(text)
        .setFontStyle(
            FontStyle.Builder()
                .setWeight(FONT_WEIGHT_NORMAL)
                .setSize(
                    DimensionBuilders.SpProp.Builder()
                        .setValue(14f)
                        .build()
                ).build()
        ).setMaxLines(3).build()
}
