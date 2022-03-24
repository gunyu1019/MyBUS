package kr.yhs.traffic.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.widget.ConfirmationOverlay
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.tasks.await
import kr.yhs.traffic.MainActivity
import kr.yhs.traffic.R
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.navigation.STATION_TYPE
import kr.yhs.traffic.navigation.Screen
import kr.yhs.traffic.navigation.StationListType
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
                        if (page == 0) StationSearch {}
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

            if (ActivityCompat.checkSelfPermission(
                    activity, Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    activity, Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), 1000
                )
                navigationController.navigate(
                    Screen.MainScreen.route
                )
                return@composable
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
            ProgressIndicator()
            scope.run {
                launch {
                    val location = activity.fusedLocationClient!!.lastLocation.await<Location>()
                    val stationList: List<StationInfo> = when (stationType) {
                        StationListType.SEARCH -> {
                            listOf()
                        }
                        StationListType.GPS_LOCATION_SEARCH -> {
                            val stationList = mutableListOf<StationInfo>()
                            val stationListData = activity.client?.getStationAround(
                                posX = location.longitude, posY = location.latitude
                            )!!.await()
                            for (station in stationListData) {
                                stationList.add(station.changeToStationInfo())
                            }
                            stationList
                        }
                        else -> listOf()
                    }
                    StationList(title, stationList)
                }
            }
        }
    }
}
