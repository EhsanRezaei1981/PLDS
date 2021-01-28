package rezaei.mohammad.plds.views.manageDoc.docStatusHistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import rezaei.mohammad.plds.data.ApiResult
import rezaei.mohammad.plds.data.model.response.DocumentBaseInfoResponse
import rezaei.mohammad.plds.data.model.response.DocumentStatusHistoryResponse
import rezaei.mohammad.plds.data.remote.RemoteRepository
import rezaei.mohammad.plds.util.Event

class DocumentStatusHistoryViewModel(
    private val remoteRepository: RemoteRepository
) : ViewModel() {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _documentsStatusHistoryList =
        MutableLiveData<List<DocumentStatusHistoryResponse.Data>>()
    val documentsStatusHistoryList: LiveData<List<DocumentStatusHistoryResponse.Data>> =
        _documentsStatusHistoryList

    private val _getDocumentsStatusHistoryError = MutableLiveData<Event<ApiResult.Error>>()
    val getDocumentsStatusHistoryError: LiveData<Event<ApiResult.Error>> =
        _getDocumentsStatusHistoryError

    private val _modifyClickEvent = MutableLiveData<Event<DocumentStatusHistoryResponse.Data>>()
    val modifyClickEvent: LiveData<Event<DocumentStatusHistoryResponse.Data>> = _modifyClickEvent

    private val _viewClickEvent = MutableLiveData<Event<DocumentStatusHistoryResponse.Data>>()
    val viewClickEvent: LiveData<Event<DocumentStatusHistoryResponse.Data>> = _viewClickEvent

    fun getDocumentStatusHistory(documentBaseInfo: DocumentBaseInfoResponse.Data) {
        viewModelScope.launch {
            _dataLoading.value = true
            when (val result = remoteRepository.getDocumentStatusHistory(documentBaseInfo)) {
                is ApiResult.Success -> {
                    _documentsStatusHistoryList.value = result.response.data
                }
                is ApiResult.Error -> {
                    _getDocumentsStatusHistoryError.value = Event(result)
                }
            }
            _dataLoading.value = false
        }
    }

    fun viewDocument(document: DocumentStatusHistoryResponse.Data) {
        _viewClickEvent.value = Event(document)
    }

    fun modifyDocument(document: DocumentStatusHistoryResponse.Data) {
        _modifyClickEvent.value = Event(document)
    }
}