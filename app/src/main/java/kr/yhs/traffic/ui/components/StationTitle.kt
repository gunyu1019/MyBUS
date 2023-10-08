package kr.yhs.traffic.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text


@Composable
fun StationTitle(
    title: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 20.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            modifier = Modifier.align(Alignment.CenterVertically),
            color = Color.White,
            style = MaterialTheme.typography.display2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}