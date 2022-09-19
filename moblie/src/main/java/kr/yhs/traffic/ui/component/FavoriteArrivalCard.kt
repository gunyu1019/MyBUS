package kr.yhs.traffic.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kr.yhs.traffic.R
import kr.yhs.traffic.models.ArrivalInfo
import kr.yhs.traffic.models.StationRoute
import kr.yhs.traffic.ui.theme.AppTheme


@Composable
fun FavoriteArrival(
    background: Color = Color(0xff3d5bab),
    busInfo: StationRoute,
    onClick: (() -> Unit) = {}
) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier
            .width(210.dp)
            .height(120.dp)
            .padding(4.dp)
            .clickable {
                onClick()
            },
        elevation = 4.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.background(background),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp, start = 10.dp, end = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    busInfo.name,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                if (busInfo.arrivalInfo.isNotEmpty()) {
                    Text(
                        "${busInfo.arrivalInfo[0].prevCount}번째 전",
                        modifier = Modifier
                            .padding(start = 3.dp)
                            .height(21.dp)
                            .align(Alignment.CenterVertically),
                        textAlign = TextAlign.End,
                        fontSize = 18.sp,
                        color = Color.LightGray
                    )
                }
            }
            for ((index, arrivalInfo) in busInfo.arrivalInfo.withIndex()) {
                // TODO(kr.yhs.traffic.utils 로 시간 구분하는 구간을 분리할 예정)
                val timeMillis by remember { mutableStateOf(arrivalInfo.time) }
                var time by remember { mutableStateOf(context.getString(R.string.timestamp_second, 0)) }

                if (timeMillis == -1 || arrivalInfo.prevCount == null)
                    continue
                time = when {
                    timeMillis / 60 < 1 -> context.getString(R.string.timestamp_second, timeMillis)
                    timeMillis / 3600 < 1 && (timeMillis % 60 == 0 || (busInfo.type in 1200..1299) || (busInfo.type in 2100..2199)) ->
                        context.getString(R.string.timestamp_minute, timeMillis / 60)
                    timeMillis / 3600 < 1 && (busInfo.type in 1100..1199 || busInfo.type in 1300..1399) ->
                        context.getString(R.string.timestamp_minute_second, timeMillis / 60, timeMillis % 60)
                    timeMillis / 216000 < 1 -> context.getString(R.string.timestamp_hour_minute, timeMillis / 3600, timeMillis % 3600 / 60)
                    else -> context.getString(R.string.timestamp_second, timeMillis)
                }

                var mainText: String = time
                var subText: String? = null

                if (arrivalInfo.seat != null)
                    subText = context.getString(R.string.arrival_text_subtext_seat, arrivalInfo.seat)
                if (arrivalInfo.prevCount == 0 && timeMillis <= 180 || timeMillis <= 60) {
                    mainText = "곧 도착"
                }

                Row(
                    modifier = Modifier.padding(start = 10.dp, top = 2.dp, bottom = 2.dp, end = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${index + 1}",
                        modifier = Modifier
                            .width(21.dp)
                            .height(21.dp)
                            .background(Color.DarkGray, shape = CircleShape),
                        color = Color.LightGray,
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp
                    )
                    ArrivalText(mainText, subText)
                }
            }
        }
    }
}


@Composable
fun ArrivalText(
   time: String, subtext: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(23.dp)
            .padding(start = 3.dp, end = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            time,
            modifier = Modifier.padding(start = 3.dp),
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Start,
            color = Color.White,
            fontSize = 18.sp
        )
        if (subtext != null) {
            Text(
                subtext,
                modifier = Modifier.padding(end = 3.dp),
                textAlign = TextAlign.End,
                color = Color.LightGray,
                fontSize = 18.sp
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun FavoriteArrivalPreview() {
    AppTheme {
        FavoriteArrival(
            busInfo = StationRoute(
                "1001",
                "3000",
                1001,
                isEnd = false,
                isWait = false,
                arrivalInfo = listOf(
                    ArrivalInfo(
                        carNumber = "12가4567",
                        congestion = null,
                        isArrival = false,
                        isFull = true,
                        lowBus = true,
                        prevCount = 1,
                        seat = 1,
                        time = 60
                    ), ArrivalInfo(
                        carNumber = "89가4567",
                        congestion = null,
                        isArrival = false,
                        isFull = true,
                        lowBus = true,
                        prevCount = 1,
                        seat = 3,
                        time = 360
                    )
                )
            )
        )
    }
}