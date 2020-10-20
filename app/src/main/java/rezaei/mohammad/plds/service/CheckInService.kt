package rezaei.mohammad.plds.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.ApiResult
import rezaei.mohammad.plds.data.LocalRepository
import rezaei.mohammad.plds.data.RemoteRepository
import rezaei.mohammad.plds.data.model.request.CheckInRequest
import rezaei.mohammad.plds.data.model.request.CheckOutRequest
import rezaei.mohammad.plds.data.model.request.Gps
import rezaei.mohammad.plds.data.model.request.UserTrackRequest
import rezaei.mohammad.plds.data.model.response.CheckInResponse
import rezaei.mohammad.plds.data.model.response.ErrorHandling
import rezaei.mohammad.plds.util.LocationHelper

interface CheckInViewCallbacks {
    fun onCheckedIn(checkedInLocation: CheckInResponse.Data)
    fun onCheckedOut()
    fun showLocationList(locationList: List<CheckInResponse.LocationItem>)
    fun onNoLocationFound()
    fun onError(errorHandling: ErrorHandling?)
}

class CheckInService : Service() {

    var checkedInLocation: CheckInResponse.Data? = null
    var isCheckedIn: Boolean = false
    private val location = MutableLiveData<Location?>()

    private val localRepository: LocalRepository by inject<rezaei.mohammad.plds.data.local.LocalRepository>()
    private val remoteRepository: RemoteRepository by inject<rezaei.mohammad.plds.data.remote.RemoteRepository>()
    private lateinit var locationHelper: LocationHelper
    private val binder = CheckInBinder()
    private var notification: NotificationBuilder? = null
    var viewCallbacks: CheckInViewCallbacks? = null

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == CHECK_OUT)
            checkOut()
        if (checkedInLocation == null && intent?.action == RESUME_PREVIOUS_CHECK_IN)
            resumeCheckIn()
        return START_NOT_STICKY
    }

    private fun resumeCheckIn() {
        GlobalScope.launch(Dispatchers.Main) {
            val checkInResponse = localRepository.getCheckInResponse()
            checkInResponse?.let {
                setCheckedIn(CheckInResponse.Data().apply { fromLocal(checkInResponse) })
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        locationHelper = LocationHelper(this, null, true)
    }

    fun checkIn(checkInRequest: CheckInRequest) {
        GlobalScope.launch(Dispatchers.Main) {
            when (val response = remoteRepository.checkIn(checkInRequest)) {
                is ApiResult.Success -> {
                    checkForLocation(checkInRequest.checkInPart, response.response.data!!)
                    viewCallbacks?.onError(response.response.errorHandling)
                }
                is ApiResult.Error -> {
                    if (response.errorHandling?.errorMessage == "No location found.")
                        viewCallbacks?.onNoLocationFound()
                    else
                        viewCallbacks?.onError(response.errorHandling)
                }
            }
        }
    }

    private fun modifyDataForTest(checkInRequest: CheckInRequest): CheckInRequest {
        return checkInRequest.also {
            //            it.gPS?.radius = 2450 // 1 location
            /*it.gPS?.radius = 500000
            it.gPS?.X = 27.9736844
            it.gPS?.Y = -26.0821684*/
        }

    }

    private fun checkForLocation(checkInPart: String?, response: CheckInResponse.Data) {
        when {
            response.locationId != null -> {// location selected
                setCheckedIn(response)
            }
            response.locations?.isNotEmpty() == true -> {// need to select location
                if (response.locations.size > 1)//user should select one location
                    viewCallbacks?.showLocationList(response.locations)
                else // check in with this location
                    with(response.locations[0]) {
                        checkIn(
                            CheckInRequest(
                                checkInPart,
                                locationId,
                                Gps(location.value?.longitude, location.value?.longitude),
                                locationType,
                                locationName
                            )
                        )
                    }
            }
        }
    }

    private fun startTracking(trackingInterval: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            delay(trackingInterval.toLong())
            if (isCheckedIn) {
                if (location.value != null && LocationHelper.isGpsEnable(this@CheckInService)) {
                    val response = remoteRepository.userTracking(
                        UserTrackRequest(
                            checkedInLocation?.uTPId,
                            checkedInLocation?.vTUTPId,
                            Gps(location.value!!.latitude, location.value!!.longitude)
                        )
                    )
                    (response as? ApiResult.Error)?.let {
                        viewCallbacks?.onError(response.errorHandling)
                    }
                } else {
                    viewCallbacks?.onError(
                        ErrorHandling(
                            true,
                            errorMessage = getString(R.string.gps_not_available),
                            isSuccessful = false
                        )
                    )
                    locationHelper.start()
                }
                startTracking(trackingInterval)
            }
        }
    }

    private fun setCheckedIn(location: CheckInResponse.Data) {
        checkedInLocation = location
        isCheckedIn = true
        viewCallbacks?.onCheckedIn(location)
        startGetLocation()
        location.trackingConfig?.trackingInterval?.let {
            startTracking(it)
        }
        saveState(location)
        showNotification(location)
    }

    fun checkOut() {
        if (!LocationHelper.isGpsEnable(this)) {
            viewCallbacks?.onError(
                ErrorHandling(
                    true,
                    errorMessage = getString(R.string.gps_not_available),
                    isSuccessful = false
                )
            )
            return
        }
        GlobalScope.launch(Dispatchers.Main) {
            notification?.setCheckingOutStatus()
            val response = remoteRepository.checkOut(
                CheckOutRequest(
                    checkedInLocation?.locationId,
                    checkedInLocation?.checkInPart,
                    checkedInLocation?.vTLocation,
                    checkedInLocation?.uTPId,
                    checkedInLocation?.vTUTPId,
                    Gps(location.value?.latitude, location.value?.longitude),
                    checkedInLocation?.locationType,
                    checkedInLocation?.locationName
                )
            )
            when (response) {
                is ApiResult.Success -> {
                    viewCallbacks?.onError(response.response.errorHandling)
                    setCheckedOut()
                }
                is ApiResult.Error -> viewCallbacks?.onError(response.errorHandling)
            }
        }
    }

    private fun setCheckedOut() {
        isCheckedIn = false
        viewCallbacks?.onCheckedOut()
        checkedInLocation = null
        locationHelper.liveLocation.removeObserver(locationObserver)
        locationHelper.stop()
        deleteState()
        stopForeground(true)
    }

    private fun saveState(location: CheckInResponse.Data) {
        GlobalScope.launch(Dispatchers.Main) {
            localRepository.insertCheckInResponse(location.toLocal())
        }
    }

    private fun deleteState() {
        GlobalScope.launch(Dispatchers.Main) {
            localRepository.deleteAllCheckInResponse()
        }
    }

    private fun startGetLocation() {
        registerReceiver(broadcastReceiver, IntentFilter("android.location.PROVIDERS_CHANGED"))
        checkGpsStatus()
        locationHelper.start()
        locationHelper.liveLocation.observeForever(locationObserver)
    }

    private val locationObserver = Observer<Location?> {
        location.postValue(it)
    }

    private fun checkGpsStatus() {
        val isGpsEnable = LocationHelper.isGpsEnable(this)

        if (!isGpsEnable)
            location.postValue(null)

        if (isGpsEnable && isCheckedIn)
            locationHelper.start()
    }

    private fun showNotification(location: CheckInResponse.Data?) {
        notification = NotificationBuilder(
            this,
            "PLDS Check In",
            "You checked in ${location?.locationName}."
        )
        startForeground(notification!!.notificationId, notification!!.getNotification().build())
    }

    override fun onDestroy() {
        stopForeground(true)
        unregisterReceiver(broadcastReceiver)
        Log.d(this::class.java.simpleName, "destroyed")
        super.onDestroy()
    }

    inner class CheckInBinder : Binder() {
        val service = this@CheckInService
    }

    companion object {
        const val CHECK_OUT = "CHECK_OUT"
        const val RESUME_PREVIOUS_CHECK_IN = "RESUME_PREVIOUS_CHECK_IN"
    }

    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "android.location.PROVIDERS_CHANGED") {
                checkGpsStatus()
            }
        }
    }
}


