package rezaei.mohammad.plds.views.manageDoc.editDoc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import rezaei.mohammad.plds.data.ApiResult
import rezaei.mohammad.plds.data.RemoteRepository
import rezaei.mohammad.plds.data.model.request.FormResult
import rezaei.mohammad.plds.data.model.request.RespondedFieldsRequest
import rezaei.mohammad.plds.data.model.response.BaseResponse
import rezaei.mohammad.plds.data.model.response.FormResponse
import rezaei.mohammad.plds.util.Event

class EditDocumentViewModel(
    private val remoteRepository: RemoteRepository
) : ViewModel() {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _fieldsResult = MutableLiveData<ApiResult<FormResponse>>()
    val fieldsResult: LiveData<ApiResult<FormResponse>> = _fieldsResult

    private val _submitEvent = MutableLiveData<Event<Unit>>()
    val submitEvent: LiveData<Event<Unit>> = _submitEvent

    private val _submitFormEvent = MutableLiveData<Event<ApiResult<BaseResponse<Unit>>>>()
    val submitFormEvent: LiveData<Event<ApiResult<BaseResponse<Unit>>>> = _submitFormEvent

    fun getRespondedFields(documentStatusId: Int, vt: String, readOnly: Boolean, type: String) {
        viewModelScope.launch {
            _dataLoading.value = true
            val result = if (readOnly)
                remoteRepository.getRespondedFields(
                    RespondedFieldsRequest(documentStatusId, vt, type)
                )
            else
                remoteRepository.getStatusSuccesses(
                    RespondedFieldsRequest(documentStatusId, vt, type, true)
                )

            _fieldsResult.value = result
            _dataLoading.value = false
        }
    }

    fun submitForm() {
        _submitEvent.value = Event(Unit)
    }

    fun submitForm(formResult: FormResult.RespondedFields) {
        viewModelScope.launch {
            _dataLoading.value = true
            val result = remoteRepository.updateRespondedFields(formResult)
            _submitFormEvent.value = Event(result)
            _dataLoading.value = false
        }
    }
}