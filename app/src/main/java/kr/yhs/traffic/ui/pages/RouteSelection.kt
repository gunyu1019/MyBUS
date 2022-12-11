package kr.yhs.traffic.ui.pages

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import androidx.wear.widget.ConfirmationOverlay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.models.StationRoute
import kr.yhs.traffic.ui.components.LoadingProgressIndicator
import kr.yhs.traffic.ui.components.NextButton
import kr.yhs.traffic.ui.theme.BusColor


class RouteSelection(private val context: Activity) {
    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun Content(
        stationInfo: StationInfo,
        busInfo: List<StationRoute>,
        scope: CoroutineScope,
        isLoaded: Boolean = false,
        maxSelect: Int = 1,
        callback: (List<StationRoute>) -> Unit
    ) {
        val scalingLazyListState = rememberScalingLazyListState()
        Scaffold(
            positionIndicator = {
                PositionIndicator(scalingLazyListState = scalingLazyListState)
            }
        ) {
            val checkedRoute = mutableListOf<StationRoute>()
            var itemIndex by remember { mutableStateOf(1) }
            ScalingLazyColumn(
                state = scalingLazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .onRotaryScrollEvent {
                        scope.launch {
                            scalingLazyListState.animateScrollBy(it.horizontalScrollPixels)
                        }
                        true
                    },
                // .focusRequester(focusRequester)
                // .focusable(),
                contentPadding = PaddingValues(16.dp),
                autoCentering = AutoCenteringParams(itemIndex = itemIndex)
            ) {
                item {
                    RouteSelectionTitle(maxSelect)
                }
                if (isLoaded) {
                    items(busInfo) {
                        var checked by remember {
                            mutableStateOf(false)
                        }
                        SmallToggleChip(it, checked) {
                            if (checked) {
                                checkedRoute.remove(it)
                                checked = false
                            } else {
                                when (checkedRoute.size < maxSelect) {
                                    true -> {
                                        checkedRoute.add(it)
                                        checked = true
                                    }
                                    else -> {
                                        ConfirmationOverlay()
                                            .setType(ConfirmationOverlay.FAILURE_ANIMATION)
                                            .setMessage("최대 ${maxSelect}개까지 등록할 수 있습니다.")
                                            .showOn(context)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    item {
                        itemIndex = 2
                        LoadingProgressIndicator()
                    }
                }
                item {
                    NextButton(
                        modifier = Modifier.padding(top = 30.dp)
                    ) {
                        if (checkedRoute.size < 1) {
                            ConfirmationOverlay()
                                .setType(ConfirmationOverlay.FAILURE_ANIMATION)
                                .setMessage("최소 1개 이상 선택해 주세요.")
                                .showOn(context)
                            return@NextButton
                        }
                        callback.invoke(checkedRoute)
                    }
                }
            }
        }
    }

    @Composable
    private fun RouteSelectionTitle(maxSelect: Int) =
        Column(
            modifier = Modifier.padding(bottom = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "버스 노선 선택",
                color = Color.White,
                fontSize = 18.sp,
                maxLines = 1,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "버스정류장에 경유하는 노선 중 최대 ${maxSelect}개까지 선택해주세요.",
                color = Color.White,
                fontSize = 12.sp,
                maxLines = 2,
                textAlign = TextAlign.Center
            )
            Text(
                text = "선택 후 다음 버튼을 눌러주세요.",
                color = Color.White,
                fontSize = 12.sp,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }

    @Composable
    private fun SmallToggleChip(busInfo: StationRoute, checked: Boolean, onClick: () -> Unit) {
        var backgroundColor = BusColor.Default
        for (busColor in BusColor.values()) {
            if (busInfo.type == busColor.typeCode) {
                backgroundColor = busColor
                break
            }
        }
        Chip(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp),
            onClick = onClick,
            label = {
                Spacer(
                    modifier = Modifier
                        .width(20.dp)
                        .height(20.dp)
                        .clip(CircleShape)
                        .background(backgroundColor.color)
                )
                Text(
                    text = busInfo.name,
                    modifier = Modifier.padding(start = 4.dp)
                )
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
                Checkbox(
                    checked = checked,
                    modifier = Modifier.semantics {
                        this.contentDescription =
                            if (checked) "Checked" else "Unchecked"
                    }
                )
            }
        )
    }
}