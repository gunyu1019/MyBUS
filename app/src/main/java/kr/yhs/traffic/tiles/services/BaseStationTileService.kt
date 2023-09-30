package kr.yhs.traffic.tiles.services

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.DeviceParametersBuilders
import androidx.wear.protolayout.DimensionBuilders.expand
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.tiles.EventBuilders
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import com.google.android.horologist.tiles.images.drawableResToImageResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.yhs.traffic.R
import kr.yhs.traffic.SettingTileActivity
import kr.yhs.traffic.StationInfoActivity
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.models.StationRoute
import kr.yhs.traffic.tiles.CoroutinesTileService
import kr.yhs.traffic.tiles.ImageId
import kr.yhs.traffic.tiles.components.SettingRequirement
import kr.yhs.traffic.tiles.components.clickable
import kr.yhs.traffic.utils.ClientBuilder
import kr.yhs.traffic.utils.StationPreferences
import kr.yhs.traffic.utils.TrafficClient
import kr.yhs.traffic.utils.timeFormatter
import retrofit2.await

abstract class BaseStationTileService(
    private val preferencesId: String, private val resourcesVersion: String
) : CoroutinesTileService(), StationPreferences {
    private lateinit var preferences: SharedPreferences
    private var client: TrafficClient? = null
    private val updateId = "UPDATE_BUS_ROUTE"

    var updateClickable = ModifiersBuilders.Clickable.Builder().setOnClick(
            ActionBuilders.LoadAction.Builder().build()
        ).setId(updateId).build()
    val stationClickable
        get() = clickable(StationInfoActivity::class.java.name, this@BaseStationTileService)

    override fun onCreate() {
        super.onCreate()
        preferences = getPreferences(preferencesId)
        val clientBuilder = ClientBuilder()
        clientBuilder.httpClient = clientBuilder.httpClientBuild()

        val retrofit = clientBuilder.build()
        client = retrofit.create(TrafficClient::class.java)
    }

    override suspend fun tileRequest(requestParams: RequestBuilders.TileRequest): TileBuilders.Tile =
        TileBuilders.Tile.Builder().apply {
            setResourcesVersion(resourcesVersion)
            // setFreshnessIntervalMillis(1000)
            setTileTimeline(
                TimelineBuilders.Timeline.Builder().addTimelineEntry(
                        TimelineBuilders.TimelineEntry.Builder().setLayout(
                                LayoutElementBuilders.Layout.Builder()
                                    .setRoot(this@BaseStationTileService.tileLayout(requestParams))
                                    .build()
                            ).build()
                    ).build()
            )
        }.build()

    override suspend fun resourcesRequest(requestParams: RequestBuilders.ResourcesRequest): ResourceBuilders.Resources =
        ResourceBuilders.Resources.Builder().apply {
            setVersion(resourcesVersion)
            addIdToImageMapping(
                ImageId.Logo.id, drawableResToImageResource(R.mipmap.ic_launcher)
            )
        }.build()

    private suspend fun tileLayout(requestParams: RequestBuilders.TileRequest): LayoutElementBuilders.LayoutElement {
        return LayoutElementBuilders.Box.Builder().apply {
            setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
            setWidth(expand())
            setHeight(expand())
            if (!preferences.contains("station")) {
                addContent(
                    SettingRequirement(this@BaseStationTileService.baseContext).content(
                        getString(R.string.station_tile_service_title),
                        getString(R.string.station_tile_service_description),
                        clickable(SettingTileActivity::class.java.name, this@BaseStationTileService)
                    )
                )
            } else {
                val stationInfo = getStationInfo(preferences)
                val busRouteId = preferences.getStringSet("busRoute", setOf())
                val defaultBusRouteInfo = busRouteId?.map {
                    StationRoute(
                        it,
                        preferences.getString("$it-name", null)
                            ?: getString(R.string.arrival_text_unknown),
                        preferences.getInt("$it-type", 0),
                        isEnd = false,
                        isWait = false,
                        arrivalInfo = listOf()
                    )
                }
                val routeInfo =
                    if (requestParams.currentState.lastClickableId != updateId) defaultBusRouteInfo else {
                        try {
                            getRoute(Dispatchers.IO, stationInfo)?.filter {
                                busRouteId!!.contains(it.id)
                            } ?: defaultBusRouteInfo
                        } catch (e: Exception) {
                            defaultBusRouteInfo
                        }
                    }
                addContent(
                    stationTileLayout(requestParams.deviceConfiguration, stationInfo, routeInfo)
                )
            }
        }.build()
    }

    private suspend fun getRoute(
        dispatcher: CoroutineDispatcher, stationInfo: StationInfo
    ) = withContext(dispatcher) {
        client?.getRoute(
            stationInfo.routeId, stationInfo.type
        )?.await()
    }

    abstract suspend fun stationTileLayout(
        deviceParameters: DeviceParametersBuilders.DeviceParameters,
        stationInfo: StationInfo,
        routeInfo: List<StationRoute>?
    ): LayoutElementBuilders.LayoutElement

    override fun onTileRemoveEvent(requestParams: EventBuilders.TileRemoveEvent) {
        super.onTileRemoveEvent(requestParams)
        val busIds = preferences.getStringSet("busRoute", setOf())
        preferences.edit {
            remove("station")
            listOf("name", "id", "ids", "posX", "posY", "displayId", "stationId", "type").forEach {
                remove("station-$it")
            }
            busIds?.forEach {
                remove("${it}-type")
                remove("${it}-name")
            }
            remove("busRoute")
            commit()
        }
    }

    fun getArrivalText(routeInfo: StationRoute): String {
        return when {
            routeInfo.isEnd == true -> this.getString(R.string.arrival_text_closed)
            routeInfo.isWait == true -> this.getString(R.string.arrival_text_wait)
            routeInfo.arrivalInfo.isNotEmpty() -> {
                val arrivalInfo = routeInfo.arrivalInfo[0]
                if (arrivalInfo.prevCount == 0 && arrivalInfo.time <= 180 || arrivalInfo.time <= 60) this@BaseStationTileService.getString(
                    R.string.arrival_text_soon
                )
                else timeFormatter(this, routeInfo.arrivalInfo[0].time, false)
            }

            else -> "-분"
        }
    }
}
