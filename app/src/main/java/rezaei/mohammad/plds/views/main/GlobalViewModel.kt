package rezaei.mohammad.plds.views.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GlobalViewModel : ViewModel() {

    //live data to keep docRefNo and share it between fragments
    val docRefNo = MutableLiveData<String>()

}