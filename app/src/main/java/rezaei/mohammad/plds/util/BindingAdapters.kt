package rezaei.mohammad.plds.util

import android.graphics.Bitmap
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import rezaei.mohammad.plds.R


@BindingAdapter("app:errorText")
fun TextInputLayout.setErrorMessage(errorMessage: Int) {
    if (errorMessage != 0)
        this.error = this.context.getString(errorMessage)
}

@BindingAdapter("app:items")
fun <T> RecyclerView.setItems(data: T?) {
    if (data == null) return
    if (adapter is BindableAdapter<*>)
        (adapter as? BindableAdapter<T>)?.setData(data)

}

@BindingAdapter("app:setBitmap")
fun ImageView.setBitmap(bitmap: Bitmap?) {
    bitmap?.let {
        this.setImageBitmap(bitmap)
    }
}

@BindingAdapter("app:endIconClick")
fun TextInputLayout.onEndIconClick(onClick: onClick) {
    this.setEndIconOnClickListener { onClick.onClick(this) }
}

@BindingAdapter("entries", "itemLayout", requireAll = false)
fun AutoCompleteTextView.bindAdapter(entries: List<Any?>?, @LayoutRes itemLayout: Int?) {
    if (entries == null) return
    val adapter = when (itemLayout) {
        null -> {
            ArrayAdapter(context, R.layout.item_spinner, entries)
        }
        else -> {
            ArrayAdapter(context, itemLayout, entries)
        }
    }
    setAdapter(adapter)
}

@BindingAdapter("onlySelectable")
fun AutoCompleteTextView.bindAdapter(onlySelectable: Boolean) {
    onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->

    }
}


interface onClick {
    fun onClick(view: View)
}

interface BindableAdapter<T> {
    fun setData(data: T)
}