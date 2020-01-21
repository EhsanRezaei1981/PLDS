package rezaei.mohammad.plds.views.docProgress

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import rezaei.mohammad.plds.data.model.response.DocumentStatusResponse
import rezaei.mohammad.plds.data.remote.RemoteRepository

class DocProgressViewModel(
    private val remoteRepository: RemoteRepository
) : ViewModel() {

    private val _documentStatus = MutableLiveData<DocumentStatusResponse.Data>()
    val documentStatus: LiveData<DocumentStatusResponse.Data> = _documentStatus

    fun start(documentStatus: DocumentStatusResponse.Data) {
        _documentStatus.value = documentStatus
    }
}
