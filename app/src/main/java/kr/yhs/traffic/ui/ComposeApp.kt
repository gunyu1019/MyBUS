package kr.yhs.traffic.ui

import android.app.Activity
import android.app.RemoteInput
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.input.RemoteInputIntentHelper
import androidx.wear.widget.ConfirmationOverlay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.yhs.traffic.MainActivity
import kr.yhs.traffic.R
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.module.getLocation
import kr.yhs.traffic.ui.pages.*
import retrofit2.await


@OptIn(ExperimentalWearMaterialApi::class, com.google.accompanist.pager.ExperimentalPagerApi::class)
@Composable
fun ComposeApp(activity: MainActivity) {
    var stationQuery by remember { mutableStateOf<String>("") }
    val navigationController = rememberSwipeDismissableNavController()
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if(it.resultCode == Activity.RESULT_OK) {
            Log.i("StationQuery", "${RemoteInput.getResultsFromIntent()}")
            stationQuery = it.data?.clipData.toString()
        }
    }

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
                            // navigationController.navigate(
                            //     Screen.StationList.route + "?$STATION_TYPE=${StationListType.SEARCH}",
                            // )
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
            LaunchedEffect(true) {
                stationList = withContext(Dispatchers.Default) {
                    when (stationType) {
                        StationListType.SEARCH -> {
                            activity.client!!.getStation(
                                name = stationQuery
                            ).await()
                        }
                        StationListType.GPS_LOCATION_SEARCH -> {
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
            StationList(title, stationList)
        }
    }
}
