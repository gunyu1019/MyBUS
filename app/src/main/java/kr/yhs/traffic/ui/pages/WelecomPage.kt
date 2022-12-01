package kr.yhs.traffic.ui.pages

import android.content.SharedPreferences
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kr.yhs.traffic.ui.components.NextButton


class WelcomePage(private val scope: CoroutineScope, private val preferences: SharedPreferences, private val onSettingEnd: () -> Unit) {
    @OptIn(ExperimentalPagerApi::class)
    @Composable
    fun Content() {
        val pagerState = rememberPagerState()
        AccompanistPager(
            pages = listOf({
                this.WelcomeMessage("환영합니다!", "공유킥보드 위치를 알아보세요!") {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }
            }, {
                this.WelcomeMessage("설정 완료", "전동킥보드 위치를 찾아볼까요?", "완료", this@WelcomePage.onSettingEnd)
            }), scope = scope, pagerState = pagerState
        )
    }

    @Composable
    fun WelcomeMessage(
        title: String,
        description: String,
        buttonText: String = "다음",
        nextButtonCallback: () -> Unit
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 3.dp),
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 3.dp, bottom = 30.dp),
                text = description,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            NextButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                buttonText,
                nextButtonCallback
            )
        }
    }
}