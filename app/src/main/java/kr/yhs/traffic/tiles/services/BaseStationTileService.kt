package kr.yhs.traffic.tiles.services

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.wear.tiles.*
import androidx.wear.tiles.DimensionBuilders.expand
import androidx.wear.tiles.TimelineBuilders.TimelineEntry
import com.google.android.horologist.tiles.images.drawableResToImageResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.models.StationRoute
import kr.yhs.traffic.tiles.CoroutinesTileService
import kr.yhs.traffic.tiles.ImageId
import kr.yhs.traffic.tiles.components.SettingRequirement
import kr.yhs.traffic.tiles.components.clickable
import kr.yhs.traffic.utils.ClientBuilder
import kr.yhs.traffic.utils.MutableTypeSharedPreferences
import kr.yhs.traffic.utils.TrafficClient
import retrofit2.await

abstract class BaseStationTileService(
    private val preferencesId: String,
    private val resourcesVersion: String
) : CoroutinesTileService(), MutableTypeSharedPreferences {
    private lateinit var preferences: SharedPreferences
    private var client: TrafficClient? = null
    private val updateId = "UPDATE_BUS_ROUTE"

    var updateClickable = ModifiersBuilders.Clickable.Builder()
        .setOnClick(
            ActionBuilders.LoadAction.Builder().build()
        ).setId(updateId).build()

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
            setTimeline(
                TimelineBuilders.Timeline.Builder()
                    .addTimelineEntry(
                        TimelineEntry.Builder()
                            .setLayout(
                                LayoutElementBuilders.Layout.Builder()
                                    .setRoot(this@BaseStationTileService.tileLayout(requestParams))
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
        }.build()

    override suspend fun resourcesRequest(requestParams: RequestBuilders.ResourcesRequest): ResourceBuilders.Resources =
        ResourceBuilders.Resources.Builder().apply {
            setVersion(resourcesVersion)
            addIdToImageMapping(
                ImageId.Logo.id,
                drawableResToImageResource(kr.yhs.traffic.R.mipmap.ic_launcher)
            )
        }.build()

    private suspend fun tileLayout(requestParams: RequestBuilders.TileRequest): LayoutElementBuilders.LayoutElement {
        return LayoutElementBuilders.Box.Builder().apply {
            setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
            setWidth(expand())
            setHeight(expand())
            if (!preferences.contains("station")) {
                addContent (
                    SettingRequirement(this@BaseStationTileService.baseContext).content(
                        "도착 예정 버스", "도착할 버스 정보를 불러오기 위한 버스 정류장을 등록해주세요.",
                        clickable(this@BaseStationTileService)
                    )
                )
            } else {
                val stationInfo = getStationInfo()
                val busRouteId = preferences.getStringSet("busRoute", setOf())
                val routeInfo = if (requestParams.state?.lastClickableId != updateId) busRouteId?.map {
                    StationRoute(
                        it,
                        preferences.getString("$it-name", null) ?: "알 수 없음",
                        preferences.getInt("$it-type", 0),
                        isEnd = false, isWait = false, arrivalInfo = listOf()
                    )
                } else {
                    client?.getRoute(
                        stationInfo.routeId, stationInfo.type
                    )?.await()?.filter {
                        busRouteId!!.contains(it.id)
                    }
                }
                addContent(
                    stationTileLayout(requestParams.deviceParameters!!, stationInfo, routeInfo)
                )
            }
        }.build()
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

    private fun getStationInfo() = StationInfo(
        this.preferences.getString("station-name", "알 수 없음") ?: "알 수 없음",
        this.preferences.getString("station-id", null) ?: "-2",
        this.preferences.getString("station-ids", null),
        this.preferences.getFloat("station-posX", 0.0F).toDouble(),
        this.preferences.getFloat("station-posY", 0.0F).toDouble(),
        this.preferences.getString("station-displayId", null) ?: "0",
        getMutableType(this.preferences, "station-stationId", null) ?: "0",
        this.preferences.getInt("station-type", 0),
    )
}
