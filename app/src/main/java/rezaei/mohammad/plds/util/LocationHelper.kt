package rezaei.mohammad.plds.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yayandroid.locationmanager.LocationManager
import com.yayandroid.locationmanager.configuration.DefaultProviderConfiguration
import com.yayandroid.locationmanager.configuration.GooglePlayServicesConfiguration
import com.yayandroid.locationmanager.configuration.LocationConfiguration
import com.yayandroid.locationmanager.configuration.PermissionConfiguration
import com.yayandroid.locationmanager.listener.LocationListener

class LocationHelper(
    context: Context,
    activity: Activity?,
    trackMode: Boolean = false
) {

    private var locationManager: LocationManager? = null
    private val _liveLocation = MutableLiveData<Location>()
    val liveLocation: LiveData<Location?> = _liveLocation

    init {
        val awesomeConfiguration = LocationConfiguration.Builder()
            .keepTracking(trackMode)
            .askForPermission(
                PermissionConfiguration.Builder()
                    .rationaleMessage("Please accept location permission.")
                    .requiredPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
                    .build()
            )
            .useGooglePlayServices(
                GooglePlayServicesConfiguration.Builder()
                    .fallbackToDefault(true)
                    .askForGooglePlayServices(false)
                    .askForSettingsApi(true)
                    .failOnConnectionSuspended(true)
                    .failOnSettingsApiSuspended(false)
                    .ignoreLastKnowLocation(false)
                    .build()
            )
            .useDefaultProviders(
                DefaultProviderConfiguration.Builder()
                    .build()
            )
            .build()
        locationManager = LocationManager.Builder(context.applicationContext)
            .activity(activity)
            .configuration(awesomeConfiguration)
            .notify(object : LocationListener {
                override fun onLocationChanged(location: Location?) {
                    _liveLocation.postValue(location)
                    Log.d(
                        this@LocationHelper::class.java.simpleName,
                        "Location changed: ${location.toString()}"
                    )
                }

                override fun onPermissionGranted(alreadyHadPermission: Boolean) {
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                    Log.d(
                        this@LocationHelper::class.java.simpleName,
                        "Status changed: $provider $status "
                    )
                }

                override fun onProviderEnabled(provider: String?) {
                    Log.d(
                        this@LocationHelper::class.java.simpleName,
                        "provider enabled: $provider "
                    )
                }

                override fun onProviderDisabled(provider: String?) {
                    Log.d(
                        this@LocationHelper::class.java.simpleName,
                        "provider disabled $provider "
                    )
                }

                override fun onProcessTypeChanged(processType: Int) {
                    Log.d(
                        this@LocationHelper::class.java.simpleName,
                        "processChanged: $processType "
                    )
                }

                override fun onLocationFailed(type: Int) {
                    _liveLocation.postValue(null)
                    Log.d(this@LocationHelper::class.java.simpleName, "Location failed: $type ")
                }
            })
            .build()
    }

    fun start() {
        if (locationManager?.isAnyDialogShowing == false)
            locationManager?.get()
    }

    fun stop() {
        locationManager?.cancel()
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