package kr.yhs.traffic.tiles.components

import androidx.wear.tiles.ActionBuilders
import androidx.wear.tiles.ModifiersBuilders
import androidx.wear.tiles.TileService
import kr.yhs.traffic.SettingTileActivity

fun clickable(classPackage: TileService) = ModifiersBuilders.Clickable.Builder()
    .setId(classPackage::class.java.name) // TileType.ArrivingSoonTile.id
    .setOnClick(
        ActionBuilders.LaunchAction.Builder()
            .setAndroidActivity(
                ActionBuilders.AndroidActivity.Builder()
                    .setClassName(SettingTileActivity::class.java.name)
                    .setPackageName(classPackage.packageName)
                    .build()
            ).build()
    ).build()