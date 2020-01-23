package rezaei.mohammad.plds.util

import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.Callback

fun View.snack(
    message: String?,
    actionText: String? = null,
    action: (() -> Unit)? = null,
    onDismissAction: (() -> Unit)? = null,
    duration: Int? = null
) {
    val snack = Snackbar.make(
        this, message ?: "",
        duration ?: if (actionText == null) Snackbar.LENGTH_LONG else Snackbar.LENGTH_INDEFINITE
    )

    var isActionInvoke = false

    snack.setAction(actionText) {
        action?.let {
            it.invoke()
            isActionInvoke = true
        } ?: kotlin.run {
            snack.dismiss()
        }
    }
    onDismissAction?.let {
        snack.addCallback(object : Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                if (!isActionInvoke)
                    onDismissAction.invoke()
            }
        })
    }

    snack.show()
}