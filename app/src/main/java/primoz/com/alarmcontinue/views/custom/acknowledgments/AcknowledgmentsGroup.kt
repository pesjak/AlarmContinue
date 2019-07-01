package primoz.com.alarmcontinue.views.custom.acknowledgments

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.dummy_linear_layout.view.*
import primoz.com.alarmcontinue.R

class AcknowledgmentsGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyle, defStyleRes) {

    private var items = mutableListOf<Any>()

    init {
        LayoutInflater.from(context).inflate(R.layout.dummy_linear_layout, this, true)
    }

    fun addAcknowledgment(
        text: String,
        subText: String? = null,
        description: String? = null
    ) {
        val acknowledgmentsItem = AcknowledgmentsItem(context)
        acknowledgmentsItem.text = text
        subText?.let { acknowledgmentsItem.subText = it }
        description?.let { acknowledgmentsItem.descriptionText = it }

        items.add(acknowledgmentsItem)
        layoutList.addView(acknowledgmentsItem)
    }

    fun addTitle(text: String) {
        val settingsItem = AcknowledgmentsTitleItem(context)
        settingsItem.text = text

        items.add(settingsItem)
        layoutList.addView(settingsItem)
    }

}