package kr.yhs.traffic.ui.pages

import android.app.RemoteInput
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Scaffold
import androidx.wear.input.RemoteInputIntentHelper
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import kr.yhs.traffic.ui.StationGPS
import kr.yhs.traffic.ui.StationSearch
import kr.yhs.traffic.ui.StationStar


@OptIn(ExperimentalPagerApi::class)
@Composable
fun mainPage(
    pages: List<@Composable () -> Unit>
) {
    val pagerState = rememberPagerState()
    Scaffold {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
        ) {
            HorizontalPager(
                count = pages.count(),
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) { pageIndex: Int ->
                pages[pageIndex]()
            }
            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
                activeColor = Color(0xffffffff),
                inactiveColor = Color(0xff808080),
                indicatorWidth = 4.dp
            )
        }
    }
}