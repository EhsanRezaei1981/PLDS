package rezaei.mohammad.plds.views.checkin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import rezaei.mohammad.plds.data.model.response.CheckInResponse

class CheckInViewModel : ViewModel() {

    val _locationList = MutableLiveData<List<CheckInResponse.LocationItem>>()
    val locationList: LiveData<List<CheckInResponse.LocationItem>> = _locationList

    val dataLoading: LiveData<Boolean> = Transformations.map(_locationList) {
        it?.isEmpty() != false
    }

    init {
        _locationList.value = null
    }

}
