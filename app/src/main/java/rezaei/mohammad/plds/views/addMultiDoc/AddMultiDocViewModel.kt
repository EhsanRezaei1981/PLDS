package rezaei.mohammad.plds.views.addMultiDoc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.local.LocalRepository
import rezaei.mohammad.plds.data.model.local.Document
import rezaei.mohammad.plds.data.model.local.DocumentType
import rezaei.mohammad.plds.util.Event
import java.util.*

class AddMultiDocViewModel(
    private val localRepository: LocalRepository,
    val docRefNo: MutableLiveData<String>,
    private val docType: DocumentType
) : ViewModel() {

    private val _documentsList = MutableLiveData<MutableList<Document>>()
    val documentsList: LiveData<MutableList<Document>> = _documentsList

    private val _removeDocumentsList = MutableLiveData<MutableList<Document>>()
    val removeDocumentsList: LiveData<MutableList<Document>> = _removeDocumentsList

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
        _removeDocumentsList.value = mutableListOf()
        autoCheckAfterCodeDetect.value = true
        setupAutoCheck()
    }

    fun loadDocumentList() {
        viewModelScope.launch {
            _documentsList.value = localRepository.getAllDocument(docType).toMutableList()
        }
    }

    fun addToList(docRefNo: String?) {
        validateDocRefNo(docRefNo)
        if (_docRefNoErr.value == 0)
            viewModelScope.launch {
                val status = localRepository.insertDocument(
                    Document(
                        docRefNo = docRefNo!!.toUpperCase(
                            Locale.US
                        ), documentType = docType
                    )
                )
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
        _removeDocumentsList.value?.add(document)
        val newList = _documentsList.value
            ?.dropWhile { _removeDocumentsList.value!!.contains(it) }?.toMutableList()
        _documentsList.value = newList
        _documentsList.value?.removeAll(_removeDocumentsList.value!!)
        _documentRemoveEvent.value = Event(document)
    }

    fun clearList() {
        GlobalScope.launch {
            localRepository.deleteAllDocs(localRepository.getAllDocument(docType))
            loadDocumentList()
        }
    }

    fun removeItem(document: Document) {
        GlobalScope.launch {
            localRepository.deleteDocument(document)
            _removeDocumentsList.value!!.remove(document)
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
            if ((autoCheckAfterCodeDetect.value == true) && it != null) {
                addToList(it)
            }
        }
    }
}
