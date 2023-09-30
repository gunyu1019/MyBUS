package kr.yhs.traffic.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText

@Composable
fun WearScaffold(
    modifier: Modifier = Modifier,
    vignette: @Composable (() -> Unit)? = null,
    positionIndicator: @Composable (() -> Unit)? = null,
    pageIndicator: @Composable (() -> Unit)? = null,
    timeText: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) = Scaffold(
    modifier, vignette, positionIndicator, pageIndicator, timeText ?: {
        TimeText()
    }, content
)