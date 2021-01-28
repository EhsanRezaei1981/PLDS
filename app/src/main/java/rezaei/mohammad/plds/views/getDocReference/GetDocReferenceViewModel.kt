package rezaei.mohammad.plds.views.getDocReference

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import rezaei.mohammad.plds.data.ApiResult
import rezaei.mohammad.plds.data.local.LocalRepository
import rezaei.mohammad.plds.data.model.local.DocumentType
import rezaei.mohammad.plds.data.model.response.DocumentStatusResponse
import rezaei.mohammad.plds.data.remote.RemoteRepository
import rezaei.mohammad.plds.util.Event

class GetDocReferenceViewModel(
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository
) : ViewModel() {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _documentStatusEvent = MutableLiveData<Event<ApiResult<DocumentStatusResponse>>>()
    val documentStatusEvent: LiveData<Event<ApiResult<DocumentStatusResponse>>> =
        _documentStatusEvent


    fun checkDocumentStatus() {
        _dataLoading.value = true
        viewModelScope.launch {
            val result = remoteRepository.retrieveDocumentStatus(
                localRepository.getAllDocument(DocumentType.CheckProgress).firstOrNull()?.docRefNo
            )
            _documentStatusEvent.value = Event(result)
            _dataLoading.value = false
        }
    }
}
