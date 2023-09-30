package kr.yhs.traffic.ui.components

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import kr.yhs.traffic.R

@Composable
fun StepPage(
    activity: Activity,
    title: String,
    description: String,
    buttonText: String = "다음",
    enableStopButton: Boolean = false,
    nextButtonCallback: () -> Unit
) = WearScaffold {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 3.dp),
            text = title,
            style = MaterialTheme.typography.display1
        )
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(start = 4.dp, end = 4.dp, bottom = 20.dp),
            text = description,
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        when (enableStopButton) {
            true -> Row(
                modifier = Modifier.align(Alignment.CenterHorizontally)
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
                        .padding(start = 3.dp), buttonText, nextButtonCallback,
                )
            }

            else -> NextButton(
                modifier = Modifier
                    .width(100.dp)
                    .align(Alignment.CenterHorizontally),
                buttonText,
                nextButtonCallback
            )
        }
    }
}