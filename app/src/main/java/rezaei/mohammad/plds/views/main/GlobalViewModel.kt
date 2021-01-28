package rezaei.mohammad.plds.views.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import rezaei.mohammad.plds.data.ApiResult
import rezaei.mohammad.plds.data.local.LocalRepository
import rezaei.mohammad.plds.data.model.local.CheckInResponseEntity
import rezaei.mohammad.plds.data.model.request.Gps
import rezaei.mohammad.plds.data.model.request.ResetCheckInRequest
import rezaei.mohammad.plds.data.model.response.BaseResponse
import rezaei.mohammad.plds.data.preference.PreferenceManager
import rezaei.mohammad.plds.data.remote.RemoteRepository

class GlobalViewModel(
    private val preferenceManager: PreferenceManager,
    private val localRepository: LocalRepository,
    private val remoteRepository: RemoteRepository
) : ViewModel() {

    //live data to keep docRefNo and share it between fragments
    val docRefNo: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    private val _checkInResponseEntity = MutableLiveData<CheckInResponseEntity?>()
    val checkInResponseEntity: LiveData<CheckInResponseEntity?> = _checkInResponseEntity

    private val _resetCheckInResult = MutableLiveData<ApiResult<BaseResponse<Unit>>>()
    val resetCheckInResult: LiveData<ApiResult<BaseResponse<Unit>>> = _resetCheckInResult

    fun signOut() {
        preferenceManager.authToken = null
        preferenceManager.username = null
        preferenceManager.password = null
        viewModelScope.launch {
            localRepository.deleteUser(localRepository.getUser())
        }
    }

    fun findIfAnyCheckInExist() {
        viewModelScope.launch {
            _checkInResponseEntity.value = localRepository.getCheckInResponse()
        }
    }

    fun resetCheckIn(gps: Gps) {
        viewModelScope.launch {
            val result = remoteRepository.resetCheckInOutOperation(ResetCheckInRequest(gps))
            _resetCheckInResult.value = result
        }
    }

}