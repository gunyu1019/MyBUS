package kr.yhs.traffic

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit


class SharedPreferencesClient(private val preferencesName: String, private val context: Context) {
    private fun getPreferences(): SharedPreferences {
        return context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
    }

    fun setString(key: String, value: String) {
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

    fun getString(
        key: String,
        default: String = ""
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
}