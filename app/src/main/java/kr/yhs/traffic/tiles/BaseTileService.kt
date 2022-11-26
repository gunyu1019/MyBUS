package kr.yhs.traffic.tiles

import androidx.wear.tiles.*
import androidx.wear.tiles.DimensionBuilders.expand
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.guava.future

open class BaseTileService : TileService() {
    open val RESOURCES_VERSION = "1"
    val serviceScope = CoroutineScope(Dispatchers.IO)
    lateinit var deviceParameters: DeviceParametersBuilders.DeviceParameters

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<TileBuilders.Tile> =
        serviceScope.future {
            TileBuilders.Tile.Builder().apply {
                setResourcesVersion(RESOURCES_VERSION)
                setFreshnessIntervalMillis(3600000)
            }.build()
        }

    override fun onResourcesRequest(requestParams: RequestBuilders.ResourcesRequest): ListenableFuture<ResourceBuilders.Resources> =
        serviceScope.future {
            ResourceBuilders.Resources.Builder().apply {
                setVersion(RESOURCES_VERSION)
            }.build()
        }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun getLayout(contents: List<LayoutElementBuilders.LayoutElement>) =
        LayoutElementBuilders.Box.Builder().apply {
            setWidth(expand())
            setHeight(expand())
            contents.forEach {
                addContent (it)
            }
        }.build()
}