package rezaei.mohammad.plds.views.docProgress

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import rezaei.mohammad.plds.data.Result
import rezaei.mohammad.plds.data.model.request.GetDynamicFieldsRequest
import rezaei.mohammad.plds.data.model.response.DocumentStatusResponse
import rezaei.mohammad.plds.data.model.response.FormResponse
import rezaei.mohammad.plds.data.remote.RemoteRepository
import rezaei.mohammad.plds.util.Event

class DocProgressViewModel(
    private val remoteRepository: RemoteRepository
) : ViewModel() {

    private val _documentStatus = MutableLiveData<DocumentStatusResponse.Data>()
    val documentStatus: LiveData<DocumentStatusResponse.Data> = _documentStatus

    private val _onBackPressEvent = MutableLiveData<Event<Unit>>()
    val onBackPressEvent: LiveData<Event<Unit>> = _onBackPressEvent

    private val _onYesPressEvent = MutableLiveData<Event<Result<FormResponse>>>()
    val onYesPressEvent: LiveData<Event<Result<FormResponse>>> = _onYesPressEvent

    private val _onNoPressEvent = MutableLiveData<Event<Result<FormResponse>>>()
    val onNoPressEvent: LiveData<Event<Result<FormResponse>>> = _onNoPressEvent

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    fun start(documentStatus: DocumentStatusResponse.Data) {
        _documentStatus.value = documentStatus
    }

    fun goBack() {
        _onBackPressEvent.value = Event(Unit)
    }

    fun getDynamicFieldsUnsuccessful() {
        viewModelScope.launch {
            _dataLoading.value = true
            val result = remoteRepository.getDynamicFieldsUnsuccessful(
                GetDynamicFieldsRequest(
                    _documentStatus.value?.documentStatusId,
                    _documentStatus.value?.vT
                )
            )
            _onNoPressEvent.value = Event(result)
            _dataLoading.value = false
        }
    }

    fun getDynamicFieldsSuccessful() {
        viewModelScope.launch {
            _dataLoading.value = true
            val result = remoteRepository.getDynamicFieldsSuccessful(
                GetDynamicFieldsRequest(
                    _documentStatus.value?.documentStatusId,
                    _documentStatus.value?.vT
                )
            )
            _onYesPressEvent.value = Event(result)
            _dataLoading.value = false
        }
    }
}
