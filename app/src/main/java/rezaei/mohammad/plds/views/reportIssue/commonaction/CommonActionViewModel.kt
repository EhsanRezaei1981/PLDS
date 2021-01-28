package rezaei.mohammad.plds.views.reportIssue.commonaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import rezaei.mohammad.plds.data.ApiResult
import rezaei.mohammad.plds.data.RemoteRepository
import rezaei.mohammad.plds.data.model.request.CommonActionReasonsRequest
import rezaei.mohammad.plds.data.model.request.FormResult
import rezaei.mohammad.plds.data.model.response.BaseResponse
import rezaei.mohammad.plds.data.model.response.CommonActionReasonsResponse
import rezaei.mohammad.plds.util.Event

class CommonActionViewModel(
    private val remoteRepository: RemoteRepository
) : ViewModel() {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _reasonList = MutableLiveData<ApiResult<CommonActionReasonsResponse>>()
    val reasonList: LiveData<ApiResult<CommonActionReasonsResponse>> = _reasonList

    private val _submitFormResult = MutableLiveData<ApiResult<BaseResponse<Unit>>>()
    val submitFormResult: LiveData<ApiResult<BaseResponse<Unit>>> = _submitFormResult

    private val _submitFormEvent = MutableLiveData<Event<Unit>>()
    val submitFormEvent: LiveData<Event<Unit>> = _submitFormEvent

    fun getReasonList(locationType: String?) {
        viewModelScope.launch {
            _dataLoading.value = true
            val apiResult =
                remoteRepository.getCommonActionReasons(CommonActionReasonsRequest(locationType))
            _reasonList.value = apiResult
            _dataLoading.value = false
        }
    }

    fun submitForm() {
        _submitFormEvent.value = Event(Unit)
    }

    fun submitForm(formResult: FormResult.CommonAction) {
        viewModelScope.launch {
            _dataLoading.value = true
            val result = remoteRepository.submitCommonActionForm(formResult)
            _submitFormResult.value = result
            _dataLoading.value = false
        }
    }
}