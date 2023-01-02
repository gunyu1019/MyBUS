package kr.yhs.traffic.utils

import android.content.SharedPreferences
import kr.yhs.traffic.models.StationInfo

interface StationPreferences: MutableTypeSharedPreferences {
    fun getStationInfo(preference: SharedPreferences) = StationInfo(
        preference.getString("station-name", "알 수 없음") ?: "알 수 없음",
        preference.getString("station-id", null) ?: "-2",
        preference.getString("station-ids", null),
        preference.getFloat("station-posX", 0.0F).toDouble(),
        preference.getFloat("station-posY", 0.0F).toDouble(),
        preference.getString("station-displayId", null) ?: "0",
        getMutableType(preference, "station-stationId", null) ?: "0",
        preference.getInt("station-type", 0),
    )
}