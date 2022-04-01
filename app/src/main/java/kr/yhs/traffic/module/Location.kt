package kr.yhs.traffic.module

import android.annotation.SuppressLint
import android.app.Activity
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.tasks.await


@SuppressLint("MissingPermission")
suspend fun getLocation(
    activity: Activity, client: FusedLocationProviderClient, update: Boolean = true
): Location? {
    var location by mutableStateOf<Location?>(null)
    location = client.lastLocation.await()
    if (location == null || update) {
        val locationRequest = LocationRequest.create()
        locationRequest.interval = 60000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

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
        location = client.lastLocation.await()
    }

    return location
}