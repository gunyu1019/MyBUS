package kr.yhs.traffic.ui

import android.Manifest
import android.app.Activity
import android.app.RemoteInput
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Vibrator
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.input.RemoteInputIntentHelper
import androidx.wear.widget.ConfirmationOverlay
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.*
import kr.yhs.traffic.*
import kr.yhs.traffic.R
import kr.yhs.traffic.models.DropdownQuery
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.ui.components.AccompanistPager
import kr.yhs.traffic.ui.pages.*
import kr.yhs.traffic.ui.pages.pager.StationGPS
import kr.yhs.traffic.ui.pages.pager.StationSearch
import kr.yhs.traffic.ui.pages.pager.StationStar
import kr.yhs.traffic.utils.getLocation
import java.net.SocketTimeoutException


class ComposeApp(private val activity: MainActivity): BaseComposeStationInfo(activity, activity.client) {
    override fun getPreferences(filename: String): SharedPreferences = activity.getPreferences(filename)

    @OptIn(
        ExperimentalPagerApi::class, ExperimentalFoundationApi::class
    )
    @Composable
    override fun Content() {
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
        val vibrator = this.activity.getSystemService(Vibrator::class.java)
        SwipeDismissableNavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navigationController,
            startDestination = Screen.MainScreen.route
        ) {
            composable(Screen.MainScreen.route) {
                AccompanistPager(
                    scope = scope,
                    vibrator = vibrator,
                    pages = listOf({
                        this@ComposeApp.StationSearch { cityCode: Int ->
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
                if (lastStation == null) {
                    ConfirmationOverlay()
                        .setType(ConfirmationOverlay.FAILURE_ANIMATION)
                        .setMessage(activity.getText(R.string.station_not_found))
                        .showOn(activity)
                    navigationController.popBackStack()
                    return@composable
                }
                ComposeStationInfoPage(station = lastStation!!, scope = scope) { errorMessage: CharSequence ->
                    ConfirmationOverlay()
                        .setType(ConfirmationOverlay.FAILURE_ANIMATION)
                        .setMessage(errorMessage)
                        .showOn(activity)
                    navigationController.popBackStack()
                    return@ComposeStationInfoPage
                }
            }
        }
    }

    @Composable
    fun StationSearch(response: (Int) -> Unit) = StationSearch(
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
        var isLoading by remember { mutableStateOf(true) }

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
                    location = location
                )
                isLoading = false
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
        }

        val vibrator = this.activity.getSystemService(Vibrator::class.java)
        StationListPage(title, stationList, location, isLoading, true, onSuccess)
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
            val convertData = mutableListOf<StationInfo>()
            val stationAroundList = getStationAround(defaultDispatcher, location!!.longitude, location.latitude)
            for (st in stationAroundList) {
                convertData.add(
                    st.convertToStationInfo()
                )
            }
            convertData
        }
        StationListType.BOOKMARK -> {
            this.getStationBookmarkList()
        }
    }
}