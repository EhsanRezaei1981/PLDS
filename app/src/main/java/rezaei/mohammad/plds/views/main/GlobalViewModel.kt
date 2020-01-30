package rezaei.mohammad.plds.views.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import rezaei.mohammad.plds.data.local.LocalRepository
import rezaei.mohammad.plds.data.preference.PreferenceManager

class GlobalViewModel(
    private val preferenceManager: PreferenceManager,
    private val localRepository: LocalRepository
) : ViewModel() {

    //live data to keep docRefNo and share it between fragments
    val docRefNo = MutableLiveData<String>()

    fun signOut() {
        preferenceManager.authToken = null
        preferenceManager.username = null
        preferenceManager.password = null
        viewModelScope.launch {
            localRepository.deleteUser(localRepository.getUser())
        }
    }

}