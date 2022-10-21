package kr.yhs.traffic.ui.pages

import android.util.Log
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Scaffold
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalPagerApi::class, ExperimentalComposeUiApi::class)
@Composable
fun MainPage(
    scope: CoroutineScope,
    pages: List<@Composable () -> Unit>
) {
    val pagerState = rememberPagerState()
    val focusRequester = remember { FocusRequester() }
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
                    .onRotaryScrollEvent {
                        Log.i("RotaryScrollEvent", "RotaryScrollEvent: ${it.horizontalScrollPixels}%")
                        scope.launch {
                            when {
                                it.horizontalScrollPixels > 0 && pagerState.currentPage < pages.count() - 1 -> pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                it.horizontalScrollPixels < 0 && pagerState.currentPage > 0 -> pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                        true
                    }
                    .focusRequester(focusRequester)
                    .focusable(),
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
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}