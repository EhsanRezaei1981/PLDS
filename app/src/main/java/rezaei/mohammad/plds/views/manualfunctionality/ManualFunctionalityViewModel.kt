package rezaei.mohammad.plds.views.manualfunctionality

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import rezaei.mohammad.plds.data.ApiResult
import rezaei.mohammad.plds.data.RemoteRepository
import rezaei.mohammad.plds.data.model.response.CourtResponse
import rezaei.mohammad.plds.data.model.response.SheriffResponse
import rezaei.mohammad.plds.util.Event

class ManualFunctionalityViewModel(
    private val remoteRepository: RemoteRepository
) : ViewModel() {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _getCourtsEvent = MutableLiveData<ApiResult<CourtResponse>>()
    val getCourtsEvent: LiveData<ApiResult<CourtResponse>> = _getCourtsEvent

    private val _getSheriffsEvent = MutableLiveData<ApiResult<SheriffResponse>>()
    val getSheriffsEvent: LiveData<ApiResult<SheriffResponse>> = _getSheriffsEvent

    private val _submitEvent = MutableLiveData<Event<Int?>>()
    val submitEvent: LiveData<Event<Int?>> = _submitEvent

    fun getCourts() {
        viewModelScope.launch {
            if (_getCourtsEvent.value is ApiResult.Success<CourtResponse>) {
                _getCourtsEvent.postValue(_getCourtsEvent.value)
                return@launch
            }
            _dataLoading.value = true
            val response = remoteRepository.getCourts(Unit)
            _getCourtsEvent.value = response
            _dataLoading.value = false
        }
    }

    fun getSheriffs() {
        viewModelScope.launch {
            if (_getSheriffsEvent.value is ApiResult.Success<SheriffResponse>) {
                _getSheriffsEvent.postValue(_getSheriffsEvent.value)
                return@launch
            }
            _dataLoading.value = true
            val response = remoteRepository.getSheriffs(Unit)
            _getSheriffsEvent.value = response
            _dataLoading.value = false
        }
    }

    fun submitForm(checkedItemId: Int?) {
        _submitEvent.value = Event(checkedItemId)
    }
}