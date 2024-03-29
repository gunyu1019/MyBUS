package kr.yhs.traffic.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import kr.yhs.traffic.R


@Composable
fun StationShortInfo(
    stationName: String,
    stationId: String,
    distance: Int = -1,
    direction: Int = -1,
    call: () -> Unit
) {
    Chip(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp, bottom = 2.dp),
        colors = ChipDefaults.chipColors(
            backgroundColor = Color.White, contentColor = Color.Black
        ),
        label = {
            Text(
                text = stationName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.body1
            )
        },
        secondaryLabel = {
            var secondaryText = stationId
            if (distance in 1..500) secondaryText += " | ${distance}m"
            Text(
                text = secondaryText, maxLines = 1, style = MaterialTheme.typography.body2
            )
        },
        icon = {
            Box {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_directions_bus),
                    contentDescription = "bus icon",
                    modifier = Modifier
                        .size(24.dp)
                        .wrapContentSize(align = Alignment.Center)
                        .align(Alignment.Center)
                )
                if (distance in 1..500) {
                    RotationArrow(
                        modifier = Modifier.align(Alignment.BottomEnd),
                        direction.toFloat()
                    )
                }
            }
        },
        onClick = call
    )
}