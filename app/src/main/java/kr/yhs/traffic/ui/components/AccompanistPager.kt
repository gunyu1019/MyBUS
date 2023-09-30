package kr.yhs.traffic.ui.components

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.material.HorizontalPageIndicator
import androidx.wear.compose.material.PageIndicatorState
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.rotaryinput.onRotaryInputAccumulated
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalWearFoundationApi::class, ExperimentalHorologistApi::class
)
@Composable
fun AccompanistPager(
    scope: CoroutineScope,
    pages: List<@Composable () -> Unit>,
    pagerState: PagerState = rememberPagerState {
        return@rememberPagerState pages.size
    },
    vibrator: Vibrator? = null,
    userScrollEnabled: Boolean? = null,
    rotaryScrollEnable: Boolean = true
) {
    val userScroll = userScrollEnabled ?: rotaryScrollEnable
    val focusRequester = rememberActiveFocusRequester()
    val pageIndicatorState: PageIndicatorState = remember {
        object : PageIndicatorState {
            override val pageOffset: Float
                get() = pagerState.currentPageOffsetFraction
            override val selectedPage: Int
                get() = pagerState.currentPage
            override val pageCount: Int
                get() = pagerState.pageCount
        }
    }

    WearScaffold(pageIndicator = {
        HorizontalPageIndicator(
            pageIndicatorState = pageIndicatorState, modifier = Modifier.padding(bottom = 2.dp)
        )
    }) {
        var modifier = Modifier.fillMaxSize()
        if (rotaryScrollEnable) {
            modifier = modifier
                .onRotaryInputAccumulated {
                    Log.i("RotaryScrollEvent", "RotaryScrollEvent: ${it}%")
                    scope.launch {
                        when {
                            it > 0 && pagerState.currentPage < pages.count() - 1 -> pagerState.animateScrollToPage(
                                pagerState.currentPage + 1
                            )

                            it < 0 && pagerState.currentPage > 0 -> pagerState.animateScrollToPage(
                                pagerState.currentPage - 1
                            )
                        }
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && vibrator != null) {
                        val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                        vibrator.vibrate(effect)
                    }
                }
                .focusRequester(focusRequester)
                .focusable()
        }
        HorizontalPager(
            state = pagerState,
            modifier = modifier,
            userScrollEnabled = userScroll,
        ) { pageIndex: Int ->
            pages[pageIndex].invoke()
        }
    }
}