package kr.yhs.traffic.ui

import android.app.Activity
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import kr.yhs.traffic.models.StationInfo


abstract class BaseCompose(private val activity: Activity) {
    @Composable
    abstract fun Content()

    abstract fun getPreferences(filename: String): SharedPreferences

    fun getMutableType(prefs: SharedPreferences, key: String, default: Any? = null): Any? {
        return when (prefs.getString("$key-type", null)) {
            "list" -> prefs.getStringSet("$key-value", mutableSetOf())?.toList()
            "set" -> prefs.getStringSet("$key-value", mutableSetOf())
            "int" -> prefs.getInt("$key-value", 0)
            "string" -> prefs.getString("$key-value", null)
            "float" -> prefs.getFloat("$key-value", 0.0F)
            else -> default
        }
    }

    fun putMutableType(editor: SharedPreferences.Editor, key: String, values: Any) {
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

    fun removeMutableType(editor: SharedPreferences.Editor, key: String) {
        editor.remove("$key-type")
        editor.remove("$key-value")
    }

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
}