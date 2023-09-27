package kr.yhs.traffic.utils

import kr.yhs.traffic.models.StationAroundInfo
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.models.StationRoute
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

class ClientBuilder {
    lateinit var httpClient: OkHttpClient.Builder

    fun httpClientBuild() = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.MINUTES)
        .readTimeout(5, TimeUnit.MINUTES)
        .writeTimeout(5,TimeUnit.MINUTES)

    fun build(): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.yhs.kr")
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient.build())
        .build()
}