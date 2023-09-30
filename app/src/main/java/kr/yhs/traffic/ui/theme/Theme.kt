package kr.yhs.traffic.ui

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.MaterialTheme
import kr.yhs.traffic.ui.theme.typography


@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        typography = typography,
        content = content
    )
}
