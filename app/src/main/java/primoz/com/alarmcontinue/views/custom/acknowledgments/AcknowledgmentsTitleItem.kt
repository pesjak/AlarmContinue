package primoz.com.alarmcontinue.views.custom.acknowledgments

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.item_acknowledgments_title.view.*
import primoz.com.alarmcontinue.R

class AcknowledgmentsTitleItem(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    var text: String = ""
        set(value) {
            field = value
            tvTitle.text = value
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.item_acknowledgments_title, this, true)
    }

}