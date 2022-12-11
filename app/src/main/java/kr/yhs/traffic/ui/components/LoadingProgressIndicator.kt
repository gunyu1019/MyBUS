package kr.yhs.traffic.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*


@Composable
fun LoadingProgressIndicator(modifier: Modifier = Modifier)
    = CircularProgressIndicator(
        modifier = modifier,
        indicatorColor = MaterialTheme.colors.secondary,
        trackColor = MaterialTheme.colors.onBackground.copy(alpha = 0.1f),
        strokeWidth = 4.dp
    )