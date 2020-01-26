package rezaei.mohammad.plds.views.addMultiDoc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.local.LocalRepository
import rezaei.mohammad.plds.data.model.local.Document
import rezaei.mohammad.plds.util.Event

class AddMultiDocViewModel(
    private val localRepository: LocalRepository,
    val docRefNo: MutableLiveData<String>
) : ViewModel() {

    private val _documentsList = MutableLiveData<MutableList<Document>>()
    val documentsList: LiveData<MutableList<Document>> = _documentsList

    private val _docRefNoErr = MutableLiveData<Int>()
    val docRefNoErr: MutableLiveData<Int> = _docRefNoErr

    private val _documentRemoveEvent = MutableLiveData<Event<Document>>()
    val documentRemoveEvent: LiveData<Event<Document>> = _documentRemoveEvent

    private val _allDocumentsRemoveEvent = MutableLiveData<Event<Unit>>()
    val allDocumentsRemoveEvent: LiveData<Event<Unit>> = _allDocumentsRemoveEvent

    private val _duplicateDocumentEvent = MutableLiveData<Event<Unit>>()
    val duplicateDocumentEvent: LiveData<Event<Unit>> = _duplicateDocumentEvent

    val autoCheckAfterCodeDetect = MutableLiveData<Boolean>()

    init {
        setupAutoCheck()
        loadDocumentList()
    }

    fun loadDocumentList() {
        viewModelScope.launch {
            _documentsList.value = localRepository.getAllDocument().toMutableList()
        }
    }

    fun addToList(docRefNo: String?) {
        validateDocRefNo(docRefNo)
        if (_docRefNoErr.value == 0)
            viewModelScope.launch {
                val status = localRepository.insertDocument(Document(docRefNo!!))
                if (status)
                    loadDocumentList()
                else
                    _duplicateDocumentEvent.value = Event(Unit)
                this@AddMultiDocViewModel.docRefNo.value = null
            }
    }

    fun fakeClearList() {
        _documentsList.value = mutableListOf()
        _allDocumentsRemoveEvent.value = Event(Unit)
    }

    fun fakeRemoveItem(document: Document) {
        // TODO: BUG: recycle view doesn't update with one time remove item
        val newList = _documentsList.value
            ?.dropWhile { it == document }?.toMutableList()
        _documentsList.value = newList
        _documentsList.value?.remove(document)
        _documentRemoveEvent.value = Event(document)
    }

    fun clearList() {
        viewModelScope.launch {
            localRepository.deleteAllDocs(localRepository.getAllDocument())
            loadDocumentList()
        }
    }

    fun removeItem(document: Document) {
        viewModelScope.launch {
            localRepository.deleteDocument(document)
            loadDocumentList()
        }
    }


    private fun validateDocRefNo(docRefNo: String?) {
        val currentDocRefNo = docRefNo

        if (currentDocRefNo == null || currentDocRefNo.isEmpty())
            _docRefNoErr.value = R.string.doc_ref_no_validate_err
        else
            _docRefNoErr.value = 0
    }

    private fun setupAutoCheck() {
        docRefNo.observeForever {
            if ((autoCheckAfterCodeDetect.value == true) && it != null)
                addToList(it)
        }
    }
}
