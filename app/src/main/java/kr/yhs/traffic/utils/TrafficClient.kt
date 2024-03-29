package kr.yhs.traffic.utils

import kr.yhs.traffic.models.StationAroundInfo
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.models.StationRoute
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TrafficClient {
    @GET("bus/station")
    fun getStation(
        @Query("name") name: String,
        @Query("cityCode") cityCode: Int = 1
    ): Call<List<StationInfo>>

    @GET("/bus/station/around")
    fun getStationAround(
        @Query("posX") posX: Double,
        @Query("posY") posY: Double,
        @Query("cityCode") cityCode: Int = 1
    ): Call<List<StationAroundInfo>>

    @GET("/bus/route")
    fun getRoute(
        @Query("id") id: String,
        @Query("cityCode") cityCode: Int,
        @Query("version") version: String = "v2"
    ): Call<List<StationRoute>>
}