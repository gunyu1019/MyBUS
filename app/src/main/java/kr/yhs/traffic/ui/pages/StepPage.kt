package kr.yhs.traffic.ui.pages

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.rotaryinput.rotaryWithSnap
import com.google.android.horologist.compose.rotaryinput.toRotaryScrollAdapter
import kr.yhs.traffic.R
import kr.yhs.traffic.ui.components.NextButton
import kr.yhs.traffic.ui.components.WearScaffold

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun StepPage(
    activity: Activity,
    title: String,
    description: String,
    buttonText: String = "다음",
    enableStopButton: Boolean = false,
    nextButtonCallback: () -> Unit
) {
    val scalingLazyListState = rememberScalingLazyListState(initialCenterItemIndex = 0)
    WearScaffold(positionIndicator = {
        PositionIndicator(scalingLazyListState = scalingLazyListState)
    }) {
        val context = LocalContext.current
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .rotaryWithSnap(
                    scalingLazyListState.toRotaryScrollAdapter()
                ),
            state = scalingLazyListState,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            autoCentering = AutoCenteringParams(itemIndex = 0, itemOffset = 50)
        ) {
            item {
                Text(
                    modifier = Modifier.padding(bottom = 3.dp),
                    text = title,
                    style = MaterialTheme.typography.display1,
                )
            }
            item {
                Text(
                    modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 20.dp),
                    text = description,
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
            item {
                when (enableStopButton) {
                    true -> Row(
                        modifier = Modifier
                    ) {
                        NextButton(
                            modifier = Modifier
                                .width(60.dp)
                                .align(Alignment.CenterVertically)
                                .padding(end = 3.dp),
                            context.getString(R.string.station_tile_setting_cancel_button),
                        ) { activity.finish() }
                        NextButton(
                            modifier = Modifier
                                .width(60.dp)
                                .align(Alignment.CenterVertically)
                                .padding(start = 3.dp),
                            buttonText, nextButtonCallback,
                        )
                    }

                    else -> NextButton(
                        modifier = Modifier.width(100.dp), buttonText, nextButtonCallback
                    )
                }
            }
        }
    }
}