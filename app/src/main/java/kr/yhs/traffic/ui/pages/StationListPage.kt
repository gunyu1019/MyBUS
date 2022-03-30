package kr.yhs.traffic.ui

import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import kr.yhs.traffic.R
import kr.yhs.traffic.models.StationInfo

@Composable
fun StationListPage(
    title: String,
    stationList: List<StationInfo>,
    location: Location?,
    stationCallback: (StationInfo) -> Unit
) {
    val scalingLazyListState: ScalingLazyListState = rememberScalingLazyListState()
    ScalingLazyColumn(
        state = scalingLazyListState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 32.dp, bottom = 32.dp)
                    .fillMaxWidth()
            )
        }
        items(stationList) { station ->
            var displayId = station.displayId
            if (displayId is List<*>) {
                displayId = displayId.joinToString(", ")
            } else if (displayId == null) {
                displayId = " "
            }
            var distance = -1
            if (location != null) {
                val result = FloatArray(1)
                Location.distanceBetween(
                    location.latitude,
                    location.longitude,
                    station.posY,
                    station.posX,
                    result
                )
                distance = result[0].toInt()
            }
            StationShortInfo(
                station.name,
                displayId as String,
                (distance < 250)
            ) {
                stationCallback(station)
            }
        }
    }
}


@Composable
fun StationShortInfo(
    stationName: String,
    stationId: String,
    near: Boolean = false,
    call: () -> Unit
) {
    Chip(
        modifier = Modifier
            .height(54.dp)
            .padding(top = 2.dp, bottom = 2.dp),
        label = {
            Text(
                text = stationName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        secondaryLabel = {
            var secondaryText = stationId
            if (near)
                secondaryText = "${stationId}ㅣ이 근처에 있음."
            Text(
                text = secondaryText,
                maxLines = 1
            )
        },
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_directions_bus),
                contentDescription = "bus icon",
                modifier = Modifier
                    .size(24.dp)
                    .wrapContentSize(align = Alignment.Center),
            )
        },
        onClick = call
    )
}