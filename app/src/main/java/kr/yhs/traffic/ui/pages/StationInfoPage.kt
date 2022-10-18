package kr.yhs.traffic.ui.pages

import android.util.Log
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kr.yhs.traffic.R
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.models.StationRoute
import kr.yhs.traffic.module.StopWatch
import kr.yhs.traffic.ui.theme.BusColor
import kr.yhs.traffic.ui.theme.StationInfoSelection

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun StationInfoPage(
    stationInfo: StationInfo,
    busInfo: List<StationRoute>,
    starActive: Boolean = false,
    scope: CoroutineScope,
    callback: (StationInfoSelection) -> Unit
) {
    val scalingLazyListState: ScalingLazyListState = rememberScalingLazyListState()
    val focusRequester = remember { FocusRequester() }
    var bookmarkActive by remember {
        mutableStateOf(starActive)
    }
    val stopWatch = remember { StopWatch() }
    var autoUpdate by remember { mutableStateOf(true) }
    Scaffold(
        positionIndicator = {
            PositionIndicator(scalingLazyListState = scalingLazyListState)
        }
    ) {
        stopWatch.start()
        ScalingLazyColumn(
            state = scalingLazyListState,
            modifier = Modifier.fillMaxSize()
                .onRotaryScrollEvent {
                    scope.launch {
                        scalingLazyListState.animateScrollBy(it.horizontalScrollPixels)
                    }
                    true
                }
                .focusRequester(focusRequester)
                .focusable(),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                StationTitle(stationInfo.name)
            }
            items(busInfo) {
                StationRoute(it, stopWatch.timeMillis.toInt())
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        modifier = Modifier.size(
                            width = ButtonDefaults.LargeButtonSize,
                            height = ButtonDefaults.ExtraSmallButtonSize
                        ),
                        onClick = {
                            bookmarkActive = !bookmarkActive
                            callback(StationInfoSelection.BOOKMARK)
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_star),
                            contentDescription = "star",
                            modifier = Modifier.size(16.dp),
                            tint = when(bookmarkActive) {
                                true -> Color.Yellow
                                false -> LocalContentColor.current
                            }
                        )
                    }
                    Button(
                        modifier = Modifier.size(
                            width = ButtonDefaults.LargeButtonSize,
                            height = ButtonDefaults.ExtraSmallButtonSize
                        ),
                        enabled = autoUpdate,
                        onClick = {
                            callback(StationInfoSelection.REFRESH)
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_refresh),
                            contentDescription = "refresh",
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }
        }
        if (stopWatch.timeMillis.toInt() > 180000) {
            autoUpdate = false
            callback(StationInfoSelection.REFRESH)
            autoUpdate = true
            stopWatch.reset()
        }
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}


@Composable
fun StationTitle(
    title: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            modifier = Modifier.align(Alignment.CenterVertically),
            color = Color.White,
            fontSize = 16.sp
        )
    }
}


@Composable
fun StationRoute(
    busInfo: StationRoute,
    timeLoop: Int
) {
    var backgroundColor = BusColor.Default
    val context = LocalContext.current
    for (busColor in BusColor.values()) {
        if (busInfo.type == busColor.typeCode) {
            backgroundColor = busColor
            break
        }
    }
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Chip(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp),
            colors = ChipDefaults.chipColors(
                backgroundColor = backgroundColor.color
            ),
            onClick = {},
            label = {
                Text(
                    text = busInfo.name
                )
            }
        )

        if (busInfo.isEnd != true && busInfo.isWait != true && busInfo.arrivalInfo.isNotEmpty()) {
            for (arrivalInfo in busInfo.arrivalInfo) {
                var timeMillis by remember { mutableStateOf(arrivalInfo.time) }
                var time by remember { mutableStateOf(context.getString(R.string.timestamp_second, 0)) }

                if (timeMillis == -1 || arrivalInfo.prevCount == null)
                    continue
                time = when {
                    timeMillis / 60 < 1 -> context.getString(R.string.timestamp_second, timeMillis)
                    timeMillis / 3600 < 1 && (timeMillis % 60 == 0 || (busInfo.type in 1200..1299) || (busInfo.type in 2100..2199)) -> context.getString(R.string.timestamp_minute, timeMillis / 60)
                    timeMillis / 3600 < 1 && (busInfo.type in 1100..1199 || busInfo.type in 1300..1399) -> context.getString(R.string.timestamp_minute_second, timeMillis / 60, timeMillis % 60)
                    timeMillis / 216000 < 1 -> context.getString(R.string.timestamp_hour_minute, timeMillis / 3600, timeMillis % 3600 / 60)
                    else -> context.getString(R.string.timestamp_second, timeMillis)
                }
                Log.i("stopWatch", "${timeLoop}초")
                timeMillis = arrivalInfo.time - (timeLoop / 1000)

                var response: String? = null
                if (arrivalInfo.prevCount == 0 && timeMillis <= 180 || timeMillis <= 60) {
                    if (arrivalInfo.seat != null)
                        response = context.getString(R.string.arrival_text_subtext_seat, arrivalInfo.seat)
                    ArrivalText(context.getString(R.string.arrival_text_soon), response)
                }
                else {
                    response = context.getString(R.string.arrival_text_subtext_prev_count, arrivalInfo.prevCount)
                    if (arrivalInfo.seat != null)
                        response = context.getString(R.string.arrival_text_subtext_prev_count_with_seat, arrivalInfo.prevCount, arrivalInfo.seat)
                    if (arrivalInfo.congestion != null) {
                        val congestionList = listOf(
                            context.getString(R.string.congestion_leisurely),
                            context.getString(R.string.congestion_normal),
                            context.getString(R.string.congestion_crowded),
                            context.getString(R.string.congestion_very_crowded)
                        )
                        response = context.getString(R.string.arrival_text_subtext_prev_count_with_congestion, arrivalInfo.prevCount, congestionList[arrivalInfo.congestion - 1])
                    }
                    ArrivalText(time, response)
                }
            }
        } else if (busInfo.isEnd == true) {
            ArrivalText(context.getString(R.string.arrival_text_closed))
        } else if (busInfo.isWait == true) {
            ArrivalText(context.getString(R.string.arrival_text_wait))
        } else {
            ArrivalText(context.getString(R.string.arrival_text_unknown))
        }
    }
}


@Composable
fun ArrivalText(
    mainText: String,
    subText: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 20.dp, end = 20.dp,
                top = 5.dp, bottom = 5.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = mainText,
            textAlign = TextAlign.Start,
            color = Color.LightGray,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        if (subText != null) {
            Text(
                text = subText,
                textAlign = TextAlign.End,
                color = Color.LightGray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}