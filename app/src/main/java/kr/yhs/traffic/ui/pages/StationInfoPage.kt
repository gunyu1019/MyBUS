package kr.yhs.traffic.ui

import android.text.Layout
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.rememberScalingLazyListState
import kr.yhs.traffic.R
import kr.yhs.traffic.models.StationInfo

@Composable
fun StationInfoPage(
    stationInfo: StationInfo
) {
    val scalingLazyListState: ScalingLazyListState = rememberScalingLazyListState()
    ScalingLazyColumn(
        state = scalingLazyListState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        item {
            StationTitle(
                stationInfo.name,
                true
            )
        }
    }
}


@Composable
fun StationTitle(
    title: String,
    starActive: Boolean = false
) {
    val resource = when (starActive) {
        true -> painterResource(R.drawable.ic_baseline_star)
        false -> painterResource(R.drawable.ic_baseline_nonstar)
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.align(Alignment.CenterVertically),
            color = Color.White
        )
        Icon(
            painter = resource,
            contentDescription = "star",
            modifier = Modifier.size(16.dp)
        )
    }
}