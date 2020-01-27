package rezaei.mohammad.plds.views.reportIssue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import rezaei.mohammad.plds.data.Result
import rezaei.mohammad.plds.data.local.LocalRepository
import rezaei.mohammad.plds.data.model.request.DocumentsInfoItem
import rezaei.mohammad.plds.data.model.request.FormResult
import rezaei.mohammad.plds.data.model.response.BaseResponse
import rezaei.mohammad.plds.data.model.response.CommonIssuesResponse
import rezaei.mohammad.plds.data.remote.RemoteRepository
import rezaei.mohammad.plds.util.Event

class ReportIssueViewModel(
    private val localRepository: LocalRepository,
    private val remoteRepository: RemoteRepository
) : ViewModel() {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _submitEvent = MutableLiveData<Event<Unit>>()
    val submitEvent: LiveData<Event<Unit>> = _submitEvent

    private val _submitFormEvent = MutableLiveData<Event<Result<BaseResponse<Unit>>>>()
    val submitFormEvent: LiveData<Event<Result<BaseResponse<Unit>>>> = _submitFormEvent

    private val _commonIssues = MutableLiveData<Result<CommonIssuesResponse>>()
    val commonIssues: LiveData<Result<CommonIssuesResponse>> = _commonIssues

    init {
        getCommonIssues()
    }

    fun getCommonIssues() {
        viewModelScope.launch {
            _dataLoading.value = true
            val result = remoteRepository.getCommonIssues(
                localRepository.getAllDocument()
                    .map { DocumentsInfoItem(it.docRefNo) }
            )
            _commonIssues.value = result
            _dataLoading.value = false
        }
    }

    suspend fun getDocumentList() =
        localRepository.getAllDocument()

    fun removeAllDocuments() {
        viewModelScope.launch {
            localRepository.deleteAllDocs(localRepository.getAllDocument())
        }
    }

    fun submitForm() {
        _submitEvent.value = Event(Unit)
    }

    fun submitForm(formResult: FormResult) {
        viewModelScope.launch {
            _dataLoading.value = true
            val result = remoteRepository.sendDynamicFieldResponse(formResult)
            _submitFormEvent.value = Event(result)
            _dataLoading.value = false
        }
    }
}
