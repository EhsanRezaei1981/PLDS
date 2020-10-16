package rezaei.mohammad.plds.util

import androidx.navigation.NavController
import androidx.navigation.NavDirections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun NavController.tryNavigate(direction: NavDirections) = try {
    navigate(direction)
} catch (e: Exception) {
    e.printStackTrace()
    GlobalScope.launch(Dispatchers.Main) {
        delay(1000)
        navigate(direction)
    }
}