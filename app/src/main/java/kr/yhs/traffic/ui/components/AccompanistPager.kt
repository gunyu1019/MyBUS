package kr.yhs.traffic.ui.components

import android.util.Log
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.HorizontalPageIndicator
import androidx.wear.compose.material.PageIndicatorState
import androidx.wear.compose.material.Scaffold
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalPagerApi::class, ExperimentalComposeUiApi::class)
@Composable
fun AccompanistPager(
    scope: CoroutineScope,
    pagerState: PagerState = rememberPagerState(),
    pages: List<@Composable () -> Unit>,
    userScrollEnabled: Boolean? = null,
    rotaryScrollEnable: Boolean = true
) {
    val userScroll = userScrollEnabled ?: rotaryScrollEnable
    val focusRequester = remember { FocusRequester() }
    val pageIndicatorState: PageIndicatorState = remember {
        object : PageIndicatorState {
            override val pageOffset: Float
                get() = pagerState.currentPageOffset
            override val selectedPage: Int
                get() = pagerState.currentPage
            override val pageCount: Int
                get() = pagerState.pageCount
        }
    }

    Scaffold(
        pageIndicator = {
            HorizontalPageIndicator(
                pageIndicatorState = pageIndicatorState,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
    ) {
        var modifier = Modifier
            .fillMaxSize()
        if (rotaryScrollEnable) {
            modifier = modifier
                .onRotaryScrollEvent {
                    Log.i("RotaryScrollEvent", "RotaryScrollEvent: ${it.horizontalScrollPixels}%")
                    scope.launch {
                        when {
                            it.horizontalScrollPixels > 0 && pagerState.currentPage < pages.count() - 1 -> pagerState.animateScrollToPage(
                                pagerState.currentPage + 1
                            )
                            it.horizontalScrollPixels < 0 && pagerState.currentPage > 0 -> pagerState.animateScrollToPage(
                                pagerState.currentPage - 1
                            )
                        }
                    }
                    true
                }
                .focusRequester(focusRequester)
                .focusable()
        }
        HorizontalPager(
            count = pages.count(),
            state = pagerState,
            modifier = modifier,
            userScrollEnabled = userScroll,
        ) { pageIndex: Int ->
            pages[pageIndex].invoke()
        }
    }
    if (rotaryScrollEnable) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}