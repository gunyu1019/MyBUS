package kr.yhs.traffic.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text


@Composable
fun LoadingProgressIndicator(modifier: Modifier = Modifier) = Row(
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically
) {
    CircularProgressIndicator(
        modifier = modifier,
        indicatorColor = MaterialTheme.colors.secondary,
        trackColor = MaterialTheme.colors.onBackground.copy(alpha = 0.1f),
        strokeWidth = 4.dp
    )
    Text(
        text = "불러오는 중",
        color = Color.LightGray,
        style = MaterialTheme.typography.body1,
        modifier = Modifier.padding(start = 6.dp)
    )
}