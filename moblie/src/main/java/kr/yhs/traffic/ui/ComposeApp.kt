package kr.yhs.traffic.ui

import android.app.Activity
import android.graphics.Paint
import android.text.style.StyleSpan
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.charlex.compose.BottomDrawerScaffold
import de.charlex.compose.rememberBottomDrawerScaffoldState
import kr.yhs.traffic.models.ArrivalInfo
import kr.yhs.traffic.models.StationRoute
import kr.yhs.traffic.ui.component.SearchBox
import kr.yhs.traffic.ui.theme.AppTheme
import kr.yhs.traffic.ui.component.FavoriteArrival


@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ComposeApp(activity: Activity? = null) {
    val bottomDrawerScaffoldState = rememberBottomDrawerScaffoldState()
    val scope = rememberCoroutineScope()
    val source = remember { MutableInteractionSource() }
    var bottomDrawerGestureEnableStatus by remember { mutableStateOf(true) }

    LaunchedEffect(source) {
        source.interactions.collect {
            if (it is PressInteraction.Release) {
                bottomDrawerScaffoldState.bottomDrawerState.expand()
                bottomDrawerGestureEnableStatus = false
            }
        }
    }

    BottomDrawerScaffold(
        drawerContent = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 10.dp,
                        end = 10.dp,
                        top = 10.dp
                    ),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                elevation = 4.dp
            ) {
                SearchBox(source) {
                    bottomDrawerGestureEnableStatus = true
                }
            }
        },
        drawerElevation = 0.dp,
        drawerPeekHeight = 150.dp,
        drawerBackgroundColor = Color.Transparent,
        gesturesEnabled = bottomDrawerGestureEnableStatus,
        scaffoldState = bottomDrawerScaffoldState
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp)
        ) {
            // Title View
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                TitleText(listOf("1154번", "버스가"), listOf(MaterialTheme.colors.primary))
                Spacer(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth())
                IconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(Icons.Rounded.Settings, "setting icon")
                }
            }
            TitleText(listOf("잠시 후", "도착 예정"), listOf(MaterialTheme.colors.primary))
            Spacer(Modifier.height(30.dp))

            // Favorite Bus Station
            val listState = rememberLazyListState()
            CardTitleText(title = "즐겨찾는 정류장")

            LazyRow(
                state = listState,
                modifier = Modifier.padding(top = 10.dp)
            ) {
                item {
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
            // TODO(INSERT SOURCE CODE)
            Spacer(Modifier.height(30.dp))

            // Around Bus Station
            // TODO(INSERT SOURCE CODE)
            Spacer(Modifier.height(30.dp))
        }
    }
}


@Composable
fun TitleText(
    sentence: List<String>,
    textColors: List<Color> = listOf()
) {
    Text(
        buildAnnotatedString {
            sentence.withIndex().forEach { (index, text) ->
                this@buildAnnotatedString.withStyle(
                    style = SpanStyle(color = textColors.getOrNull(index)?:Color.Unspecified)
                ) {
                    append("$text ")
                }
            }
        }, fontSize = 40.sp,
        fontWeight = FontWeight.Black
    )
}


@Composable
fun CardTitleText(title: String) {
    Text(
        text = title,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    )
}


@Preview(showBackground = true)
@Composable
fun ComposeAppPreview() {
    AppTheme {
        ComposeApp()
    }
}