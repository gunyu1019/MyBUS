package kr.yhs.traffic.ui.pages

import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.*
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.rotaryinput.rotaryWithSnap
import com.google.android.horologist.compose.rotaryinput.toRotaryScrollAdapter
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.ui.components.LoadingProgressIndicator
import kr.yhs.traffic.ui.components.StationEmpty
import kr.yhs.traffic.ui.components.StationShortInfo
import kr.yhs.traffic.ui.components.WearScaffold
import kotlin.math.atan2
import kotlin.math.roundToInt

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun StationListPage(
    title: String,
    stationList: List<StationInfo>,
    location: Location?,
    isLoading: Boolean = false,
    rotaryScrollEnable: Boolean = true,
    stationCallback: (StationInfo) -> Unit
) {
    val scalingLazyListState: ScalingLazyListState = rememberScalingLazyListState()
    var modifier = Modifier.fillMaxSize()

    if (rotaryScrollEnable) {
        modifier = modifier.rotaryWithSnap(
            scalingLazyListState.toRotaryScrollAdapter()
        )
    }
    WearScaffold(positionIndicator = {
        PositionIndicator(
            scalingLazyListState = scalingLazyListState
        )
    }, timeText = {
        TimeText(
            modifier = Modifier.scrollAway(scalingLazyListState)
        )
    }) {
        ScalingLazyColumn(
            state = scalingLazyListState,
            modifier = modifier,
            contentPadding = PaddingValues(10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            item {
                Text(
                    text = title,
                    style = MaterialTheme.typography.display3,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 32.dp, bottom = 16.dp)
                        .fillMaxWidth()
                )
            }
            if (stationList.isNotEmpty()) {
                items(stationList) { station ->
                    var displayId = station.displayId
                    if (displayId is List<*>) {
                        displayId = displayId.joinToString(", ")
                    } else if (displayId == null) {
                        displayId = " "
                    }
                    var distance = -1
                    var direction = -1
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
                        direction = (atan2(
                            location.latitude - station.posY, station.posX - location.longitude
                        ) * 180 / Math.PI).roundToInt() - location.bearing.roundToInt()
                    }
                    StationShortInfo(
                        station.name, displayId as String, distance, direction
                    ) {
                        stationCallback(station)
                    }
                }
            } else if (isLoading) {
                item {
                    LoadingProgressIndicator()
                }
            } else {
                item {
                    StationEmpty()
                }
            }
        }
    }
}