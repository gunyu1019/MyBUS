package kr.yhs.traffic.module

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.tasks.await


suspend fun getLocation(
    activity: Activity, client: FusedLocationProviderClient
): Location? {
    if (ActivityCompat.checkSelfPermission(
            activity, Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            activity, Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        val permissionRequest = rememberMultiplePermissionsState(
            listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
        return null
    }
    return client.lastLocation.await()
}