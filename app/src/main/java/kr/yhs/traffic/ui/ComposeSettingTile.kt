package kr.yhs.traffic.ui

import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import kr.yhs.traffic.SettingTileActivity
import kr.yhs.traffic.TileType
import kr.yhs.traffic.ui.components.StepPage
import kr.yhs.traffic.ui.components.AccompanistPager
import kr.yhs.traffic.ui.pages.StationListPage
import kotlin.system.exitProcess


class ComposeSettingTile(private val activity: SettingTileActivity, private val tileType: TileType): BaseCompose(activity) {
    override fun getPreferences(filename: String): SharedPreferences = activity.getPreferences(filename)

    @Composable
    override fun Content() {
        val coroutineScope = rememberCoroutineScope()
        val pagerState = rememberPagerState()
        AccompanistPager(
            scope = coroutineScope,
            pagerState = pagerState,
            pages = listOf({
                StepPage(title = tileType.title, description = "\"다음\"버튼을 클릭하여 등록할 정류장를 선택해주세요.\n즐겨찾기에 등록되어 있어야합니다.") {
                    coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }
            }, {
                val bookmarkStation = getStationBookmarkList()
                StationListPage(
                    "등록할 정류장", bookmarkStation, null, coroutineScope
                ) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            }, {
                StepPage(title = "", description = "") {
                    exitProcess(1)
                }
            }),
            userScrollEnabled = false,
            rotaryScrollEnable = false
        )
    }
}