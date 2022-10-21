package kr.yhs.traffic.ui

import android.Manifest
import android.app.Activity
import android.app.RemoteInput
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.input.RemoteInputIntentHelper
import androidx.wear.widget.ConfirmationOverlay
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.yhs.traffic.MainActivity
import kr.yhs.traffic.R
import kr.yhs.traffic.models.DropdownQuery
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.models.StationRoute
import kr.yhs.traffic.module.getLocation
import kr.yhs.traffic.ui.navigator.StationGPS
import kr.yhs.traffic.ui.navigator.StationSearch
import kr.yhs.traffic.ui.navigator.StationStar
import kr.yhs.traffic.ui.pages.*
import kr.yhs.traffic.ui.theme.StationInfoSelection
import retrofit2.HttpException
import retrofit2.await
import java.net.SocketTimeoutException


class ComposeApp(private val activity: MainActivity) {
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
    private val bookmarkArrayKey = "bookmark-station"

    @OptIn(
        com.google.accompanist.pager.ExperimentalPagerApi::class,
        com.google.accompanist.permissions.ExperimentalPermissionsApi::class
    )
    @Composable
    fun Content() {
        var stationQuery by remember { mutableStateOf("") }
        var queryCityCode by remember { mutableStateOf(1) }
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
                    scope,
                    listOf({
                        StationSearch(
                            activity.getString(R.string.station_search_title),
                            activity.getString(R.string.station_search_description),
                            items = listOf(
                                DropdownQuery(activity.getString(R.string.item_metropolitan), 1),
                                DropdownQuery(activity.getString(R.string.item_buc), 3)
                            )
                        ) { cityCode: Int ->
                            queryCityCode = cityCode
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
                        StationGPS(
                            activity.getString(R.string.station_gps_title),
                            activity.getString(R.string.station_gps_description)
                        ) {
                            navigationController.navigate(
                                Screen.StationList.route + "?$STATION_TYPE=${StationListType.GPS_LOCATION_SEARCH}",
                            )
                        }
                    }, {
                        StationStar(
                            activity.getString(R.string.station_star_title),
                            activity.getString(R.string.station_star_description)
                        ) {
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
                val stationType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    it.arguments?.getSerializable(STATION_TYPE, StationListType::class.java)
                } else {
                    it.arguments?.getSerializable(STATION_TYPE)
                }
                var stationList by remember { mutableStateOf<List<StationInfo>>(emptyList()) }
                var location by remember { mutableStateOf<Location?>(null) }

                val permissionResult = rememberMultiplePermissionsState(
                    listOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
                LaunchedEffect(true) {
                    if (activity.fusedLocationClient != null) {
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
                                    .setMessage(activity.getText(R.string.gps_permission))
                                    .showOn(activity)
                                navigationController.popBackStack()
                                return@LaunchedEffect
                            }
                        }
                        location = getLocation(
                            activity.fusedLocationClient!!,
                            stationType == StationListType.GPS_LOCATION_SEARCH
                        )
                    }
                    Log.i("location", "$location")
                    if (location == null && stationType == StationListType.GPS_LOCATION_SEARCH) {
                        ConfirmationOverlay()
                            .setType(ConfirmationOverlay.FAILURE_ANIMATION)
                            .setMessage(activity.getText(R.string.gps_not_found))
                            .showOn(activity)
                        navigationController.popBackStack()
                        return@LaunchedEffect
                    }
                    try {
                        stationList = when (stationType) {
                            StationListType.SEARCH -> {
                                getStation(defaultDispatcher, stationQuery, queryCityCode)
                            }
                            StationListType.GPS_LOCATION_SEARCH -> {
                                val convertData = mutableListOf<StationInfo>()
                                val stationAroundList = getStationAround(defaultDispatcher, location!!.longitude, location!!.latitude)
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
                                val bookmarkData = sharedPreferences.getArrayExtension("bookmark-station")
                                for (stationId in bookmarkData) {
                                    bookmarkStation.add(
                                        StationInfo(
                                            sharedPreferences.getString("$stationId-name") ?: activity.getString(R.string.unknown),
                                            sharedPreferences.getString("$stationId-id") ?: "-2",
                                            sharedPreferences.getString("$stationId-ids"),
                                            sharedPreferences.getFloat("$stationId-posX").toDouble(),
                                            sharedPreferences.getFloat("$stationId-posY").toDouble(),
                                            sharedPreferences.getString(
                                                "$stationId-displayId",
                                                "0"
                                            ),
                                            sharedPreferences.getMutableType("$stationId-stationId")
                                                ?: "0",
                                            sharedPreferences.getInt("$stationId-type")
                                        )
                                    )
                                }
                                // Log.i("stationBookmark", "$bookmarkData $bookmarkStation")
                                sharedPreferences.setArrayExtension(
                                    bookmarkArrayKey,
                                    bookmarkData
                                )
                                bookmarkStation
                            }
                            else -> listOf()
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
                if (activity.fusedLocationClient == null && stationType == StationListType.GPS_LOCATION_SEARCH) {
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
                StationListPage(title, stationList, location, scope) { station: StationInfo ->
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
                    return@composable
                }
                val postLastStation = lastStation!!
                LaunchedEffect(true) {
                    try {
                        busList = getRoute(
                            defaultDispatcher,
                            postLastStation.routeId,
                            postLastStation.type
                        )
                    } catch (e: Exception) {
                        when(e) {
                            is SocketTimeoutException, is HttpException -> {
                                ConfirmationOverlay()
                                    .setType(ConfirmationOverlay.FAILURE_ANIMATION)
                                    .setMessage(activity.getText(R.string.timeout))
                                    .showOn(activity)
                                navigationController.popBackStack()
                                return@LaunchedEffect
                            }
                            else -> throw e
                        }
                    }
                    // Log.i("BusInfo", "$busList")
                }
                val preBookmarkData = activity.spClient!!.getArrayExtension(bookmarkArrayKey)
                val bookmarkKey = "${postLastStation.routeId}0${postLastStation.type}"
                // Log.i("station-bookmark", "$preBookmarkData $bookmarkKey ${preBookmarkData.indexOf(bookmarkKey)}")
                StationInfoPage(
                    postLastStation,
                    busList,
                    preBookmarkData.contains(bookmarkKey),
                    scope
                ) {
                    when (it) {
                        StationInfoSelection.BOOKMARK -> {
                            val sharedPreferences = activity.spClient!!
                            val bookmarkData = sharedPreferences.getArrayExtension(bookmarkArrayKey)
                            // Log.d("station-bookmark", "$bookmarkData $bookmarkKey ${bookmarkData.indexOf(bookmarkKey)}")
                            if (bookmarkData.contains(bookmarkKey)) {
                                bookmarkData.remove(bookmarkKey)
                                sharedPreferences.removeKey("$bookmarkKey-name")
                                sharedPreferences.removeKey("$bookmarkKey-type")
                                sharedPreferences.removeKey("$bookmarkKey-id")
                                sharedPreferences.removeKey("$bookmarkKey-ids")
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
                                sharedPreferences.setString("$bookmarkKey-id", postLastStation.ids)
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
                            activity.spClient!!.setArrayExtension(bookmarkArrayKey, bookmarkData)
                        }
                        StationInfoSelection.REFRESH -> {
                            scope.launch {
                                try {
                                    busList = getRoute(Dispatchers.Default, lastStation)
                                }
                                catch (_: SocketTimeoutException) {}
                            }
                        }
                    }
                }
            }
        }
    }


    private suspend fun getStation(dispatcher: CoroutineDispatcher, query: String, cityCode: Int) = withContext(dispatcher) {
        activity.client!!.getStation(
            name = query,
            cityCode = cityCode
        ).await()
    }


    private suspend fun getStationAround(dispatcher: CoroutineDispatcher, posX: Double, posY: Double) = withContext(dispatcher) {
        activity.client!!.getStationAround(
            posX = posX, posY = posY
        ).await()
    }


    private suspend fun getRoute(dispatcher: CoroutineDispatcher, lastStation: StationInfo?) = withContext(dispatcher) {
        activity.client!!.getRoute(
            cityCode = lastStation!!.type,
            id = lastStation.routeId
        ).await()
    }


    private suspend fun getRoute(dispatcher: CoroutineDispatcher, id: String, cityCode: Int) = withContext(dispatcher) {
        activity.client!!.getRoute(
            cityCode = cityCode,
            id = id
        ).await()
    }
}