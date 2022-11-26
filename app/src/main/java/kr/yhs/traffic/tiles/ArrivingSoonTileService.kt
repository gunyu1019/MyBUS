package kr.yhs.traffic.tiles

import androidx.wear.tiles.*
import androidx.wear.tiles.DimensionBuilders.expand
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.guava.future

class ArrivingSoonTileService : BaseTileService() {
    override val RESOURCES_VERSION = "1"
}