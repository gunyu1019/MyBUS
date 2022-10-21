package kr.yhs.traffic

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import org.json.JSONArray


class SharedPreferencesClient(private val preferencesName: String, private val context: Context) {
    /* var masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build(); */

    private fun getPreferences(): SharedPreferences =
        /* EncryptedSharedPreferences.create(
            context,
            preferencesName,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ) */
        context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)

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

    fun setMutableType(key: String, values: Any?) {
        when (values) {
            is List<*> -> {
                setString("$key-type", "list")
                val value = arrayListOf<Any?>()
                for (data in values)
                    value.add(value)
                setArrayExtension("$key-array",value)
            }
            is Int -> {
                setString("$key-type", "int")
                setInt("$key-int",values)
            }
            is String -> {
                setString("$key-type", "string")
                setString("$key-int",values)
            }
            is Float -> {
                setString("$key-type", "float")
                setFloat("$key-int",values)
            }
        }
    }

    fun getMutableType(key: String, default: Any? = null): Any? {
        val typeData = getString("$key-type")
        return when (typeData) {
            "list" -> {
                val value = mutableListOf<Any?>()
                val arrayData = getArrayExtension("$key-array")
                for (data in arrayData)
                    value.add(data)
                return value
            }
            "int" -> getInt("$key-int")
            "string" -> getInt("$key-string")
            "float" -> getInt("$key-float")
            else -> default
        }
    }

    fun removeMutableType(key: String) {
        val prefs = getPreferences()
        val typeData = getString("$key-type")
        prefs.apply {
            edit {
                when (typeData) {
                    "list" -> remove("$key-array")
                    "int" -> remove("$key-int")
                    "string" -> remove("$key-string")
                    "float" -> remove("$key-float")
                }
                commit()
            }
        }
    }
}