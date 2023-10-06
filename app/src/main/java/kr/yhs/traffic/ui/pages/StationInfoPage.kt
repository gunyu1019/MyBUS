package kr.yhs.traffic.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.LocalContentColor
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.scrollAway
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.rotaryinput.rotaryWithSnap
import com.google.android.horologist.compose.rotaryinput.toRotaryScrollAdapter
import kr.yhs.traffic.R
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.models.StationRoute
import kr.yhs.traffic.ui.components.LoadingProgressIndicator
import kr.yhs.traffic.ui.components.StationRouteInfo
import kr.yhs.traffic.ui.components.StationTitle
import kr.yhs.traffic.ui.components.WearScaffold
import kr.yhs.traffic.ui.theme.StationInfoSelection
import kr.yhs.traffic.utils.StopWatch

@OptIn(
    ExperimentalHorologistApi::class, ExperimentalHorologistApi::class
)
@Composable
fun StationInfoPage(
    stationInfo: StationInfo,
    busInfo: List<StationRoute>,
    starActive: Boolean = false,
    isLoading: Boolean = false,
    buttonList: List<StationInfoSelection> = listOf(
        StationInfoSelection.REFRESH, StationInfoSelection.BOOKMARK
    ),
    callback: (StationInfoSelection) -> Unit
) {
    val scalingLazyListState: ScalingLazyListState =
        rememberScalingLazyListState(initialCenterItemIndex = 0)
    var bookmarkActive by remember {
        mutableStateOf(starActive)
    }
    val stopWatch = remember { StopWatch() }
    var autoUpdate by remember { mutableStateOf(true) }
    WearScaffold(positionIndicator = {
        PositionIndicator(scalingLazyListState = scalingLazyListState)
    }, timeText = {
        TimeText(
            modifier = Modifier.scrollAway(scalingLazyListState)
        )
    }) {
        stopWatch.start()
        ScalingLazyColumn(
            state = scalingLazyListState, modifier = Modifier
                .fillMaxSize()
                .rotaryWithSnap(
                    scalingLazyListState.toRotaryScrollAdapter()
                ), contentPadding = PaddingValues(16.dp)
        ) {
            item {
                StationTitle(stationInfo.name)
            }
            if (!isLoading) {
                items(busInfo) {
                    StationRouteInfo(it, stopWatch.timeMillis.toInt())
                }
            } else {
                item {
                    LoadingProgressIndicator()
                }
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (buttonList.contains(StationInfoSelection.BOOKMARK)) {
                        Button(modifier = Modifier.size(
                            width = ButtonDefaults.LargeButtonSize,
                            height = ButtonDefaults.ExtraSmallButtonSize
                        ), onClick = {
                            bookmarkActive = !bookmarkActive
                            callback(StationInfoSelection.BOOKMARK)
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_star),
                                contentDescription = "star",
                                modifier = Modifier.size(16.dp),
                                tint = when (bookmarkActive) {
                                    true -> Color.Yellow
                                    false -> LocalContentColor.current
                                }
                            )
                        }
                    }
                    if (buttonList.contains(StationInfoSelection.REFRESH)) {
                        Button(modifier = Modifier.size(
                            width = ButtonDefaults.LargeButtonSize,
                            height = ButtonDefaults.ExtraSmallButtonSize
                        ), enabled = autoUpdate, onClick = {
                            callback(StationInfoSelection.REFRESH)
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_refresh),
                                contentDescription = "refresh",
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                    if (buttonList.contains(StationInfoSelection.EXIT)) {
                        Button(modifier = Modifier.size(
                            width = ButtonDefaults.LargeButtonSize,
                            height = ButtonDefaults.ExtraSmallButtonSize
                        ), onClick = {
                            callback(StationInfoSelection.EXIT)
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_exit),
                                contentDescription = "refresh",
                                modifier = Modifier.size(16.dp),
                            )
                        }
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
}