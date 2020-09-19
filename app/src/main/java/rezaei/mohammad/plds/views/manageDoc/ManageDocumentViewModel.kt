package rezaei.mohammad.plds.views.manageDoc

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.ApiResult
import rezaei.mohammad.plds.data.model.request.DocumentStatusRequest
import rezaei.mohammad.plds.data.model.response.DocumentBaseInfoResponse
import rezaei.mohammad.plds.data.remote.RemoteRepository
import rezaei.mohammad.plds.util.Event
import rezaei.mohammad.plds.util.hideKeyboard

class ManageDocumentViewModel(
    private val remoteRepository: RemoteRepository,
    val docRefNo: MutableLiveData<String>
) : ViewModel() {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _docRefNoErr = MutableLiveData<Int>()
    val docRefNoErr: MutableLiveData<Int> = _docRefNoErr

    private val _documentBaseInfo = MutableLiveData<Event<ApiResult<DocumentBaseInfoResponse>>>()
    val documentBaseInfo: LiveData<Event<ApiResult<DocumentBaseInfoResponse>>> = _documentBaseInfo

    init {
        setupAutoCheck()
    }

    fun getDocumentBaseInfo(view: View?, docRefNo: String?) {
        view?.hideKeyboard()
        this@ManageDocumentViewModel.docRefNo.postValue(docRefNo)
    }

    private fun getDocumentBaseInfo() {
        validateDocRefNo()
        if (_docRefNoErr.value == 0)
            viewModelScope.launch {
                _dataLoading.value = true
                _documentBaseInfo.value =
                    Event(remoteRepository.getDocumentBaseInfo(DocumentStatusRequest(docRefNo.value)))
                _dataLoading.value = false

            }
    }

    private fun validateDocRefNo() {
        val currentDocRefNo = docRefNo.value

        if (currentDocRefNo == null || currentDocRefNo.isEmpty())
            _docRefNoErr.value = R.string.doc_ref_no_validate_err
        else
            _docRefNoErr.value = 0
    }

    private fun setupAutoCheck() {
        docRefNo.observeForever {
            if (it != null) {
                getDocumentBaseInfo()
            }
        }
    }
}