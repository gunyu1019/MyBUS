package kr.yhs.traffic.tiles.components

import androidx.wear.tiles.ActionBuilders
import androidx.wear.tiles.ModifiersBuilders
import androidx.wear.tiles.TileService

fun clickable(className: String, classPackage: TileService) = clickable(className, classPackage, classPackage::class.java.name)

fun clickable(className: String, classPackage: TileService, id: String) = ModifiersBuilders.Clickable.Builder()
    .setId(id)
    .setOnClick(
        ActionBuilders.LaunchAction.Builder()
            .setAndroidActivity(
                ActionBuilders.AndroidActivity.Builder()
                    .setClassName(className)
                    .setPackageName(classPackage.packageName)
                    .build()
            ).build()
    ).build()