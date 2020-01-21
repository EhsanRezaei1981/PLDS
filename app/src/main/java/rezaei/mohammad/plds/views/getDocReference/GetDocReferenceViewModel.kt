package rezaei.mohammad.plds.views.getDocReference

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.Result
import rezaei.mohammad.plds.data.model.response.DocumentStatusResponse
import rezaei.mohammad.plds.data.remote.RemoteRepository
import rezaei.mohammad.plds.util.Event

class GetDocReferenceViewModel(
    private val remoteRepository: RemoteRepository,
    val docRefNo: MutableLiveData<String>
) : ViewModel() {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _docRefNoErr = MutableLiveData<Int>()
    val docRefNoErr: MutableLiveData<Int> = _docRefNoErr

    val autoCheckAfterCodeDetect = MutableLiveData<Boolean>()

    private val _documentStatusEvent = MutableLiveData<Event<Result<DocumentStatusResponse>>>()
    val documentStatusEvent: MutableLiveData<Event<Result<DocumentStatusResponse>>> =
        _documentStatusEvent

    init {
        _dataLoading.value = false
        setupAutoCheck()
    }

    fun checkDocumentStatus() {
        validateDocRefNo()
        if (_docRefNoErr.value == 0) {
            _dataLoading.value = true
            viewModelScope.launch {
                val result = remoteRepository.retrieveDocumentStatus(docRefNo.value)
                _documentStatusEvent.value = Event(result)
                _dataLoading.value = false
            }
        }
    }

    private fun setupAutoCheck() {
        docRefNo.observeForever {
            if ((autoCheckAfterCodeDetect.value == true) && it != null)
                checkDocumentStatus()
        }
    }

    private fun validateDocRefNo() {
        val currentDocRefNo = docRefNo.value

        if (currentDocRefNo == null || currentDocRefNo.isEmpty())
            _docRefNoErr.value = R.string.doc_ref_no_validate_err
        else
            _docRefNoErr.value = 0
    }
}
