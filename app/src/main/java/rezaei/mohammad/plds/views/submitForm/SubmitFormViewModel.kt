package rezaei.mohammad.plds.views.submitForm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import rezaei.mohammad.plds.data.Result
import rezaei.mohammad.plds.data.local.LocalRepository
import rezaei.mohammad.plds.data.model.local.DocumentType
import rezaei.mohammad.plds.data.model.request.FormResult
import rezaei.mohammad.plds.data.model.response.BaseResponse
import rezaei.mohammad.plds.data.model.response.CourtResponse
import rezaei.mohammad.plds.data.model.response.SheriffResponse
import rezaei.mohammad.plds.data.remote.RemoteRepository
import rezaei.mohammad.plds.util.Event

class SubmitFormViewModel(
    private val localRepository: LocalRepository,
    private val remoteRepository: RemoteRepository
) : ViewModel() {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _getCourtsEvent = MutableLiveData<Event<Result<CourtResponse>>>()
    val getCourtsEvent: LiveData<Event<Result<CourtResponse>>> = _getCourtsEvent

    private val _getSheriffsEvent = MutableLiveData<Event<Result<SheriffResponse>>>()
    val getSheriffsEvent: LiveData<Event<Result<SheriffResponse>>> = _getSheriffsEvent

    private val _submitEvent = MutableLiveData<Event<Unit>>()
    val submitEvent: LiveData<Event<Unit>> = _submitEvent

    private val _submitFormEvent = MutableLiveData<Event<Result<BaseResponse<Unit>>>>()
    val submitFormEvent: LiveData<Event<Result<BaseResponse<Unit>>>> = _submitFormEvent

    private val _isMultiDoc = MutableLiveData<Boolean>()
    val isMultiDoc: LiveData<Boolean> = _isMultiDoc

    init {
        isMultiDoc()
    }

    fun submitForm() {
        _submitEvent.value = Event(Unit)
    }

    fun getCourts() {
        viewModelScope.launch {
            val response = remoteRepository.getCourts(Unit)
            _getCourtsEvent.value = Event(response)
        }
    }

    fun getSheriffs() {
        viewModelScope.launch {
            val response = remoteRepository.getSheriffs(Unit)
            _getSheriffsEvent.value = Event(response)
        }
    }

    suspend fun getDocumentList() =
        localRepository.getAllDocument(DocumentType.CheckProgress)

    fun submitForm(formResult: FormResult) {
        viewModelScope.launch {
            _dataLoading.value = true
            val result = remoteRepository.sendDynamicFieldResponse(formResult)
            _submitFormEvent.value = Event(result)
            _dataLoading.value = false
        }
    }

    fun removeAllDocuments() {
        viewModelScope.launch {
            localRepository.deleteAllDocs(getDocumentList())
        }
    }

    private fun isMultiDoc() {
        viewModelScope.launch {
            _isMultiDoc.value = getDocumentList().size > 1
        }
    }

}
