package kr.yhs.traffic.tiles.components

import android.content.Context
import androidx.wear.tiles.DimensionBuilders
import androidx.wear.tiles.LayoutElementBuilders.*
import androidx.wear.tiles.ModifiersBuilders
import androidx.wear.tiles.ModifiersBuilders.Clickable
import kr.yhs.traffic.tiles.ImageId
import kr.yhs.traffic.tiles.textButton


class SettingRequirement(private val builder: Column.Builder, private val context: Context) {
    private val descriptionPadding = DimensionBuilders.dp(5f)
    private val titlePadding = DimensionBuilders.dp(4f)
    private val imageSize = DimensionBuilders.dp(20f)
    private val titleToDescriptionPadding = DimensionBuilders.dp(20f)
    private val descriptionToButtonPadding = DimensionBuilders.dp(16f)

    fun content(title: String, description: String, onClick: Clickable) {
        builder.addContent(title(title))
        builder.addContent(
            Spacer.Builder()
                .setHeight(titleToDescriptionPadding)
                .build()
        )
        builder.addContent(description(description))
        builder.addContent(
            Spacer.Builder()
                .setHeight(descriptionToButtonPadding)
                .build()
        )
        builder.addContent(
            textButton(context, "등록하기", onClick)
        )
    }

    private fun title (text: String) =
        Row.Builder().apply {
            addContent  (
                Image.Builder()
                    .setWidth(imageSize)
                    .setHeight(imageSize)
                    .setResourceId(ImageId.Logo.id)
                    .build()
            )
            addContent (
                Spacer.Builder()
                    .setWidth(titlePadding)
                    .build()
            )
            addContent (
                Text.Builder()
                    .setText(text)
                    .setFontStyle(
                        FontStyle.Builder()
                            .setWeight(FONT_WEIGHT_BOLD)
                            .setSize(
                                DimensionBuilders.sp(18f)
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
                    DimensionBuilders.sp(12f)
                ).build()
        ).setMaxLines(3)
        .setModifiers(
            ModifiersBuilders.Modifiers.Builder()
                .setPadding(
                    ModifiersBuilders.Padding.Builder()
                        .setStart(descriptionPadding)
                        .setEnd(descriptionPadding)
                        .build()
                ).build()
        )
        .build()
}
