package kr.yhs.traffic

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

open class BaseEncryptedSharedPreference(private val context: Context) {
    lateinit var masterKey: MasterKey

    fun getPreferences(filename: String): SharedPreferences =
        EncryptedSharedPreferences.create(
            context, filename, masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    fun masterKeyBuild() {
        masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }
}