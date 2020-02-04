package rezaei.mohammad.plds.formBuilder

import android.annotation.SuppressLint
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

    @SuppressLint("SetTextI18n")
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
        inputText.editText?.setText(
            "${date.get(Calendar.YEAR)}/" +
                    "${date.get(Calendar.MONTH).plus(1).to2Digit()}/" +
                    date.get(Calendar.DAY_OF_MONTH).to2Digit()
        )
    }

    private fun disableEditable() {
        inputText.editText?.isFocusable = false
        inputText.editText?.isClickable = true
    }

    @SuppressLint("SetTextI18n")
    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        inputText.editText?.setText("$year/${month.plus(1).to2Digit()}/${day.to2Digit()}")
    }
}

fun Int.to2Digit(): String {
    return if (this >= 10) this.toString()
    else "0$this"
}