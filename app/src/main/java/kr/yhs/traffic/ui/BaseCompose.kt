package kr.yhs.traffic.ui

import android.content.SharedPreferences
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import kotlinx.coroutines.*
import kr.yhs.traffic.R
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.models.StationRoute
import kr.yhs.traffic.ui.pages.StationInfoPage
import kr.yhs.traffic.ui.theme.StationInfoSelection
import kr.yhs.traffic.utils.MutableTypeSharedPreferences
import kr.yhs.traffic.utils.TrafficClient
import retrofit2.HttpException
import retrofit2.await
import java.net.SocketTimeoutException


abstract class BaseCompose(private val client: TrafficClient?): MutableTypeSharedPreferences {
    val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO

    @Composable
    abstract fun Content()

    abstract fun getPreferences(filename: String): SharedPreferences

    fun getStationBookmarkList(): MutableList<StationInfo> {
        val bookmarkStation = mutableListOf<StationInfo>()
        val sharedPreferences = getPreferences("bookmark")
        val bookmark = sharedPreferences.getStringSet("bookmark-list", mutableSetOf<String>())
        bookmark?.forEach { stationId: String ->
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
        return bookmarkStation
    }


    suspend fun getStation(dispatcher: CoroutineDispatcher, query: String, cityCode: Int) =
        withContext(dispatcher) {
            client!!.getStation(
                name = query,
                cityCode = cityCode
            ).await()
        }


    suspend fun getStationAround(
        dispatcher: CoroutineDispatcher,
        posX: Double,
        posY: Double
    ) = withContext(dispatcher) {
        client!!.getStationAround(
            posX = posX, posY = posY
        ).await()
    }


    suspend fun getRoute(dispatcher: CoroutineDispatcher, lastStation: StationInfo?) =
        withContext(dispatcher) {
            client!!.getRoute(
                cityCode = lastStation!!.type,
                id = lastStation.routeId
            ).await()
        }


    suspend fun getRoute(dispatcher: CoroutineDispatcher, id: String, cityCode: Int) =
        withContext(dispatcher) {
            client!!.getRoute(
                cityCode = cityCode,
                id = id
            ).await()
        }
}