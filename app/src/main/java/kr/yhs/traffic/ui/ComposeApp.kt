package kr.yhs.traffic.ui

import android.Manifest
import android.app.Activity
import android.app.RemoteInput
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.edit
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.input.RemoteInputIntentHelper
import androidx.wear.widget.ConfirmationOverlay
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.*
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
                stationQuery =
                    remoteInputResponse.getCharSequence("SEARCH_BUS_STATION", "").toString()
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
                    scope = scope,
                    pages = listOf({
                        this@ComposeApp.StationSearch(navigationController) { cityCode: Int ->
                            queryCityCode = cityCode
                            val remoteInputs = listOf(
                                RemoteInput.Builder("SEARCH_BUS_STATION")
                                    .setLabel(activity.getString(R.string.search_label))
                                    .build()
                            )
                            val intent = RemoteInputIntentHelper.createActionRemoteInputIntent()
                            RemoteInputIntentHelper.putRemoteInputsExtra(intent, remoteInputs)
                            launcher.launch(intent)
                        }
                    }, {
                        this@ComposeApp.StationGPS(navigationController)
                    }, {
                        this@ComposeApp.StationStar(navigationController)
                    })
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
                this@ComposeApp.StationListPage(
                    stationType as StationListType,
                    stationQuery,
                    queryCityCode,
                    scope,
                    onFailed = { errorMessage: CharSequence ->
                        ConfirmationOverlay()
                            .setType(ConfirmationOverlay.FAILURE_ANIMATION)
                            .setMessage(errorMessage)
                            .showOn(activity)
                        navigationController.popBackStack()
                    }, onSuccess = { station: StationInfo ->
                        lastStation = station
                        navigationController.navigate(
                            Screen.StationInfo.route,
                        )
                    }
                )

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
                val station = lastStation!!
                LaunchedEffect(true) {
                    try {
                        busList = getRoute(
                            defaultDispatcher,
                            station.routeId,
                            station.type
                        )
                    } catch (e: Exception) {
                        when (e) {
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
                val sharedPreferences = activity.getPreferences("bookmark")
                val bookmark =
                    sharedPreferences.getStringSet("bookmark-list", mutableSetOf<String>())
                        ?: mutableSetOf<String>()
                val bookmarkKey = "${station.routeId}0${station.type}"

                StationInfoPage(
                    station, busList,
                    bookmark.contains(bookmarkKey), scope
                ) {
                    when (it) {
                        StationInfoSelection.BOOKMARK -> {
                            val newBookmark = mutableSetOf<String>()
                            newBookmark.addAll(bookmark.toMutableSet())
                            if (bookmark.contains(bookmarkKey)) {
                                listOf(
                                    "name",
                                    "type",
                                    "id",
                                    "ids",
                                    "posX",
                                    "posY",
                                    "stationId",
                                    "displayId"
                                ).forEach {
                                    sharedPreferences.edit {
                                        if (sharedPreferences.contains("$bookmarkKey-$it-value"))
                                            removeMutableType(this, "$bookmarkKey-$it")
                                        else
                                            remove("$bookmarkKey-$it")
                                    }
                                }
                                newBookmark.remove(bookmarkKey)
                                sharedPreferences.edit {
                                    putStringSet("bookmark-list", newBookmark)
                                    commit()
                                }
                            } else {
                                val displayId = if (station.displayId is List<*>) {
                                    station.displayId.joinToString(", ")
                                } else station.displayId?.toString() ?: " "
                                newBookmark.add(bookmarkKey)
                                sharedPreferences.edit {
                                    putString("$bookmarkKey-name", station.name)
                                    putInt("$bookmarkKey-type", station.type)
                                    putString("$bookmarkKey-id", station.id)
                                    putString("$bookmarkKey-ids", station.ids)
                                    putFloat("$bookmarkKey-posX", station.posX.toFloat())
                                    putFloat("$bookmarkKey-posY", station.posY.toFloat())
                                    putMutableType(
                                        this,
                                        "$bookmarkKey-stationId",
                                        station.stationId
                                    )
                                    putString("$bookmarkKey-displayId", displayId)
                                    putStringSet("bookmark-list", newBookmark)
                                    commit()
                                }
                            }
                        }
                        StationInfoSelection.REFRESH -> {
                            scope.launch {
                                try {
                                    busList = getRoute(Dispatchers.Default, lastStation)
                                } catch (_: SocketTimeoutException) {}
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getMutableType(prefs: SharedPreferences, key: String, default: Any? = null): Any? {
        return when (prefs.getString("$key-type", null)) {
            "list" -> prefs.getStringSet("$key-value", mutableSetOf())?.toList()
            "set" -> prefs.getStringSet("$key-value", mutableSetOf())
            "int" -> prefs.getInt("$key-value", 0)
            "string" -> prefs.getString("$key-value", null)
            "float" -> prefs.getFloat("$key-value", 0.0F)
            else -> default
        }
    }

    private fun putMutableType(editor: SharedPreferences.Editor, key: String, values: Any) {
        when {
            values is List<*> && values.all { it is String } -> {
                editor.putString("$key-type", "list")
                val dumpSet = mutableSetOf<String>()
                values.forEach { dumpSet.add(it as String) }
                editor.putStringSet("$key-value", dumpSet)
            }
            values is MutableSet<*> && values.all { it is String } -> {
                editor.putString("$key-type", "set")
                val dumpSet = mutableSetOf<String>()
                values.forEach { dumpSet.add(it as String) }
                editor.putStringSet("$key-value", dumpSet)
            }
            values is Int -> {
                editor.putString("$key-type", "int")
                editor.putInt("$key-value", values)
            }
            values is String -> {
                editor.putString("$key-type", "string")
                editor.putString("$key-value", values)
            }
            values is Float -> {
                editor.putString("$key-type", "float")
                editor.putFloat("$key-value", values)
            }
        }
    }

    private fun removeMutableType(editor: SharedPreferences.Editor, key: String) {
        editor.remove("$key-type")
        editor.remove("$key-value")
    }


    private suspend fun getStation(dispatcher: CoroutineDispatcher, query: String, cityCode: Int) =
        withContext(dispatcher) {
            activity.client!!.getStation(
                name = query,
                cityCode = cityCode
            ).await()
        }


    private suspend fun getStationAround(
        dispatcher: CoroutineDispatcher,
        posX: Double,
        posY: Double
    ) = withContext(dispatcher) {
        activity.client!!.getStationAround(
            posX = posX, posY = posY
        ).await()
    }


    private suspend fun getRoute(dispatcher: CoroutineDispatcher, lastStation: StationInfo?) =
        withContext(dispatcher) {
            activity.client!!.getRoute(
                cityCode = lastStation!!.type,
                id = lastStation.routeId
            ).await()
        }


    private suspend fun getRoute(dispatcher: CoroutineDispatcher, id: String, cityCode: Int) =
        withContext(dispatcher) {
            activity.client!!.getRoute(
                cityCode = cityCode,
                id = id
            ).await()
        }

    @Composable
    fun StationSearch(navigationController: NavController, response: (Int) -> Unit) = StationSearch(
        activity.getString(R.string.station_search_title),
        activity.getString(R.string.station_search_description),
        items = listOf(
            DropdownQuery(activity.getString(R.string.item_metropolitan), 1),
            DropdownQuery(activity.getString(R.string.item_buc), 3)
        ),
        response
    )


    @Composable
    fun StationGPS(navigationController: NavController) = StationGPS(
        activity.getString(R.string.station_gps_title),
        activity.getString(R.string.station_gps_description)
    ) {
        navigationController.navigate(
            Screen.StationList.route + "?$STATION_TYPE=${StationListType.GPS_LOCATION_SEARCH}",
        )
    }


    @Composable
    fun StationStar(navigationController: NavController) = StationStar(
        activity.getString(R.string.station_star_title),
        activity.getString(R.string.station_star_description)
    ) {
        navigationController.navigate(
            Screen.StationList.route + "?$STATION_TYPE=${StationListType.BOOKMARK}",
        )
    }


    @Composable
    fun StationListPage(
        stationType: StationListType,
        query: String,
        cityCode: Int,
        scope: CoroutineScope = rememberCoroutineScope(),
        onFailed: (CharSequence) -> Unit,
        onSuccess: (StationInfo) -> Unit
    ) {
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
                        onFailed(activity.getText(R.string.gps_permission))
                        return@LaunchedEffect
                    }
                }
                location = getLocation(
                    activity.fusedLocationClient!!,
                    stationType == StationListType.GPS_LOCATION_SEARCH
                )
            }
            // Log.i("location", "$location")
            if (location == null && stationType == StationListType.GPS_LOCATION_SEARCH) {
                onFailed(activity.getText(R.string.gps_not_found))
                return@LaunchedEffect
            }
            try {
                stationList = this@ComposeApp.getStationList(
                    stationType = stationType,
                    query = query,
                    cityCode = cityCode,
                    location = location!!
                )
            } catch (e: SocketTimeoutException) {
                onFailed(activity.getText(R.string.timeout))
                return@LaunchedEffect
            }
        }
        if (activity.fusedLocationClient == null && stationType == StationListType.GPS_LOCATION_SEARCH) {
            onFailed(activity.getText(R.string.gps_not_found))
            return
        }

        val title = when (stationType) {
            StationListType.SEARCH -> activity.getString(R.string.title_search, query)
            StationListType.GPS_LOCATION_SEARCH -> activity.getString(R.string.title_gps_location)
            StationListType.BOOKMARK -> activity.getString(R.string.title_bookmark)
            else -> activity.getString(R.string.title_search)
        }
        StationListPage(title, stationList, location, scope, onSuccess)
    }

    private suspend fun getStationList(
        stationType: StationListType,
        query: String,
        cityCode: Int,
        location: Location? = null
    ) = when (stationType) {
        StationListType.SEARCH -> {
            this@ComposeApp.getStation(defaultDispatcher, query, cityCode)
        }
        StationListType.GPS_LOCATION_SEARCH -> {
            // location != null
            val convertData = mutableListOf<StationInfo>()
            val stationAroundList =
                getStationAround(defaultDispatcher, location!!.longitude, location.latitude)
            for (st in stationAroundList) {
                convertData.add(
                    st.convertToStationInfo()
                )
            }
            convertData
        }
        StationListType.BOOKMARK -> {
            val bookmarkStation = mutableListOf<StationInfo>()
            val sharedPreferences = activity.getPreferences("bookmark")
            val bookmark = sharedPreferences.getStringSet("bookmark-list", mutableSetOf<String>())
            bookmark?.forEach { stationId: String ->
                // Log.i("Bookmark", stationId)
                bookmarkStation.add(
                    StationInfo(
                        sharedPreferences.getString("$stationId-name", "알 수 없음") ?: "알 수 없음",
                        sharedPreferences.getString("$stationId-id", null) ?: "-2",
                        sharedPreferences.getString("$stationId-ids", null),
                        sharedPreferences.getFloat("$stationId-posX", 0.0F).toDouble(),
                        sharedPreferences.getFloat("$stationId-posY", 0.0F).toDouble(),
                        sharedPreferences.getString("$stationId-displayId", null) ?: "0",
                        getMutableType(sharedPreferences, "$stationId-stationId", null) ?: "0",
                        sharedPreferences.getInt("$stationId-type", 0),
                    )
                )
            }
            bookmarkStation
        }
    }
}