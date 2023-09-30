package kr.yhs.traffic.ui

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.*
import androidx.core.content.edit
import androidx.wear.tiles.TileService
import androidx.wear.widget.ConfirmationOverlay
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import kr.yhs.traffic.R
import kr.yhs.traffic.SettingTileActivity
import kr.yhs.traffic.StationTileType
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.models.StationRoute
import kr.yhs.traffic.ui.components.AccompanistPager
import kr.yhs.traffic.ui.components.StepPage
import kr.yhs.traffic.ui.pages.RouteSelection
import kr.yhs.traffic.ui.pages.StationListPage
import retrofit2.HttpException
import java.net.SocketTimeoutException


class ComposeSettingTile(
    private val activity: SettingTileActivity,
    private val stationTileType: StationTileType
) : BaseCompose(activity.client) {

    override fun getPreferences(filename: String): SharedPreferences = activity.getPreferences(filename)

    @Composable
    override fun Content() {
        val coroutineScope = rememberCoroutineScope()
        val pagerState = rememberPagerState()
        var station by remember { mutableStateOf<StationInfo?>(null) }
        var route by remember { mutableStateOf<List<StationRoute>>(listOf()) }
        val preferences = getPreferences(stationTileType.preferenceId)
        AccompanistPager(
            scope = coroutineScope,
            pagerState = pagerState,
            pages = listOf({
                StepPage(
                    this@ComposeSettingTile.activity,
                    title = stationTileType.title,
                    description = activity.getString(R.string.station_tile_setting_first_description, stationTileType.title),
                    enableStopButton = true
                ) {
                    coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }
            }, {
                val bookmarkStation = getStationBookmarkList()
                StationListPage(
                    activity.getString(R.string.station_tile_setting_station_list_title),
                    bookmarkStation,
                    null,
                    false,
                    pagerState.currentPage == 1
                ) {
                    station = it
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            }, {
                if (activity.client == null) {
                    ConfirmationOverlay()
                        .setType(ConfirmationOverlay.FAILURE_ANIMATION)
                        .setMessage(activity.getText(R.string.station_not_found))
                        .showOn(activity)
                    activity.finish()
                    return@listOf
                }
                var routeInfo by remember {
                    mutableStateOf(listOf<StationRoute>())
                }
                var isLoaded by remember { mutableStateOf(false) }
                // Log.i("stationInfo", station.toString())
                if (station != null) {
                    LaunchedEffect(true) {
                        try {
                            routeInfo = getRoute(
                                defaultDispatcher,
                                station?.routeId!!,
                                station?.type!!
                            )
                            isLoaded = true
                        } catch (e: Exception) {
                            when (e) {
                                is SocketTimeoutException, is HttpException -> {
                                    ConfirmationOverlay()
                                        .setType(ConfirmationOverlay.FAILURE_ANIMATION)
                                        .setMessage(activity.getText(R.string.timeout))
                                        .showOn(activity)
                                    activity.finish()
                                    return@LaunchedEffect
                                }
                                else -> throw e
                            }
                        }
                    }

                    RouteSelection(this.activity).Content(
                        station!!,
                        routeInfo,
                        isLoaded,
                        pagerState.currentPage == 2,
                        stationTileType.maxBusSelect
                    ) { stationRoute ->
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                        route = stationRoute
                        preferences.edit {
                            val busIds = mutableSetOf<String>()
                            stationRoute.forEach {
                                this.putString("${it.id}-name", it.name)
                                this.putInt("${it.id}-type", it.type)
                                busIds.add(it.id)
                            }
                            this.putStringSet("busRoute", busIds)
                        }
                    }
                }
            }, {
                if (station != null && route.isNotEmpty()) {
                    val privateStation = station!!
                    val displayId = if (privateStation.displayId is List<*>) {
                        privateStation.displayId.joinToString(", ")
                    } else privateStation.displayId?.toString() ?: " "
                    preferences.edit {
                        putString("station", privateStation.routeId)
                        putString("station-name", privateStation.name)
                        putInt("station-type", privateStation.type)
                        putString("station-id", privateStation.id)
                        putString("station-ids", privateStation.ids)
                        putFloat("station-posX", privateStation.posX.toFloat())
                        putFloat("station-posY", privateStation.posY.toFloat())
                        putMutableType(
                            this,
                            "station-stationId",
                            privateStation.stationId
                        )
                        putString("station-displayId", displayId)
                        commit()
                    }
                }
                StepPage(
                    this@ComposeSettingTile.activity,
                    title = stationTileType.title,
                    description = activity.getString(R.string.station_tile_setting_success_description, stationTileType.title, station?.name, station?.displayId),
                    activity.getString(R.string.station_tile_setting_success_button), false
                ) {
                    Log.i("TileService", "${stationTileType.preferenceId} ${stationTileType.classJava}")
                    TileService.getUpdater(activity.baseContext).requestUpdate(stationTileType.classJava)
                    activity.finish()
                }
            }),
            userScrollEnabled = false,
            rotaryScrollEnable = false
        )
    }
}