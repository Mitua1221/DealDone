package com.arjental.dealdone.userinterface

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.arjental.dealdone.R
import com.arjental.dealdone.Translator
import java.util.*

private const val ARG_DATE = "date"

class DatePickerFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val date = arguments?.getSerializable(ARG_DATE) as Date

        val calendar = Calendar.getInstance()

        calendar.time = date

        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)

        val dateListener = DatePickerDialog.OnDateSetListener {
                it: DatePicker, year: Int, month: Int, day: Int ->
            Translator.timeSelectedFromCalendar.value = GregorianCalendar(year, month, day).time
        }

        return DatePickerDialog(
            requireContext(),
            R.style.calendar_dialog_theme,
            dateListener,
            initialYear,
            initialMonth,
            initialDay
        )

    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        Translator.timeSelectedFromCalendar.value = null
    }

    companion object {
        fun newInstance(date: Date): DatePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_DATE, date)
            }
            return DatePickerFragment().apply {
                arguments = args
            }
        }
    }
}