package com.tasnimulhasan.common.extfun

import android.app.Activity
import android.content.Context
import android.location.LocationManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority

fun Activity.createLocationRequest() {
    val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        10000
    ).setMinUpdateIntervalMillis(5000).build()
    val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
    val client = LocationServices.getSettingsClient(this)
    val task = client.checkLocationSettings(builder.build())
    task.addOnSuccessListener {

    }
    task.addOnFailureListener {
        if (it is ResolvableApiException) {
            try {
                it.startResolutionForResult(this, 107)
            } catch (sendEx: java.lang.Exception) {
                sendEx.printStackTrace()
            }
        }
    }
}

fun Activity.isLocationEnabled() : Boolean {
    val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    try {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}