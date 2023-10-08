package kr.yhs.traffic.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import kr.yhs.traffic.utils.nonScaledSp

@Composable
fun ArrivalText(
    mainText: String, subText: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 20.dp, end = 20.dp, top = 5.dp, bottom = 5.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = mainText,
            textAlign = TextAlign.Start,
            color = Color.LightGray,
            style = MaterialTheme.typography.body1
        )
        if (subText != null) {
            Text(
                text = subText,
                textAlign = TextAlign.End,
                color = Color.LightGray,
                style = MaterialTheme.typography.body2,
                fontSize = MaterialTheme.typography.body2.fontSize.nonScaledSp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}