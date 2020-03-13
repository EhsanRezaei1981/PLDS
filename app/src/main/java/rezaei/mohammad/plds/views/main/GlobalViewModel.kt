package rezaei.mohammad.plds.views.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import rezaei.mohammad.plds.data.local.LocalRepository
import rezaei.mohammad.plds.data.model.local.Document
import rezaei.mohammad.plds.data.model.local.DocumentType
import rezaei.mohammad.plds.data.preference.PreferenceManager

class GlobalViewModel(
    private val preferenceManager: PreferenceManager,
    private val localRepository: LocalRepository
) : ViewModel() {

    //live data to keep docRefNo and share it between fragments
    val docRefNo = MutableLiveData<String>()

    private val _documentsList = MutableLiveData<MutableList<Document>>()
    val documentsList: LiveData<MutableList<Document>> = _documentsList

    fun signOut() {
        preferenceManager.authToken = null
        preferenceManager.username = null
        preferenceManager.password = null
        viewModelScope.launch {
            localRepository.deleteUser(localRepository.getUser())
        }
    }

    fun getDocuments(documentType: DocumentType) {
        viewModelScope.launch {
            _documentsList.value = localRepository.getAllDocument(documentType).toMutableList()
        }
    }

}