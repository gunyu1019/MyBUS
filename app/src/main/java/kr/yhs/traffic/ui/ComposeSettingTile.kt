package kr.yhs.traffic.ui

import android.content.SharedPreferences
import androidx.compose.runtime.*
import androidx.core.content.edit
import androidx.wear.tiles.TileService
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import kr.yhs.traffic.SettingTileActivity
import kr.yhs.traffic.TileType
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.ui.components.StepPage
import kr.yhs.traffic.ui.components.AccompanistPager
import kr.yhs.traffic.ui.pages.StationListPage
import kotlin.system.exitProcess


class ComposeSettingTile(private val activity: SettingTileActivity, private val tileType: TileType): BaseCompose() {
    override fun getPreferences(filename: String): SharedPreferences = activity.getPreferences(filename)

    @Composable
    override fun Content() {
        val coroutineScope = rememberCoroutineScope()
        val pagerState = rememberPagerState()
        var station by remember { mutableStateOf<StationInfo?>(null) }
        AccompanistPager(
            scope = coroutineScope,
            pagerState = pagerState,
            pages = listOf({
                StepPage(title = tileType.title, description = "${tileType.title}에 불러올 등록할 정류장를 선택해주세요. 즐겨찾기에 등록되어 있어야합니다.") {
                    coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }
            }, {
                val bookmarkStation = getStationBookmarkList()
                val preferences = getPreferences(tileType.preferenceId)
                StationListPage(
                    "등록할 정류장", bookmarkStation, null, coroutineScope, pagerState.currentPage == 1
                ) {
                    val displayId = if (it.displayId is List<*>) {
                        it.displayId.joinToString(", ")
                    } else it.displayId?.toString() ?: " "
                    station = it
                    preferences.edit {
                        putString("station", it.routeId)
                        putString("station-name", it.name)
                        putInt("station-type", it.type)
                        putString("station-id", it.id)
                        putString("station-ids", it.ids)
                        putFloat("station-posX", it.posX.toFloat())
                        putFloat("station-posY", it.posY.toFloat())
                        putMutableType(
                            this,
                            "station-stationId",
                            it.stationId
                        )
                        putString("station-displayId", displayId)
                        commit()
                    }
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            }, {
                StepPage(title = tileType.title, description = "성공적으로 ${tileType.title}에 ${station?.name}(${station?.displayId})을 등록하였습니다.", "완료") {
                    TileService.getUpdater(activity.baseContext).requestUpdate(tileType.classJava)
                    exitProcess(0)
                }
            }),
            userScrollEnabled = false,
            rotaryScrollEnable = false
        )
    }
}