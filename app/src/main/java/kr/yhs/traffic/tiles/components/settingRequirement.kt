package kr.yhs.traffic.tiles.components

import android.content.Context
import androidx.wear.protolayout.DimensionBuilders
import androidx.wear.protolayout.LayoutElementBuilders.Column
import androidx.wear.protolayout.LayoutElementBuilders.FONT_WEIGHT_BOLD
import androidx.wear.protolayout.LayoutElementBuilders.FONT_WEIGHT_NORMAL
import androidx.wear.protolayout.LayoutElementBuilders.FontStyle
import androidx.wear.protolayout.LayoutElementBuilders.Image
import androidx.wear.protolayout.LayoutElementBuilders.Row
import androidx.wear.protolayout.LayoutElementBuilders.Spacer
import androidx.wear.protolayout.LayoutElementBuilders.Text
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ModifiersBuilders.Clickable
import kr.yhs.traffic.tiles.ImageId



class SettingRequirement(private val context: Context) {
    private val descriptionPadding = DimensionBuilders.dp(5f)
    private val titlePadding = DimensionBuilders.dp(4f)
    private val imageSize = DimensionBuilders.dp(20f)
    private val titleToDescriptionPadding = DimensionBuilders.dp(20f)
    private val descriptionToButtonPadding = DimensionBuilders.dp(16f)

    fun content(title: String, description: String, onClick: Clickable) = Column.Builder().apply {
        this.addContent(title(title))
        this.addContent(
            Spacer.Builder()
                .setHeight(titleToDescriptionPadding)
                .build()
        )
        this.addContent(description(description))
        this.addContent(
            Spacer.Builder()
                .setHeight(descriptionToButtonPadding)
                .build()
        )
        this.addContent(
            textButton(context, "등록하기", onClick)
        )
    }.build()

    private fun title(text: String) =
        Row.Builder().apply {
            addContent(
                Image.Builder()
                    .setWidth(imageSize)
                    .setHeight(imageSize)
                    .setResourceId(ImageId.Logo.id)
                    .build()
            )
            addContent(
                Spacer.Builder()
                    .setWidth(titlePadding)
                    .build()
            )
            addContent(
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

    private fun description(text: String) = Text.Builder()
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
