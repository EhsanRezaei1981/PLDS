package rezaei.mohammad.plds.service

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import rezaei.mohammad.plds.data.ApiResult
import rezaei.mohammad.plds.data.RemoteRepository
import rezaei.mohammad.plds.data.model.request.CheckInRequest
import rezaei.mohammad.plds.data.model.request.CheckOutRequest
import rezaei.mohammad.plds.data.model.request.Gps
import rezaei.mohammad.plds.data.model.request.UserTrackRequest
import rezaei.mohammad.plds.data.model.response.CheckInResponse
import rezaei.mohammad.plds.data.model.response.ErrorHandling
import rezaei.mohammad.plds.data.preference.PreferenceManager
import rezaei.mohammad.plds.service.NotificationBuilder.Companion.CHECK_OUT
import rezaei.mohammad.plds.util.Event
import rezaei.mohammad.plds.util.LocationHelper

class CheckInService : Service() {

    val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _checkedInLocation = MutableLiveData<CheckInResponse.Data>()
    val checkedInLocation: LiveData<CheckInResponse.Data> = _checkedInLocation

    val _locationList = MutableLiveData<List<CheckInResponse.LocationItem>>()
    val locationList: LiveData<List<CheckInResponse.LocationItem>> = _locationList

    private val _goToManualFunctionalityEvent = MutableLiveData<Event<Unit>>()
    val goToManualFunctionalityEvent: LiveData<Event<Unit>> = _goToManualFunctionalityEvent

    private val _isCheckedIn = MutableLiveData<Boolean>()
    val isCheckedIn: LiveData<Boolean> = _isCheckedIn

    private val _errorHandling = MutableLiveData<Event<ErrorHandling?>>()
    val errorHandling: LiveData<Event<ErrorHandling?>> = _errorHandling

    private val location = MutableLiveData<Location>()

    private val pref: PreferenceManager by inject()


    private val remoteRepository: RemoteRepository by inject<rezaei.mohammad.plds.data.remote.RemoteRepository>()
    private lateinit var locationHelper: LocationHelper
    private val binder = CheckInBinder()
    private var notification: NotificationBuilder? = null

    override fun onBind(intent: Intent): IBinder {
        _dataLoading.postValue(true)
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == CHECK_OUT)
            checkOut()
        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        locationHelper = LocationHelper(this, null)
    }

    fun checkIn(checkInRequest: CheckInRequest) {
        _dataLoading.postValue(true)
        GlobalScope.launch(Dispatchers.Main) {
            when (val response = remoteRepository.checkIn(checkInRequest)) {
                is ApiResult.Success -> {
                    checkForLocation(checkInRequest.checkInPart, response.response.data)
                    _errorHandling.postValue(Event(response.response.errorHandling))
                }
                is ApiResult.Error -> {
                    if (response.errorHandling?.errorMessage == "No location found.")
                        _goToManualFunctionalityEvent.postValue(Event(Unit))
                    else
                        _errorHandling.postValue(Event(response.errorHandling))
                }
            }
            _dataLoading.postValue(false)
        }
    }

    /* private fun modifyDataForTest(checkInRequest: CheckInRequest): CheckInRequest {
         return checkInRequest.also {
 //            it.gPS?.radius = 2450 // 1 location
             it.gPS?.radius = 500000
             it.gPS?.X = 27.9736844
             it.gPS?.Y = -26.0821684
         }

     }*/

    private fun checkForLocation(checkInPart: String?, response: CheckInResponse.Data?) {
        when {
            response?.locationId != null -> {// location selected
                setCheckedIn(response)
                startGetLocation()
                if (response.trackingConfig?.trackingInterval != null)
                    startTracking(response.trackingConfig.trackingInterval)
            }
            response?.locations?.isNotEmpty() == true -> {// need to select location
                if (response.locations.size > 1)//user should select one location
                    _locationList.postValue(response.locations)
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
            if (isCheckedIn.value == true) {
                if (location.value != null) {
                    val response = remoteRepository.userTracking(
                        UserTrackRequest(
                            checkedInLocation.value?.uTPId,
                            checkedInLocation.value?.vTUTPId,
                            Gps(location.value!!.latitude, location.value!!.longitude)
                        )
                    )
                    (response as? ApiResult.Error)?.let {
                        _errorHandling.postValue(Event(response.errorHandling))
                    }
                }

                delay(trackingInterval.toLong())
                startTracking(trackingInterval)
            }
        }
    }

    private fun setCheckedIn(location: CheckInResponse.Data?) {
        _checkedInLocation.postValue(location)
        _isCheckedIn.postValue(true)
        showNotification(location)
        pref.currentLocation = CheckInResponse.LocationItem(
            location?.locationId,
            location?.locationType,
            location?.locationName
        )
    }

    fun checkOut() {
        _dataLoading.postValue(true)
        GlobalScope.launch(Dispatchers.Main) {
            notification?.setCheckingOutStatus()
            val response = remoteRepository.checkOut(
                CheckOutRequest(
                    checkedInLocation.value?.locationId,
                    checkedInLocation.value?.checkInPart,
                    checkedInLocation.value?.vTLocation,
                    checkedInLocation.value?.uTPId,
                    checkedInLocation.value?.vTUTPId,
                    Gps(location.value?.latitude, location.value?.longitude),
                    checkedInLocation.value?.locationType,
                    checkedInLocation.value?.locationName
                )
            )
            when (response) {
                is ApiResult.Success -> {
                    _errorHandling.postValue(Event(response.response.errorHandling))
                    setCheckedOut()
                }
                is ApiResult.Error -> _errorHandling.postValue(Event(response.errorHandling))
            }
            _dataLoading.postValue(false)
        }
    }

    private fun setCheckedOut() {
        _isCheckedIn.postValue(false)
        _checkedInLocation.postValue(null)
        locationHelper.liveLocation.removeObserver(locationObserver)
        locationHelper.stop()
        pref.currentLocation = null
        stopForeground(true)
        stopSelf()
    }

    private fun startGetLocation() {
        locationHelper.start(true)
        locationHelper.liveLocation.observeForever(locationObserver)
    }

    val locationObserver = Observer<Location> {
        location.postValue(it)
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
        Log.d(this::class.java.simpleName, "destroyed")
        super.onDestroy()
    }

    inner class CheckInBinder : Binder() {
        val service = this@CheckInService
    }
}

