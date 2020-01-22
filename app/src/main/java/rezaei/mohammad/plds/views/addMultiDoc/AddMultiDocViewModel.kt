package rezaei.mohammad.plds.views.addMultiDoc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.local.LocalRepository
import rezaei.mohammad.plds.data.model.local.Document

class AddMultiDocViewModel(
    private val localRepository: LocalRepository,
    val docRefNo: MutableLiveData<String>
) : ViewModel() {

    private val _documentsList = MutableLiveData<MutableList<Document>>()
    val documentsList: LiveData<MutableList<Document>> = _documentsList

    private val _docRefNoErr = MutableLiveData<Int>()
    val docRefNoErr: MutableLiveData<Int> = _docRefNoErr

    val autoCheckAfterCodeDetect = MutableLiveData<Boolean>()

    init {
        setupAutoCheck()
    }

    fun addToList() {
        if (_docRefNoErr.value == 0)
            _documentsList.value?.add(Document(docRefNo.value!!))
    }

    fun clearList() {
        _documentsList.value = mutableListOf()
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
            if ((autoCheckAfterCodeDetect.value == true) && it != null)
                addToList()
        }
    }
}
