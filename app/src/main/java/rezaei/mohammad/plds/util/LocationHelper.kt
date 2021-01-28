package rezaei.mohammad.plds.util

import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*


class LocationHelper(
    context: Context,
    trackingInterval: Long? = null,
    private val onLocationUpdated: (Location: Location) -> Unit
) {

    private val TAG: String = LocationHelper::class.java.simpleName
    private var mLocationRequest: LocationRequest
    private var mFusedLocationClient: FusedLocationProviderClient
    private var mLocationCallback: LocationCallback? = null

    init {
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = trackingInterval?.div(2) ?: 0
        mLocationRequest.fastestInterval = trackingInterval ?: 0
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                onLocationUpdated.invoke(locationResult.lastLocation)
            }
        }
    }

    fun startTracking() {
        requestLocationUpdates()
    }

    fun stop() {
        removeLocationUpdates()
    }

    private fun requestLocationUpdates() {
        Log.i(TAG, "Requesting location updates")
        try {
            mFusedLocationClient.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback, Looper.myLooper()
            )
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permission. Could not request updates. $unlikely")
        }
    }

    private fun removeLocationUpdates() {
        Log.i(TAG, "Removing location updates")
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback)
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permission. Could not remove updates. $unlikely")
        }
    }

    fun getLastLocation(lastLocation: (Location: Location?) -> Unit) {
        try {
            mFusedLocationClient.lastLocation
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        lastLocation.invoke(task.result!!)
                    } else {
                        lastLocation.invoke(null)
                        Log.w(TAG, "Failed to get location.")
                    }
                }
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permission.$unlikely")
        }
    }

    companion object {
        fun isGpsEnable(context: Context): Boolean {
            val locationManager: android.location.LocationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
            val isGpsEnabled: Boolean =
                locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
            val isNetworkEnabled: Boolean =
                locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)

            return isGpsEnabled || isNetworkEnabled
        }
    }
}