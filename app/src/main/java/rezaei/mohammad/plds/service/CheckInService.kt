package rezaei.mohammad.plds.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
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

    private val localRepository: LocalRepository by inject<rezaei.mohammad.plds.data.local.LocalRepository>()
    private val remoteRepository: RemoteRepository by inject<rezaei.mohammad.plds.data.remote.RemoteRepository>()
    private var locationHelper: LocationHelper? = null
    private val binder = CheckInBinder()
    private var notification: NotificationBuilder? = null
    var viewCallbacks: CheckInViewCallbacks? = null
    private var wakeLock: PowerManager.WakeLock? = null

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

    override fun onCreate() {
        super.onCreate()
        registerReceiver(broadcastReceiver, IntentFilter("android.location.PROVIDERS_CHANGED"))
    }

    private fun resumeCheckIn() {
        GlobalScope.launch(Dispatchers.Main) {
            val checkInResponse = localRepository.getCheckInResponse()
            checkInResponse?.let {
                setCheckedIn(CheckInResponse.Data().apply { fromLocal(checkInResponse) })
            }
        }
    }


    fun checkIn(checkInRequest: CheckInRequest) {
        GlobalScope.launch(Dispatchers.Main) {
            when (val response = remoteRepository.checkIn(modifyDataForTest(checkInRequest))) {
                is ApiResult.Success -> {
                    checkForLocation(response.response.data!!)
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
//            it.gPS?.radius = 50000
            it.gPS?.X = 27.9736844
            it.gPS?.Y = -26.0821684
        }

    }

    private fun checkForLocation(response: CheckInResponse.Data) {
        when {
            response.locationId != null -> {// location selected
                setCheckedIn(response)
            }
            response.locations?.isNotEmpty() == true -> {// need to select location
                if (response.locations.isNotEmpty())//user should select one location
                    viewCallbacks?.showLocationList(response.locations)
            }
        }
    }

    private fun sendLocation(location: Location) {
        if (isCheckedIn) {
            if (LocationHelper.isGpsEnable(this@CheckInService)) {
                GlobalScope.launch(Dispatchers.IO) {
                    val response = remoteRepository.userTracking(
                        UserTrackRequest(
                            checkedInLocation?.uTPId,
                            checkedInLocation?.vTUTPId,
                            Gps(location.latitude, location.longitude)
                        )
                    )
                    (response as? ApiResult.Error)?.let {
                        viewCallbacks?.onError(response.errorHandling)
                    }
                }
            } else {
                viewCallbacks?.onError(
                    ErrorHandling(
                        true,
                        errorMessage = getString(R.string.gps_not_available),
                        isSuccessful = false
                    )
                )
                locationHelper?.startTracking()
            }
        }
    }

    private fun setCheckedIn(location: CheckInResponse.Data) {
        checkedInLocation = location
        isCheckedIn = true
        viewCallbacks?.onCheckedIn(location)
        GlobalScope.launch(Dispatchers.Main) {
            delay(location.trackingConfig?.trackingInterval ?: 0)
            startGetLocation(location.trackingConfig?.trackingInterval)
        }
        saveState(location)
        showNotification(location)
        initWakeLock()
    }

    private fun initWakeLock() {
        val mgr = this.applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myAppName:MyWakeLock")
        wakeLock!!.acquire()
    }

    fun checkOut() {
        locationHelper?.getLastLocation { location ->
            if (location == null || !LocationHelper.isGpsEnable(this)) {
                viewCallbacks?.onError(
                    ErrorHandling(
                        true,
                        errorMessage = getString(R.string.gps_not_available),
                        isSuccessful = false
                    )
                )
                notification?.setCheckOutFailStatus(getString(R.string.gps_not_available))
                locationHelper?.startTracking()
                return@getLastLocation
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
                        Gps(location.latitude, location.longitude),
                        checkedInLocation?.locationType,
                        checkedInLocation?.locationName
                    )
                )
                when (response) {
                    is ApiResult.Success -> {
                        viewCallbacks?.onError(response.response.errorHandling)
                        setCheckedOut()
                    }
                    is ApiResult.Error -> {
                        viewCallbacks?.onError(response.errorHandling)
                        //restore notification status to checked in
                        notification?.setCheckOutFailStatus(response.errorHandling?.errorMessage)
                    }
                }
            }
        }
    }

    fun setCheckedOut() {
        isCheckedIn = false
        viewCallbacks?.onCheckedOut()
        checkedInLocation = null
        locationHelper?.stop()
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

    private fun startGetLocation(trackingInterval: Long?) {
        locationHelper = LocationHelper(this, trackingInterval) {
            if (isCheckedIn)
                sendLocation(it)
        }
        trackingInterval?.let { locationHelper!!.startTracking() }
        checkGpsStatus()
    }

    private fun checkGpsStatus() {
        val isGpsEnable = LocationHelper.isGpsEnable(this)

        if (isGpsEnable && isCheckedIn)
            locationHelper?.startTracking()
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
        wakeLock?.release()
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


