package kr.yhs.traffic.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Text
import kr.yhs.traffic.R
import kr.yhs.traffic.models.StationRoute
import kr.yhs.traffic.ui.theme.BusColor
import kr.yhs.traffic.utils.timeFormatter


@Composable
fun StationRouteInfo(
    busInfo: StationRoute, timeLoop: Int
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
        Chip(modifier = Modifier
            .fillMaxWidth()
            .height(32.dp), colors = ChipDefaults.chipColors(
            backgroundColor = backgroundColor.color
        ), onClick = {}, label = {
            Text(
                text = busInfo.name
            )
        })

        if (busInfo.isEnd != true && busInfo.isWait != true && busInfo.arrivalInfo.isNotEmpty()) {
            for (arrivalInfo in busInfo.arrivalInfo) {
                var timeMillis by remember { mutableIntStateOf(arrivalInfo.time) }
                var time by remember {
                    mutableStateOf(
                        context.getString(
                            R.string.timestamp_second, 0
                        )
                    )
                }

                if (timeMillis == -1 || arrivalInfo.prevCount == null) continue
                time = timeFormatter(
                    context, timeMillis, (busInfo.type in 1100..1199 || busInfo.type in 1300..1399)
                )
                timeMillis = arrivalInfo.time - (timeLoop / 1000)

                var response: String? = null
                if (arrivalInfo.prevCount == 0 && timeMillis <= 180 || timeMillis <= 60) {
                    if (arrivalInfo.seat != null) response =
                        context.getString(R.string.arrival_text_subtext_seat, arrivalInfo.seat)
                    ArrivalText(context.getString(R.string.arrival_text_soon), response)
                } else {
                    response = context.getString(
                        R.string.arrival_text_subtext_prev_count, arrivalInfo.prevCount
                    )
                    if (arrivalInfo.seat != null) response = context.getString(
                        R.string.arrival_text_subtext_prev_count_with_seat,
                        arrivalInfo.prevCount,
                        arrivalInfo.seat
                    )
                    if (arrivalInfo.congestion != null) {
                        val congestionList = listOf(
                            context.getString(R.string.congestion_leisurely),
                            context.getString(R.string.congestion_normal),
                            context.getString(R.string.congestion_crowded),
                            context.getString(R.string.congestion_very_crowded)
                        )
                        response = context.getString(
                            R.string.arrival_text_subtext_prev_count_with_congestion,
                            arrivalInfo.prevCount,
                            congestionList[arrivalInfo.congestion - 1]
                        )
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
