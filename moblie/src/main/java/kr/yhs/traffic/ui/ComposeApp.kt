package kr.yhs.traffic.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.PinDrop
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import de.charlex.compose.BottomDrawerScaffold
import de.charlex.compose.rememberBottomDrawerScaffoldState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.yhs.traffic.MainActivity
import kr.yhs.traffic.R
import kr.yhs.traffic.models.ArrivalInfo
import kr.yhs.traffic.models.StationAroundInfo
import kr.yhs.traffic.models.StationRoute
import kr.yhs.traffic.module.getLocation
import kr.yhs.traffic.ui.component.AroundStation
import kr.yhs.traffic.ui.component.FavoriteArrival
import kr.yhs.traffic.ui.component.SearchBox
import kr.yhs.traffic.ui.theme.AppTheme
import kr.yhs.traffic.utils.Keyboard
import kr.yhs.traffic.utils.keyboardAsState


@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalComposeUiApi::class,
    ExperimentalPermissionsApi::class
)
@Composable
fun ComposeApp(activity: MainActivity? = null) {
    val bottomDrawerScaffoldState = rememberBottomDrawerScaffoldState()
    val scope = rememberCoroutineScope()
    val source = remember { MutableInteractionSource() }
    var location by remember { mutableStateOf<Location?>(null) }

    val permissionResult = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(source) {
        source.interactions.collect {
            if (it is PressInteraction.Release) {
                bottomDrawerScaffoldState.bottomDrawerState.expand()
            }
        }
    }
    if (activity != null) {
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)
    }
    val keyboardState by keyboardAsState()

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
                SearchBox(interactionSource = source)
            }
        },
        drawerElevation = 0.dp,
        drawerPeekHeight = 180.dp,
        drawerBackgroundColor = Color.Transparent,
        gesturesEnabled = (keyboardState == Keyboard.Closed),
        scaffoldState = bottomDrawerScaffoldState
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp)
        ) {
            // Title View
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
            ) {
                TitleText(listOf("1154번", "버스가"), listOf(MaterialTheme.colors.primary))
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column {
                    CardTitleText(title = "즐겨찾는 정류장")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.PinDrop, "location")
                        Text(text = "ㅇㅇ정류장")
                    }
                }
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        Icons.Outlined.ChevronLeft,
                        "left icon",
                        modifier = Modifier
                            .width(30.dp)
                            .height(30.dp)
                            .padding(0.dp)
                    )
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        Icons.Outlined.ChevronRight,
                        "right icon",
                        modifier = Modifier
                            .width(30.dp)
                            .height(30.dp)
                            .padding(0.dp)
                    )
                }
            }
            LazyRow(
                state = listState,
                modifier = Modifier.padding(top = 2.dp)
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
                    ) {

                    }
                }
            }
            Spacer(Modifier.height(30.dp))

            // Around Bus Station
            CardTitleText(title = "주변 정류장")
            LaunchedEffect(true) {
                if (activity?.fusedLocationClient != null) {
                    if (ActivityCompat.checkSelfPermission(
                            activity, Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            activity, Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        permissionResult.launchMultiplePermissionRequest()
                        if (!permissionResult.allPermissionsGranted) {
                            return@LaunchedEffect
                        }
                    }
                }
                location = withContext(Dispatchers.Default) {
                    getLocation(
                        activity?.fusedLocationClient!!
                    )
                }
            }
            LazyRow(
                state = listState
            ) {
                item {
                    AroundStation(
                        stationInfo = StationAroundInfo(
                            "서울역", "iwd101", 37.0, 38.0, "idk102", "idk102", 1, 200
                        )
                    )
                }
            }
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
                    style = SpanStyle(color = textColors.getOrNull(index) ?: Color.Unspecified)
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
        fontWeight = FontWeight.Bold,
    )
}


@Preview(showBackground = true)
@Composable
fun ComposeAppPreview() {
    AppTheme {
        ComposeApp()
    }
}