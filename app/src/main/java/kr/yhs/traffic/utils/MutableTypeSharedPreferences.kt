package kr.yhs.traffic.utils

import android.content.SharedPreferences


interface MutableTypeSharedPreferences {
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
}