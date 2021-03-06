package rezaei.mohammad.plds.views.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.ApiResult
import rezaei.mohammad.plds.data.local.LocalRepository
import rezaei.mohammad.plds.data.model.response.LoginResponse
import rezaei.mohammad.plds.data.preference.PreferenceManager
import rezaei.mohammad.plds.data.remote.RemoteRepository
import rezaei.mohammad.plds.util.Event

class LoginViewModel(
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    private val _isLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _isLoading

    private val _usernameErr = MutableLiveData<Int>()
    val usernameErr: LiveData<Int> = _usernameErr
    private val _passwordErr = MutableLiveData<Int>()
    val passwordErr: LiveData<Int> = _passwordErr

    private val _loginResultEvent = MutableLiveData<Event<ApiResult<LoginResponse>>>()
    val loginResultEvent: LiveData<Event<ApiResult<LoginResponse>>> = _loginResultEvent

    init {
        //login automatically after first one
        if (preferenceManager.authToken != null)
            _loginResultEvent.value = Event(ApiResult.Success(LoginResponse()))
    }


    fun login() {
        validateInputs()
        if (usernameErr.value == 0 && passwordErr.value == 0) {
            _isLoading.value = true
            viewModelScope.launch {
                val result = remoteRepository.login(username.value!!, password.value!!)
                _loginResultEvent.value = Event(result)
                (result as? ApiResult.Success)?.let { saveUser(it.response) }
                _isLoading.value = false
            }
        }
    }

    private fun validateInputs() {
        val currentUserName = username.value
        val currentPassword = password.value

        if (currentUserName == null || currentUserName.trim().isEmpty())
            _usernameErr.value = R.string.validate_username_err
        else
            _usernameErr.value = 0
        if (currentPassword == null || currentPassword.trim().isEmpty())
            _passwordErr.value = R.string.validate_pass_err
        else
            _passwordErr.value = 0
    }

    private fun saveUser(loginResponse: LoginResponse) {
        preferenceManager.username = username.value
        preferenceManager.password = password.value
        preferenceManager.authToken = loginResponse.data?.jAToken

        viewModelScope.launch {
            localRepository.saveUser(loginResponse.data!!)
        }
    }
}
