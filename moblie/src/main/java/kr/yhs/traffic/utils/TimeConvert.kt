package kr.yhs.traffic.utils

import android.net.RouteInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import kr.yhs.traffic.R
import kr.yhs.traffic.models.ArrivalInfo
import kr.yhs.traffic.models.StationRoute


@Composable
fun timeToString(timeMillis: Int, busInfo: StationRoute) = when {
    timeMillis / 60 < 1 -> LocalContext.current.getString(R.string.timestamp_second, timeMillis)
    timeMillis / 3600 < 1 && (timeMillis % 60 == 0 || (busInfo.type in 1200..1299) || (busInfo.type in 2100..2199)) ->
        LocalContext.current.getString(R.string.timestamp_minute, timeMillis / 60)
    timeMillis / 3600 < 1 && (busInfo.type in 1100..1199 || busInfo.type in 1300..1399) ->
        LocalContext.current.getString(R.string.timestamp_minute_second, timeMillis / 60, timeMillis % 60)
    timeMillis / 216000 < 1 -> LocalContext.current.getString(
        R.string.timestamp_hour_minute,
        timeMillis / 3600,
        timeMillis % 3600 / 60
    )
    else -> LocalContext.current.getString(R.string.timestamp_second, timeMillis)
}