package rezaei.mohammad.plds.util

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout


@BindingAdapter("app:errorText")
fun TextInputLayout.setErrorMessage(errorMessage: Int) {
    if (errorMessage != 0)
        this.error = this.context.getString(errorMessage)
}