package kr.yhs.traffic.ui

import android.app.Activity
import android.app.RemoteInput
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.input.RemoteInputIntentHelper
import androidx.wear.widget.ConfirmationOverlay
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.yhs.traffic.MainActivity
import kr.yhs.traffic.R
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.module.getLocation
import kr.yhs.traffic.ui.navigator.STATION_TYPE
import kr.yhs.traffic.ui.navigator.Screen
import kr.yhs.traffic.ui.navigator.StationListType
import retrofit2.await


@OptIn(ExperimentalWearMaterialApi::class, com.google.accompanist.pager.ExperimentalPagerApi::class)
@Composable
fun ComposeApp(activity: MainActivity) {
    val navigationController = rememberSwipeDismissableNavController()
    val scope = rememberCoroutineScope()

    SwipeDismissableNavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navigationController,
        startDestination = Screen.MainScreen.route
    ) {
        // Main Windows
        composable(Screen.MainScreen.route) {
            val pagerState = rememberPagerState()
            Scaffold {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                ) {
                    HorizontalPager(
                        count = 3,
                        state = pagerState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                    ) { page: Int ->
                        if (page == 0) StationSearch {
                            val remoteInputs = listOf(
                                RemoteInput.Builder("SEARCH_BUS_STATION")
                                    .setLabel("검색하실 정류소")
                                    .build()
                            )
                            val intent = RemoteInputIntentHelper.createActionRemoteInputIntent()
                            RemoteInputIntentHelper.putRemoteInputsExtra(intent, remoteInputs)
                            activity.systemCommand.launch(intent)
                        }
                        if (page == 1) StationGPS {
                            navigationController.navigate(
                                Screen.StationList.route
                            )
                        }
                        if (page == 2) StationStar {}
                    }
                    HorizontalPagerIndicator(
                        pagerState = pagerState,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally),
                        activeColor = Color(0xffffffff),
                        inactiveColor = Color(0xff808080),
                        indicatorWidth = 4.dp
                    )
                }
            }
        }
        composable(
            Screen.StationList.route,
            listOf(
                navArgument(STATION_TYPE) {
                    type = NavType.EnumType(StationListType::class.java)
                    defaultValue = StationListType.GPS_LOCATION_SEARCH
                }
            )
        ) {
            val stationType = it.arguments?.getSerializable(STATION_TYPE)
            var stationList by remember { mutableStateOf<List<StationInfo>>(emptyList()) }
            LaunchedEffect(true) {
                stationList = withContext(Dispatchers.Default) {
                    val convertData = mutableListOf<StationInfo>()
                    val location = getLocation(activity, activity.fusedLocationClient!!)
                    if (location == null) {
                        navigationController.navigate(
                            Screen.MainScreen.route
                        )
                    }
                    val stationAroundList = activity.client!!.getStationAround(
                        posX = location!!.longitude,
                        posY = location.latitude
                    ).await()
                    for (st in stationAroundList) {
                        convertData.add(
                            st.convertToStationInfo()
                        )
                    }
                    convertData
                }
            }
            if (activity.fusedLocationClient == null) {
                ConfirmationOverlay()
                    .setType(ConfirmationOverlay.FAILURE_ANIMATION)
                    .setMessage(activity.getText(R.string.gps_not_found))
                    .showOn(activity)
                navigationController.navigate(
                    Screen.MainScreen.route
                )
                return@composable
            }

            val title = when (stationType) {
                StationListType.SEARCH -> activity.getString(R.string.title_search)
                StationListType.GPS_LOCATION_SEARCH -> activity.getString(R.string.title_gps_location)
                else -> activity.getString(R.string.title_search)
            }
            StationList(title, stationList)
        }
    }
}
