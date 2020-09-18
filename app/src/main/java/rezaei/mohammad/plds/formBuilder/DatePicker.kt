package rezaei.mohammad.plds.formBuilder

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import kotlinx.android.synthetic.main.string_view.view.*
import rezaei.mohammad.plds.data.model.response.FormResponse

import java.util.*

class DatePicker(
    context: Context?,
    structure: FormResponse.DataItem,
    readOnly: Boolean = false
) :
    TextInputView(context, structure), DatePickerDialog.OnDateSetListener {

    init {
        isReadOnly = readOnly
        initDatePicker(structure)
        disableEditable()
    }

    @SuppressLint("SetTextI18n")
    private fun initDatePicker(structure: FormResponse.DataItem) {
        val year: Int
        val month: Int
        val dayOfMonth: Int

        val selectedDate = structure.value?.reply?.split("/", "-")
        val date = Calendar.getInstance()
        year = selectedDate?.get(0)?.toInt() ?: date.get(Calendar.YEAR)
        month = selectedDate?.get(1)?.toInt() ?: date.get(Calendar.MONTH)
        dayOfMonth = selectedDate?.get(1)?.toInt() ?: date.get(Calendar.DAY_OF_MONTH)


        inputText.editText?.setOnClickListener {
            DatePickerDialog(
                context, this,
                year,
                month,
                dayOfMonth
            ).show()
        }

        inputText.editText?.setText(
            "${year}/" + "${month.plus(1).to2Digit()}/" + dayOfMonth.to2Digit()
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