package rezaei.mohammad.plds.views.manageDoc.imageViewer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import rezaei.mohammad.plds.data.ApiResult
import rezaei.mohammad.plds.data.RemoteRepository
import rezaei.mohammad.plds.data.model.request.GetFileRequest
import rezaei.mohammad.plds.data.model.response.GetFileResponse

class ImageViewerViewModel(
    private val remoteRepository: RemoteRepository
) : ViewModel() {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _loadImageResult = MutableLiveData<ApiResult<GetFileResponse>>()
    val loadImageResult: LiveData<ApiResult<GetFileResponse>> = _loadImageResult

    fun loadImage(getFileRequest: GetFileRequest) {
        viewModelScope.launch {
            _dataLoading.value = true
            val result = remoteRepository.getFileByMainLegalInfo(getFileRequest)
            _loadImageResult.value = result
            _dataLoading.value = false
        }
    }

}