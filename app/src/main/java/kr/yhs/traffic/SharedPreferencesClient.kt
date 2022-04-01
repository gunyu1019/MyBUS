package kr.yhs.traffic

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import org.json.JSONArray


class SharedPreferencesClient(private val preferencesName: String, private val context: Context) {
    private fun getPreferences(): SharedPreferences {
        return context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
    }

    fun setString(key: String, value: String?) {
        val prefs = getPreferences()
        prefs.apply {
            edit {
                putString(key, value)
                commit()
            }
        }
    }

    fun setBoolean(key: String, value: Boolean) {
        val prefs = getPreferences()
        prefs.apply {
            edit {
                putBoolean(key, value)
                commit()
            }
        }
    }

    fun setInt(key: String, value: Int) {
        val prefs = getPreferences()
        prefs.apply {
            edit {
                putInt(key, value)
                commit()
            }
        }
    }

    fun setFloat(key: String, value: Float) {
        val prefs = getPreferences()
        prefs.apply {
            edit {
                putFloat(key, value)
                commit()
            }
        }
    }

    fun getString(
        key: String,
        default: String? = null
    ): String? {
        val prefs = getPreferences()
        return prefs.getString(key, default)
    }

    fun getBoolean(
        key: String,
        default: Boolean = false
    ): Boolean {
        val prefs = getPreferences()
        return prefs.getBoolean(key, default)
    }

    fun getInt(
        key: String,
        default: Int = 0
    ): Int {
        val prefs = getPreferences()
        return prefs.getInt(
            key,
            default
        )
    }

    fun getFloat(
        key: String,
        default: Float = 0.0f
    ): Float {
        val prefs = getPreferences()
        return prefs.getFloat(
            key,
            default
        )
    }

    fun removeKey(key: String) {
        val prefs = getPreferences()
        prefs.apply {
            edit {
                remove(key)
                commit()
            }
        }
    }

    fun clear() {
        val prefs = getPreferences()
        prefs.apply {
            edit {
                clear()
                commit()
            }
        }
    }

    fun setArrayExtension(key: String, values: ArrayList<Any?>) {
        val jsonArray = JSONArray()
        for (v in values) {
            jsonArray.put(v)
        }
        if (values.isNotEmpty()) {
            setString(key, jsonArray.toString())
        } else {
            removeKey(key)
        }
    }

    fun getArrayExtension(key: String): ArrayList<Any?> {
        val preData = getString(key)
        val arrayList = arrayListOf<Any?>()
        if (preData != null) {
            val jsonArray = JSONArray(preData)
            for (v in 0 until jsonArray.length()) {
                arrayList.add(
                    jsonArray.opt(v)
                )
            }
        }
        return arrayList
    }
}