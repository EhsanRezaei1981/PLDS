package rezaei.mohammad.plds.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.Callback
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.model.response.ErrorHandling


fun View.snack(
    message: ErrorHandling?,
    actionText: String? = null,
    action: (() -> Unit)? = null,
    onDismissAction: (() -> Unit)? = null,
    duration: Int? = null
) {
    hideKeyboard()
    val snack = Snackbar.make(
        this, message?.errorMessage ?: "Unknown error",
        duration ?: if (actionText == null) Snackbar.LENGTH_LONG else Snackbar.LENGTH_INDEFINITE
    )

    if (message?.isSuccessful != null && message.isSuccessful == false)
        snack.setTextColor(ContextCompat.getColor(context, R.color.colorNo))

    var isActionInvoke = false
    var isDismissActionInvoke = false

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
                if (!isActionInvoke && !isDismissActionInvoke) {
                    onDismissAction.invoke()
                    isDismissActionInvoke = true
                }
            }
        })
    }

    this.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewDetachedFromWindow(p0: View?) {
            snack.dismiss()
        }

        override fun onViewAttachedToWindow(p0: View?) {
        }
    })

    snack.show()
}

fun Fragment.setActivityTitle(title: String?) {
    title?.let {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = title
    }
}

fun View.hideKeyboard() {
    val imm: InputMethodManager =
        context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun Context.dpToPx(dp: Float): Float = dp * resources.displayMetrics.density