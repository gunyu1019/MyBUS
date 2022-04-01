package kr.yhs.traffic.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import kr.yhs.traffic.R
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.models.StationRoute
import kr.yhs.traffic.ui.theme.BusColor
import kr.yhs.traffic.ui.theme.StationInfoSelection

@Composable
fun StationInfoPage(
    stationInfo: StationInfo,
    busInfo: List<StationRoute>,
    starActive: Boolean = false,
    callback: (StationInfoSelection) -> Unit
) {
    val scalingLazyListState: ScalingLazyListState = rememberScalingLazyListState()
    var bookmarkActive by remember {
        mutableStateOf(starActive)
    }
    ScalingLazyColumn(
        state = scalingLazyListState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            StationTitle(stationInfo.name)
        }
        items(busInfo) {
            StationRoute(it)
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
    busInfo: StationRoute
) {
    var backgroundColor = BusColor.Default
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
                if (arrivalInfo.time == null || arrivalInfo.prevCount == null)
                    continue

                val time = when {
                    arrivalInfo.time / 60 < 1 -> "${arrivalInfo.time}초"
                    arrivalInfo.time / 3600 < 1 && arrivalInfo.time % 60 == 0 -> "${arrivalInfo.time / 60}분"
                    arrivalInfo.time / 3600 < 1 -> "${arrivalInfo.time / 60}분 ${arrivalInfo.time % 60}초"
                    arrivalInfo.time / 3600 < 1 && arrivalInfo.time % 60 == 0 -> "${arrivalInfo.time / 3600}시간 ${arrivalInfo.time % 3600 / 60}분"
                    arrivalInfo.time / 216000 < 1 -> "${arrivalInfo.time / 3600}시간 ${arrivalInfo.time % 3600 / 60}분 ${arrivalInfo.time % 60}초"
                    else -> "${arrivalInfo.time}초"
                }
                var response: String? = null
                if (arrivalInfo.prevCount == 0 && arrivalInfo.time <= 60) {
                    if (arrivalInfo.seat != null)
                        response = "${arrivalInfo.seat}석"
                    ArrivalText("곧 도착", response)
                }
                else {
                    response = "${arrivalInfo.prevCount}번째 전"
                    if (arrivalInfo.seat != null)
                        response = "${arrivalInfo.prevCount}번째 전, ${arrivalInfo.seat}석"
                    ArrivalText(time, response)
                }
            }
        } else if (busInfo.isEnd == true) {
            ArrivalText("운행 종료")
        } else if (busInfo.isWait == true) {
            ArrivalText("출발 대기")
        } else {
            ArrivalText("정보 없음")
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
        horizontalArrangement = Arrangement.SpaceBetween
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
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}