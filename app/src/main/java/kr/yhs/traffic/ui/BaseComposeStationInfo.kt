package kr.yhs.traffic.ui

import android.app.Activity
import androidx.compose.runtime.*
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.yhs.traffic.R
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.models.StationRoute
import kr.yhs.traffic.ui.pages.StationInfoPage
import kr.yhs.traffic.ui.theme.StationInfoSelection
import kr.yhs.traffic.utils.TrafficClient
import retrofit2.HttpException
import java.net.SocketTimeoutException


abstract class BaseComposeStationInfo(
    private val activity: Activity,
    client: TrafficClient?
) : BaseCompose(client) {
    @Composable
    fun ComposeStationInfoPage(
        station: StationInfo,
        scope: CoroutineScope,
        isTile: Boolean = false,
        onFailed: (CharSequence) -> Unit
    ) {
        var busList by remember { mutableStateOf<List<StationRoute>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }

        LaunchedEffect(true) {
            try {
                busList = getRoute(
                    defaultDispatcher,
                    station.routeId,
                    station.type
                )
                isLoading = false
            } catch (e: Exception) {
                when (e) {
                    is SocketTimeoutException, is HttpException -> {
                        onFailed(activity.getText(R.string.timeout))
                        return@LaunchedEffect
                    }
                    else -> throw e
                }
            }
        }
        val sharedPreferences = getPreferences("bookmark")
        val bookmark = sharedPreferences.getStringSet("bookmark-list", mutableSetOf<String>())?: mutableSetOf<String>()
        val bookmarkKey = "${station.routeId}0${station.type}"

        StationInfoPage(
            stationInfo = station,
            busInfo = busList,
            starActive = bookmark.contains(bookmarkKey),
            isLoading = isLoading,
            buttonList = if (isTile) listOf(StationInfoSelection.EXIT, StationInfoSelection.REFRESH)
            else listOf(StationInfoSelection.BOOKMARK, StationInfoSelection.REFRESH)
        ) {
            when (it) {
                StationInfoSelection.BOOKMARK -> {
                    val newBookmark = mutableSetOf<String>()
                    newBookmark.addAll(bookmark.toMutableSet())
                    if (bookmark.contains(bookmarkKey)) {
                        listOf(
                            "name", "type", "id", "ids", "posX", "posY", "stationId", "displayId"
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
                            busList = getRoute(Dispatchers.Default, station)
                        } catch (_: SocketTimeoutException) {}
                    }
                }
                StationInfoSelection.EXIT -> {
                    activity.finish()
                }
            }
        }
    }
}