package primoz.com.alarmcontinue.views.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.view_item_select_day.view.*
import primoz.com.alarmcontinue.R
import primoz.com.alarmcontinue.enums.EnumDayOfWeek

class SelectDayItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var selectedBackground = R.drawable.circle
    private val unselectedBackground = android.R.color.transparent

    private var dayText: EnumDayOfWeek = EnumDayOfWeek.MONDAY
        set(value) {
            field = value
            tvDayText.text = setFirstLetterOf(value)
        }

    private var isChecked: Boolean = false
        set(value) {
            field = value
            val background = if (value) selectedBackground else unselectedBackground
            val textAppearance = if (value) R.style.TextAppearance_Bold else R.style.TextAppearance_Regular
            tvDayText.background = ContextCompat.getDrawable(context, background)
            tvDayText.setTextAppearance(textAppearance)
            tvDayText.textSize = 12F
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_item_select_day, this, true)
    }

    fun setData(day: EnumDayOfWeek, shouldCheck: Boolean = false, listener: ((EnumDayOfWeek, Boolean) -> Unit)) {
        dayText = day
        isChecked = shouldCheck

        containerText.setOnClickListener {
            changeCheckedState()
            listener.invoke(dayText, isChecked)
        }

    }

    fun changeCheckedState() {
        isChecked = !isChecked
    }

    /*
    Private
     */

    private fun setFirstLetterOf(enumDayOfWeek: EnumDayOfWeek): String {
        return when (enumDayOfWeek) {
            EnumDayOfWeek.MONDAY -> "M"
            EnumDayOfWeek.TUESDAY -> "T"
            EnumDayOfWeek.WEDNESDAY -> "W"
            EnumDayOfWeek.THURSDAY -> "T"
            EnumDayOfWeek.FRIDAY -> "F"
            EnumDayOfWeek.SATURDAY -> "S"
            EnumDayOfWeek.SUNDAY -> "S"
        }
    }
}