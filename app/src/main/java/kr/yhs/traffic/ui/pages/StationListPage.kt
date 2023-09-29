package kr.yhs.traffic.ui.pages

import android.location.Location
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onPreRotaryScrollEvent
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.items
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kr.yhs.traffic.R
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.ui.components.LoadingProgressIndicator
import kr.yhs.traffic.ui.components.WearScaffold
import kotlin.math.atan2
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun StationListPage(
    title: String,
    stationList: List<StationInfo>,
    location: Location?,
    coroutineScope: CoroutineScope,
    isLoading: Boolean = false,
    rotaryScrollEnable: Boolean = true,
    stationCallback: (StationInfo) -> Unit
) {
    val scalingLazyListState: ScalingLazyListState = rememberScalingLazyListState()
    val focusRequester = remember { FocusRequester() }
    var modifier = Modifier.fillMaxSize()
    if (rotaryScrollEnable) {
        modifier = modifier
            .onRotaryScrollEvent {
                coroutineScope.launch {
                    scalingLazyListState.animateScrollBy(it.verticalScrollPixels)
                }
                true
            }
            .focusRequester(focusRequester)
            .focusable()
    }
    WearScaffold(
        positionIndicator = {
            PositionIndicator(
                scalingLazyListState = scalingLazyListState
            )
        }
    ) {
        ScalingLazyColumn(
            state = scalingLazyListState,
            modifier = modifier,
            contentPadding = PaddingValues(10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            item {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
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
                        direction = (
                                atan2(
                                    location.latitude - station.posY,
                                    station.posX - location.longitude
                                ) * 180 / Math.PI
                        ).roundToInt() - location.bearing.roundToInt()
                    }
                    StationShortInfo(
                        station.name,
                        displayId as String,
                        distance, direction
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
    if (rotaryScrollEnable) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}


@Composable
fun StationEmpty() {
    val context = LocalContext.current
    return Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_baseline_search_off),
            contentDescription = "Not Found",
            modifier = Modifier.height(30.dp)
        )
        Text(
            text = context.getString(R.string.result_not_found),
            fontSize = 14.sp
        )
    }
}


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
            .height(54.dp)
            .fillMaxWidth()
            .padding(top = 2.dp, bottom = 2.dp),
        colors = ChipDefaults.chipColors(
            backgroundColor = Color.White,
            contentColor = Color.Black
        ),
        label = {
            Text(
                text = stationName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        secondaryLabel = {
            var secondaryText = stationId
            if (distance in 1..500)
                secondaryText += " | ${distance}m"
            Text(
                text = secondaryText,
                maxLines = 1
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
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_arrow),
                        contentDescription = "Rotation",
                        modifier = Modifier
                            .size(14.dp)
                            .wrapContentSize(align = Alignment.BottomEnd)
                            .align(Alignment.BottomEnd)
                            .rotate(direction.toFloat())
                            .background(Color.White)
                            .border(
                                width = 1.dp,
                                brush = Brush.radialGradient(
                                    colors = listOf(Color.Black, Color.LightGray)
                                ),
                                shape = CircleShape
                            )
                    )
                }
            }
        },
        onClick = call
    )
}