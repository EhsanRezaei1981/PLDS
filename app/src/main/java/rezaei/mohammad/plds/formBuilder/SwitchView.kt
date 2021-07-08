package rezaei.mohammad.plds.formBuilder

import android.content.Context
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.view_switch.view.*
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.model.request.ElementResult
import rezaei.mohammad.plds.data.model.response.FormResponse

class SwitchView(
    context: Context,
    private val structure: FormResponse.DataItem,
    readOnly: Boolean = false
) : ConstraintLayout(context), FormView {

    var isReadOnly: Boolean = false
        set(value) {
            isSaveEnabled = true
            switch_view.isEnabled = !value
            field = value
        }

    override var valueIndex: Int = 0
        set(value) {
            field = value
            setStructure()
        }

    init {
        View.inflate(context, R.layout.view_switch, this)
        isReadOnly = readOnly
        setStructure()
    }

    private fun setStructure() {
        switch_label.text = structure.label
        switch_description.text = structure.date
        structure.value?.getOrNull(valueIndex)?.let {
            switch_view.isChecked = it.reply?.toBoolean() ?: false
        }
    }

    override fun validate(): Boolean = true

    override val elementId: Int = structure.statusQueryId ?: 0
    override val result: ElementResult
        get() = ElementResult.BooleanResult(switch_view.isChecked)
}
