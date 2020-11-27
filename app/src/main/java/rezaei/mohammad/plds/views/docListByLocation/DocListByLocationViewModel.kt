package rezaei.mohammad.plds.views.docListByLocation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import rezaei.mohammad.plds.data.ApiResult
import rezaei.mohammad.plds.data.LocalRepository
import rezaei.mohammad.plds.data.RemoteRepository
import rezaei.mohammad.plds.data.model.local.Document
import rezaei.mohammad.plds.data.model.local.DocumentType
import rezaei.mohammad.plds.data.model.request.GetDocumentsOnLocationRequest
import rezaei.mohammad.plds.data.model.response.DocumentOnLocationResponse
import rezaei.mohammad.plds.data.model.response.ErrorHandling
import rezaei.mohammad.plds.util.Event
import java.util.*

class DocListByLocationViewModel(
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository
) : ViewModel() {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _documentList = MutableLiveData<List<DocumentOnLocationResponse.Data>>()
    val documentList: LiveData<List<DocumentOnLocationResponse.Data>> = _documentList

    private val _documenttEvent = MutableLiveData<Event<ErrorHandling?>>()
    val documentEvent: LiveData<Event<ErrorHandling?>> = _documenttEvent


    fun getDocuments(getDocumentsOnLocationRequest: GetDocumentsOnLocationRequest) {
        viewModelScope.launch {
            _dataLoading.value = true
            when (val apiResult =
                remoteRepository.getDocumentListOnLocation(getDocumentsOnLocationRequest)) {
                is ApiResult.Success -> _documentList.value = apiResult.response.data ?: emptyList()
                is ApiResult.Error -> _documenttEvent.value = Event(apiResult.errorHandling)
            }
            _dataLoading.value = false
        }
    }

    fun addToReportIssue(docRefNo: String) {
        viewModelScope.launch {
            val isAdded = localRepository.insertDocument(
                Document(
                    docRefNo = docRefNo.toUpperCase(Locale.US),
                    documentType = DocumentType.ReportIssue
                )
            )
            if (isAdded)
                _documenttEvent.value = Event(
                    ErrorHandling(
                        true,
                        errorMessage = "Document Added To Report Issue List",
                        isSuccessful = true
                    )
                )
            else
                _documenttEvent.value = Event(
                    ErrorHandling(
                        true,
                        errorMessage = "This Document Added To Report Issue List Already",
                        isSuccessful = false
                    )
                )
        }
    }

    fun addToDocumentProgress(docRefNo: String) {
        viewModelScope.launch {
            val isAdded = localRepository.insertDocument(
                Document(
                    docRefNo = docRefNo.toUpperCase(Locale.US),
                    documentType = DocumentType.CheckProgress
                )
            )
            if (isAdded)
                _documenttEvent.value = Event(
                    ErrorHandling(
                        true,
                        errorMessage = "Document Added To Document Progress List",
                        isSuccessful = true
                    )
                )
            else
                _documenttEvent.value = Event(
                    ErrorHandling(
                        true,
                        errorMessage = "This Document Added To Document Progress List Already",
                        isSuccessful = false
                    )
                )
        }
    }
}