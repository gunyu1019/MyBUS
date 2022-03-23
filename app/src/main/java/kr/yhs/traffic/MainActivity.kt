package kr.yhs.traffic

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kr.yhs.traffic.client.TrafficClient
import kr.yhs.traffic.models.StationInfo
import kr.yhs.traffic.ui.BaseApplication
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Callable

class MainActivity : ComponentActivity() {
    var fusedLocationClient: FusedLocationProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.yhs.kr")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val client = retrofit.create(TrafficClient::class.java)

        setContent {
            BaseApplication(this)
        }

        if (!hasGPS()) {
            Log.i("MainActivity", "This Device has not GPS")
        } else {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        }
    }

    fun hasGPS(): Boolean = packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)
}