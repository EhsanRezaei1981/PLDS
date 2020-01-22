package rezaei.mohammad.plds.util

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.snack(message: String?, actionText: String? = null, action: (() -> Unit)? = null) {
    val snack = Snackbar.make(
        this, message ?: "",
        if (actionText == null) Snackbar.LENGTH_LONG else Snackbar.LENGTH_INDEFINITE
    )
    snack.setAction(actionText) {
        action?.let {
            it.invoke()
        } ?: kotlin.run {
            snack.dismiss()
        }
    }
    snack.show()
}