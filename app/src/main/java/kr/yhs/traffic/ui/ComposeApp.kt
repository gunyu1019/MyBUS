package kr.yhs.traffic.ui

import android.Manifest
import android.app.Activity
import android.app.RemoteInput
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.input.RemoteInputIntentHelper
import androidx.wear.widget.ConfirmationOverlay
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.yhs.traffic.MainActivity
import kr.yhs.traffic.R
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.models.StationRoute
import kr.yhs.traffic.module.getLocation
import kr.yhs.traffic.ui.pages.*
import retrofit2.await


@OptIn(ExperimentalWearMaterialApi::class, com.google.accompanist.pager.ExperimentalPagerApi::class,
    com.google.accompanist.permissions.ExperimentalPermissionsApi::class
)
@Composable
fun ComposeApp(activity: MainActivity) {
    var stationQuery by remember { mutableStateOf("") }
    val navigationController = rememberSwipeDismissableNavController()
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if(it.resultCode == Activity.RESULT_OK) {
            val intent = it.data
            val remoteInputResponse = RemoteInput.getResultsFromIntent(intent)
            stationQuery = remoteInputResponse.getString("SEARCH_BUS_STATION", "")
            navigationController.navigate(
                Screen.StationList.route + "?$STATION_TYPE=${StationListType.SEARCH}",
            )
        }
    }
    var lastStation by remember { mutableStateOf<StationInfo?>(null) }

    SwipeDismissableNavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navigationController,
        startDestination = Screen.MainScreen.route
    ) {
        composable(Screen.MainScreen.route) {
            mainPage(
                listOf({
                        StationSearch {
                            val remoteInputs = listOf(
                                RemoteInput.Builder("SEARCH_BUS_STATION")
                                    .setLabel("검색하실 정류소")
                                    .build()
                            )
                            val intent = RemoteInputIntentHelper.createActionRemoteInputIntent()
                            RemoteInputIntentHelper.putRemoteInputsExtra(intent, remoteInputs)
                            launcher.launch(intent)
                        }
                    }, {
                        StationGPS {
                            navigationController.navigate(
                                Screen.StationList.route + "?$STATION_TYPE=${StationListType.GPS_LOCATION_SEARCH}",
                            )
                        }
                    }, {
                        StationStar {}
                    }
                )
            )
        }
        composable(
            Screen.StationList.route+ "?$STATION_TYPE={$STATION_TYPE}",
            listOf(
                navArgument(STATION_TYPE) {
                    type = NavType.EnumType(StationListType::class.java)
                    defaultValue = StationListType.SEARCH
                }
            )
        ) {
            val stationType = it.arguments?.getSerializable(STATION_TYPE)
            var stationList by remember { mutableStateOf<List<StationInfo>>(emptyList()) }
            var location by remember { mutableStateOf<Location?>(null) }

            val permissionResult = rememberMultiplePermissionsState(
                listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            LaunchedEffect(true) {
                if (ActivityCompat.checkSelfPermission(
                        activity, Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        activity, Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionResult.launchMultiplePermissionRequest()
                    if (!permissionResult.allPermissionsGranted) {
                        ConfirmationOverlay()
                            .setType(ConfirmationOverlay.FAILURE_ANIMATION)
                            .setMessage(activity.getText(R.string.gps_not_found))
                            .showOn(activity)
                        navigationController.navigate(
                            Screen.MainScreen.route
                        )
                        return@LaunchedEffect
                    }
                }
                location = withContext(Dispatchers.Default) {
                    getLocation(activity, activity.fusedLocationClient!!)
                }
                Log.i("location", "$location")
                stationList = withContext(Dispatchers.Default) {
                    when (stationType) {
                        StationListType.SEARCH -> {
                            activity.client!!.getStation(
                                name = stationQuery
                            ).await()
                        }
                        StationListType.GPS_LOCATION_SEARCH -> {
                            val convertData = mutableListOf<StationInfo>()
                            if (location == null) {
                                navigationController.navigate(
                                    Screen.MainScreen.route
                                )
                                return@withContext listOf()
                            }
                            val stationAroundList = activity.client!!.getStationAround(
                                posX = location!!.longitude,
                                posY = location!!.latitude
                            ).await()
                            for (st in stationAroundList) {
                                convertData.add(
                                    st.convertToStationInfo()
                                )
                            }
                            convertData
                        }
                        else -> listOf()
                    }
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
                StationListType.SEARCH -> activity.getString(R.string.title_search, stationQuery)
                StationListType.GPS_LOCATION_SEARCH -> activity.getString(R.string.title_gps_location)
                else -> activity.getString(R.string.title_search)
            }
            StationListPage(title, stationList, location) { station: StationInfo ->
                lastStation = station
                navigationController.navigate(
                    Screen.StationInfo.route,
                )
            }
        }
        composable(
            Screen.StationInfo.route
        ) {
            var busList by remember { mutableStateOf<List<StationRoute>>(emptyList()) }
            if (lastStation == null) {
                ConfirmationOverlay()
                    .setType(ConfirmationOverlay.FAILURE_ANIMATION)
                    .setMessage(activity.getText(R.string.gps_not_found))
                    .showOn(activity)
                navigationController.navigate(
                    Screen.MainScreen.route
                )
            }
            LaunchedEffect(true) {
                busList = withContext(Dispatchers.Default) {
                    activity.client!!.getRoute(
                        cityCode = lastStation!!.type,
                        id = lastStation!!.id.toString().padStart(5, '0')
                    ).await()
                }
                Log.i("BusInfo", "$busList")
            }
            StationInfoPage(lastStation!!, busList)
        }
    }
}
