package rezaei.mohammad.plds.views.loginInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import rezaei.mohammad.plds.data.local.LocalRepository
import rezaei.mohammad.plds.data.model.response.LoginResponse

class LoginInfoViewModel(
    private val localRepository: LocalRepository
) : ViewModel() {

    private val _user = MutableLiveData<LoginResponse.User>()
    val user: LiveData<LoginResponse.User> = _user

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            _user.value = localRepository.getUser()
        }
    }
}
