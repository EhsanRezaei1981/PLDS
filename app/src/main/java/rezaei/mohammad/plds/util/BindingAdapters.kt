package rezaei.mohammad.plds.util

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import rezaei.mohammad.plds.data.model.local.Document
import rezaei.mohammad.plds.views.addMultiDoc.DocumentAdapter


@BindingAdapter("app:errorText")
fun TextInputLayout.setErrorMessage(errorMessage: Int) {
    if (errorMessage != 0)
        this.error = this.context.getString(errorMessage)
}

@BindingAdapter("app:items")
fun setItems(listView: RecyclerView, items: MutableList<Document>?) {
    (listView.adapter as DocumentAdapter).submitList(items)
}