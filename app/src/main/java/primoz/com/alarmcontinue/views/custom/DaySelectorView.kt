package primoz.com.alarmcontinue.views.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.view_select_day.view.*
import primoz.com.alarmcontinue.R
import primoz.com.alarmcontinue.enums.EnumDayOfWeek

class DaySelectorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyle, defStyleRes) {

    var selectedDays: MutableList<EnumDayOfWeek> = mutableListOf()
        set(value) {
            field = value
            setDay(day1, EnumDayOfWeek.MONDAY, selectedDays.contains(EnumDayOfWeek.MONDAY))
            setDay(day2, EnumDayOfWeek.TUESDAY, selectedDays.contains(EnumDayOfWeek.TUESDAY))
            setDay(day3, EnumDayOfWeek.WEDNESDAY, selectedDays.contains(EnumDayOfWeek.WEDNESDAY))
            setDay(day4, EnumDayOfWeek.THURSDAY, selectedDays.contains(EnumDayOfWeek.THURSDAY))
            setDay(day5, EnumDayOfWeek.FRIDAY, selectedDays.contains(EnumDayOfWeek.FRIDAY))
            setDay(day6, EnumDayOfWeek.SATURDAY, selectedDays.contains(EnumDayOfWeek.SATURDAY))
            setDay(day7, EnumDayOfWeek.SUNDAY, selectedDays.contains(EnumDayOfWeek.SUNDAY))
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_select_day, this, true)
        selectedDays = mutableListOf()
    }

    /*
    Private
     */

    private fun setDay(view: SelectDayItemView, text: EnumDayOfWeek, shouldCheck: Boolean = false) {
        view.setData(text, shouldCheck) { dayOfTheWeek, isChecked ->
            if (isChecked) selectedDays.add(dayOfTheWeek)
            else selectedDays.remove(dayOfTheWeek)
            updateStatus()
        }
    }

    private fun updateStatus() {
        if (selectedDays.size == 7 || selectedDays.size == 0) {
            tvCurrentDaysOverall.text = context.getText(R.string.every_day)
        } else if (selectedDays.size == 2
            && selectedDays.contains(EnumDayOfWeek.SATURDAY)
            && selectedDays.contains(EnumDayOfWeek.SUNDAY)
        ) {
            tvCurrentDaysOverall.text = context.getText(R.string.weekend)
        } else {
            tvCurrentDaysOverall.text = context.getText(R.string.repeat)
        }
    }

}