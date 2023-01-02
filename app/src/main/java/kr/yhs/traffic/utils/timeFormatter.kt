package kr.yhs.traffic.utils

import android.content.Context
import kr.yhs.traffic.R

fun timeFormatter (context: Context, timeMillis: Int, detail: Boolean): String = when {
    timeMillis / 60 < 1 ->
        context.getString(R.string.timestamp_second, timeMillis)
    timeMillis / 3600 < 1 && (timeMillis % 60 == 0 || !detail) ->
        context.getString(R.string.timestamp_minute, timeMillis / 60)
    timeMillis / 3600 < 1 && detail -> context.getString(
        R.string.timestamp_minute_second, timeMillis / 60, timeMillis % 60)
    timeMillis / 216000 < 1 ->
        context.getString(R.string.timestamp_hour_minute, timeMillis / 3600, timeMillis % 3600 / 60)
    else -> context.getString(R.string.timestamp_second, timeMillis)
}