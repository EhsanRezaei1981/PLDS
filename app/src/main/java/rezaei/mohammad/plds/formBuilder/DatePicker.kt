package rezaei.mohammad.plds.formBuilder

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import kotlinx.android.synthetic.main.string_view.view.*
import rezaei.mohammad.plds.data.model.response.FormResponse

import java.util.*

class DatePicker(context: Context?, structure: FormResponse.DataItem) :
    TextInputView(context, structure), DatePickerDialog.OnDateSetListener {

    init {
        initDatePicker()
        disableEditable()
    }

    private fun initDatePicker() {
        val date = Calendar.getInstance()
        inputText.editText?.setOnClickListener {
            DatePickerDialog(
                context, this,
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun disableEditable() {
        inputText.editText?.isFocusable = false
        inputText.editText?.isClickable = true
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        inputText.editText?.setText("$year/${month.plus(1)}/$day")
    }
}