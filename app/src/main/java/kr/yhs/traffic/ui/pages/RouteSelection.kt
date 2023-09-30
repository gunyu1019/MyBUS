package kr.yhs.traffic.ui.pages

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Checkbox
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.scrollAway
import androidx.wear.widget.ConfirmationOverlay
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.rotaryinput.rotaryWithSnap
import com.google.android.horologist.compose.rotaryinput.toRotaryScrollAdapter
import kr.yhs.traffic.R
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.models.StationRoute
import kr.yhs.traffic.ui.components.LoadingProgressIndicator
import kr.yhs.traffic.ui.components.NextButton
import kr.yhs.traffic.ui.components.WearScaffold
import kr.yhs.traffic.ui.theme.BusColor


class RouteSelection(private val context: Activity) {
    @OptIn(ExperimentalHorologistApi::class)
    @Composable
    fun Content(
        stationInfo: StationInfo,
        busInfo: List<StationRoute>,
        isLoaded: Boolean = false,
        rotaryScrollEnable: Boolean = true,
        maxSelect: Int = 1,
        callback: (List<StationRoute>) -> Unit
    ) {
        val scalingLazyListState = rememberScalingLazyListState()
        var modifier = Modifier.fillMaxSize()
        if (rotaryScrollEnable) {
            modifier = modifier.rotaryWithSnap(
                scalingLazyListState.toRotaryScrollAdapter()
            )
        }
        WearScaffold(
            positionIndicator = {
                PositionIndicator(scalingLazyListState = scalingLazyListState)
            }, timeText = {
                TimeText(
                    modifier = Modifier.scrollAway(scalingLazyListState)
                )
            }
        ) {
            val checkedRoute = mutableListOf<StationRoute>()
            var itemIndex by remember { mutableIntStateOf(1) }
            ScalingLazyColumn(
                state = scalingLazyListState,
                modifier = modifier,
                contentPadding = PaddingValues(16.dp),
                autoCentering = AutoCenteringParams(itemIndex = itemIndex)
            ) {
                item {
                    RouteSelectionTitle(maxSelect)
                }
                if (isLoaded) {
                    items(busInfo) {
                        itemIndex = 1
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
                                            .setMessage(context.getString(R.string.route_selection_max_selection_message, maxSelect))
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
                    Row(modifier = Modifier.padding(top = 30.dp)) {
                        NextButton(
                            modifier = Modifier
                                .width(60.dp)
                                .align(Alignment.CenterVertically)
                                .padding(end = 3.dp),
                            context.getString(R.string.station_tile_setting_cancel_button),
                        ) { context.finish() }
                        NextButton(
                            modifier = Modifier
                                .width(60.dp)
                                .padding(start = 3.dp)
                        ) {
                            if (checkedRoute.size < maxSelect) {
                                ConfirmationOverlay()
                                    .setType(ConfirmationOverlay.FAILURE_ANIMATION)
                                    .setMessage(context.getString(R.string.route_selection_min_selection_message, maxSelect))
                                    .showOn(context)
                                return@NextButton
                            }
                            callback.invoke(checkedRoute)
                        }
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
                text = context.getString(R.string.route_selection_title),
                color = Color.White,
                style = MaterialTheme.typography.display1,
                maxLines = 1,
                modifier = Modifier.padding(bottom = 2.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = context.getString(R.string.route_selection_description1, maxSelect),
                color = Color.White,
                style = MaterialTheme.typography.body2,
                maxLines = 2,
                textAlign = TextAlign.Center
            )
            Text(
                text = context.getString(R.string.route_selection_description2),
                color = Color.White,
                style = MaterialTheme.typography.body2,
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
                    style = MaterialTheme.typography.title1,
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