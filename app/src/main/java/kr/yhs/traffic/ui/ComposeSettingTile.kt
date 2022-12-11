package kr.yhs.traffic.ui

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.*
import androidx.core.content.edit
import androidx.wear.tiles.TileService
import androidx.wear.widget.ConfirmationOverlay
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.yhs.traffic.R
import kr.yhs.traffic.SettingTileActivity
import kr.yhs.traffic.TileType
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
    private val tileType: TileType
) : BaseCompose(activity.client) {
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO

    override fun getPreferences(filename: String): SharedPreferences =
        activity.getPreferences(filename)

    @Composable
    override fun Content() {
        val coroutineScope = rememberCoroutineScope()
        val pagerState = rememberPagerState()
        var station by remember { mutableStateOf<StationInfo?>(null) }
        var route by remember { mutableStateOf<List<StationRoute>>(listOf()) }
        val preferences = getPreferences(tileType.preferenceId)
        AccompanistPager(
            scope = coroutineScope,
            pagerState = pagerState,
            pages = listOf({
                StepPage(
                    title = tileType.title,
                    description = "${tileType.title}에 불러올 등록할 정류장를 선택해주세요. 즐겨찾기에 등록되어 있어야합니다."
                ) {
                    coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }
            }, {
                val bookmarkStation = getStationBookmarkList()
                StationListPage(
                    "등록할 정류장", bookmarkStation, null, coroutineScope, false, pagerState.currentPage == 1
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
                Log.i("stationInfo", station.toString())
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
                        coroutineScope,
                        isLoaded,
                        tileType.maxBusSelect
                    ) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                        preferences.edit {
                            this.putStringSet("busRoute", it.map { it.id }.toSet())
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
                    title = tileType.title,
                    description = "성공적으로 ${tileType.title}에 ${station?.name}(${station?.displayId})을 등록하였습니다.",
                    "완료"
                ) {
                    TileService.getUpdater(activity.baseContext).requestUpdate(tileType.classJava)
                    activity.finish()
                }
            }),
            userScrollEnabled = false,
            rotaryScrollEnable = false
        )
    }
}