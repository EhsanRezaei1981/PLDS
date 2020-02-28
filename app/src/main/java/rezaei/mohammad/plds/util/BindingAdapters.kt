package rezaei.mohammad.plds.util

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.model.local.Document
import rezaei.mohammad.plds.views.addMultiDoc.DocumentAdapter


@BindingAdapter("app:errorText")
fun TextInputLayout.setErrorMessage(errorMessage: Int) {
    if (errorMessage != 0)
        this.error = this.context.getString(errorMessage)
}

@BindingAdapter("app:items")
fun RecyclerView.setItems(items: MutableList<Document>?) {
    (adapter as DocumentAdapter).submitList(items)
}

@BindingAdapter("app:setBitmap")
fun ImageView.setBitmap(bitmap: Bitmap?) {
    bitmap?.let {
        this.setImageBitmap(bitmap)
    } ?: kotlin.run {
        this.setImageDrawable(ContextCompat.getDrawable(this.context, R.drawable.user))
    }
}

@BindingAdapter("app:endIconClick")
fun TextInputLayout.onEndIconClick(onClick: onClick) {
    this.setEndIconOnClickListener { onClick.onClick(this) }
}

interface onClick {
    fun onClick(view: View)
}