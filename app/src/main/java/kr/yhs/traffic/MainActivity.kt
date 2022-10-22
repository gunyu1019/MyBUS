package kr.yhs.traffic

import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kr.yhs.traffic.module.TrafficClient
import kr.yhs.traffic.ui.ComposeApp
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    var fusedLocationClient: FusedLocationProviderClient? = null
    var client: TrafficClient? = null
    lateinit var masterKey: MasterKey

    fun getPreferences(filename: String): SharedPreferences =
        EncryptedSharedPreferences.create(
            this, filename, masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        masterKey = MasterKey.Builder(this.baseContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        val httpClient = OkHttpClient.Builder()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.yhs.kr")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()

        client = retrofit.create(TrafficClient::class.java)
        setContent {
            ComposeApp(this).Content()
        }

        if (!hasGPS()) {
            Log.i("MainActivity", "This Device has not GPS")
        } else {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        }
    }

    private fun hasGPS(): Boolean =
        packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)
}