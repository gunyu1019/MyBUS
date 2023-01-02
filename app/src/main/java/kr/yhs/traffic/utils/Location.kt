package kr.yhs.traffic.utils

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.android.gms.location.*
import kotlinx.coroutines.tasks.await


@SuppressLint("MissingPermission")
suspend fun getLocation(
    client: FusedLocationProviderClient, update: Boolean = true
): Location? {
    var location by mutableStateOf<Location?>(null)
    location = client.lastLocation.await()
    if (location == null || update) {
        val locationRequest = LocationRequest.Builder(60000)
            .setDurationMillis(5000)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()

        client.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)
                    for (locationIndex in p0.locations) {
                        location = locationIndex
                        Log.i("Location-Requests", "$location")
                    }
                }
            },
            Looper.getMainLooper()
        ).await()
        // location = client.lastLocation.await()
    }

    return location
}