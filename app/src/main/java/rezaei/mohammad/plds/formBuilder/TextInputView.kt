package rezaei.mohammad.plds.formBuilder

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.string_view.view.*
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.model.request.ElementResult
import rezaei.mohammad.plds.data.model.response.FormResponse

open class TextInputView(
    context: Context?,
    private val structure: FormResponse.DataItem,
    readOnly: Boolean = false
) :
    LinearLayout(context),
    FormView {

    var isReadOnly: Boolean = false
        set(value) {
            inputText.editText?.isEnabled = !value
            inputText.editText?.isFocusable = !value
            inputText.isFocusableInTouchMode = !value
            field = value
        }

    init {
        View.inflate(context, R.layout.string_view, this)
        isReadOnly = readOnly
        setStructure()
    }

    private fun setStructure() {
        inputText.hint = structure.label
        structure.value?.reply?.let {
            inputText.editText?.setText(it)
        }
    }

    override fun validate(): Boolean {
        return if (structure.isMandatory == 0) {
            true
        } else {
            if (inputText.editText?.text?.isEmpty() == true) {
                inputText.error = context.getString(R.string.field_mandatory)
                false
            } else {
                inputText.error = null
                true
            }
        }
    }

    override val elementId: Int = structure.statusQueryId ?: 0

    override val result: ElementResult?
        get() = ElementResult.StringResult(
            structure.statusQueryId,
            inputText.editText?.text.toString(),
            structure.value?.vTMTId,
            structure.value?.mTId
        )
}