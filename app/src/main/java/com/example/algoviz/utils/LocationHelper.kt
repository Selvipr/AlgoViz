package com.example.algoviz.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object LocationHelper {

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocationAsText(context: Context): Result<String> {
        return try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            
            val location = suspendCancellableCoroutine<android.location.Location?> { cont ->
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { loc ->
                        cont.resume(loc)
                    }
                    .addOnFailureListener { exception ->
                        cont.resumeWithException(exception)
                    }
            }

            if (location != null) {
                val geocoder = Geocoder(context, Locale.getDefault())
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val city = address.locality ?: address.subAdminArea ?: ""
                    val country = address.countryName ?: ""
                    
                    val locationText = if (city.isNotEmpty() && country.isNotEmpty()) {
                        "$city, $country"
                    } else if (city.isNotEmpty()) {
                        city
                    } else if (country.isNotEmpty()) {
                        country
                    } else {
                        "Unknown Location"
                    }
                    Result.success(locationText)
                } else {
                    Result.failure(Exception("Could not find address for current coordinates."))
                }
            } else {
                Result.failure(Exception("Location not available. Make sure GPS is turned on."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
