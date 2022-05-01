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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.yhs.traffic.MainActivity
import kr.yhs.traffic.R
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.models.StationRoute
import kr.yhs.traffic.module.getLocation
import kr.yhs.traffic.ui.navigator.StationGPS
import kr.yhs.traffic.ui.navigator.StationSearch
import kr.yhs.traffic.ui.navigator.StationStar
import kr.yhs.traffic.ui.pages.*
import kr.yhs.traffic.ui.theme.StationInfoSelection
import retrofit2.await
import java.net.SocketTimeoutException


@OptIn(
    ExperimentalWearMaterialApi::class, com.google.accompanist.pager.ExperimentalPagerApi::class,
    com.google.accompanist.permissions.ExperimentalPermissionsApi::class
)
@Composable
fun ComposeApp(activity: MainActivity) {
    var stationQuery by remember { mutableStateOf("") }
    val navigationController = rememberSwipeDismissableNavController()
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val intent = it.data
            val remoteInputResponse = RemoteInput.getResultsFromIntent(intent)
            stationQuery = remoteInputResponse.getCharSequence("SEARCH_BUS_STATION", "").toString()
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
            MainPage(
                listOf({
                    StationSearch {
                        val remoteInputs = listOf(
                            RemoteInput.Builder("SEARCH_BUS_STATION")
                                .setLabel(
                                    activity.getString(R.string.search_label)
                                )
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
                    StationStar {
                        navigationController.navigate(
                            Screen.StationList.route + "?$STATION_TYPE=${StationListType.BOOKMARK}",
                        )
                    }
                }
                )
            )
        }
        composable(
            Screen.StationList.route + "?$STATION_TYPE={$STATION_TYPE}",
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
                        navigationController.popBackStack()
                        return@LaunchedEffect
                    }
                }
                location = withContext(Dispatchers.Default) {
                    getLocation(
                        activity.fusedLocationClient!!,
                        stationType == StationListType.GPS_LOCATION_SEARCH
                    )
                }
                Log.i("location", "$location")
                if (location == null) {
                    ConfirmationOverlay()
                        .setType(ConfirmationOverlay.FAILURE_ANIMATION)
                        .setMessage(activity.getText(R.string.gps_not_found))
                        .showOn(activity)
                    navigationController.popBackStack()
                    return@LaunchedEffect
                }
                try {
                    stationList = withContext(Dispatchers.Default) {
                        when (stationType) {
                            StationListType.SEARCH -> {
                                activity.client!!.getStation(
                                    name = stationQuery
                                ).await()
                            }
                            StationListType.GPS_LOCATION_SEARCH -> {
                                val convertData = mutableListOf<StationInfo>()
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
                            StationListType.BOOKMARK -> {
                                val bookmarkStation = mutableListOf<StationInfo>()
                                val sharedPreferences = activity.spClient!!
                                val bookmarkData =
                                    sharedPreferences.getArrayExtension("bookmark-station")
                                for (stationId in bookmarkData) {
                                    bookmarkStation.add(
                                        StationInfo(
                                            sharedPreferences.getString("$stationId-name")
                                                ?: activity.getString(R.string.unknown),
                                            sharedPreferences.getString("$stationId-id") ?: "0",
                                            sharedPreferences.getFloat("$stationId-posX").toDouble(),
                                            sharedPreferences.getFloat("$stationId-posY").toDouble(),
                                            sharedPreferences.getString("$stationId-displayId", "0"),
                                            sharedPreferences.getMutableType("$stationId-stationId")
                                                ?: "0",
                                            sharedPreferences.getInt("$stationId-type")
                                        )
                                    )
                                }
                                // Log.i("stationBookmark", "$bookmarkData $bookmarkStation")
                                sharedPreferences.setArrayExtension("bookmark-station", bookmarkData)
                                bookmarkStation
                            }
                            else -> listOf()
                        }
                    }
                } catch (e: SocketTimeoutException) {
                    ConfirmationOverlay()
                        .setType(ConfirmationOverlay.FAILURE_ANIMATION)
                        .setMessage(activity.getText(R.string.timeout))
                        .showOn(activity)
                    navigationController.popBackStack()
                    return@LaunchedEffect
                }
            }
            if (activity.fusedLocationClient == null) {
                ConfirmationOverlay()
                    .setType(ConfirmationOverlay.FAILURE_ANIMATION)
                    .setMessage(activity.getText(R.string.gps_not_found))
                    .showOn(activity)
                navigationController.popBackStack()
                return@composable
            }

            val title = when (stationType) {
                StationListType.SEARCH -> activity.getString(R.string.title_search, stationQuery)
                StationListType.GPS_LOCATION_SEARCH -> activity.getString(R.string.title_gps_location)
                StationListType.BOOKMARK -> activity.getString(R.string.title_bookmark)
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
                    .setMessage(activity.getText(R.string.station_not_found))
                    .showOn(activity)
                navigationController.popBackStack()
            }
            val postLastStation = lastStation!!
            LaunchedEffect(true) {
                try {
                    busList = withContext(Dispatchers.Default) {
                        activity.client!!.getRoute(
                            cityCode = postLastStation.type,
                            id = postLastStation.id
                        ).await()
                    }
                } catch (e: SocketTimeoutException) {
                    ConfirmationOverlay()
                        .setType(ConfirmationOverlay.FAILURE_ANIMATION)
                        .setMessage(activity.getText(R.string.timeout))
                        .showOn(activity)
                    navigationController.popBackStack()
                    return@LaunchedEffect
                }
                // Log.i("BusInfo", "$busList")
            }

            val preBookmarkData = activity.spClient!!.getArrayExtension("bookmark-station")
            val bookmarkKey = "${postLastStation.id}0${postLastStation.type}"
            // Log.i("station-bookmark", "$preBookmarkData $bookmarkKey ${preBookmarkData.indexOf(bookmarkKey)}")
            StationInfoPage(
                postLastStation,
                busList,
                preBookmarkData.contains(bookmarkKey)
            ) {
                when (it) {
                    StationInfoSelection.BOOKMARK -> {
                        val sharedPreferences = activity.spClient!!
                        val bookmarkData = sharedPreferences.getArrayExtension("bookmark-station")
                        // Log.d("station-bookmark", "$bookmarkData $bookmarkKey ${bookmarkData.indexOf(bookmarkKey)}")
                        if (bookmarkData.contains(bookmarkKey)) {
                            // Log.i("station-bookmark", "$bookmarkData $bookmarkKey")
                            bookmarkData.remove(bookmarkKey)
                            sharedPreferences.removeKey("$bookmarkKey-name")
                            sharedPreferences.removeKey("$bookmarkKey-type")
                            sharedPreferences.removeKey("$bookmarkKey-id")
                            sharedPreferences.removeKey("$bookmarkKey-posX")
                            sharedPreferences.removeKey("$bookmarkKey-posY")
                            sharedPreferences.removeMutableType("$bookmarkKey-stationId")
                            sharedPreferences.removeKey("$bookmarkKey-displayId")
                        } else {
                            var displayId = postLastStation.displayId
                            displayId = if (displayId is List<*>) {
                                displayId.joinToString(", ")
                            } else displayId?.toString() ?: " "

                            bookmarkData.add(bookmarkKey)
                            sharedPreferences.setString(
                                "$bookmarkKey-name",
                                postLastStation.name
                            )
                            sharedPreferences.setInt("$bookmarkKey-type", postLastStation.type)
                            sharedPreferences.setString("$bookmarkKey-id", postLastStation.id)
                            sharedPreferences.setFloat(
                                "$bookmarkKey-posX",
                                postLastStation.posX.toFloat()
                            )
                            sharedPreferences.setFloat(
                                "$bookmarkKey-posY",
                                postLastStation.posY.toFloat()
                            )
                            sharedPreferences.setMutableType(
                                "$bookmarkKey-stationId",
                                postLastStation.stationId
                            )
                            sharedPreferences.setString("$bookmarkKey-displayId", displayId)
                        }
                        activity.spClient!!.setArrayExtension("bookmark-station", bookmarkData)
                    }
                    StationInfoSelection.REFRESH -> {
                        scope.launch {
                            try {
                                busList = withContext(Dispatchers.Default) {
                                    activity.client!!.getRoute(
                                        cityCode = lastStation!!.type,
                                        id = lastStation!!.id
                                    ).await()
                                }
                                // Log.i("BusInfo", "$busList")
                            } catch (e: SocketTimeoutException) {}
                        }
                    }
                }
            }
        }
    }
}
