package primoz.com.alarmcontinue.views.custom.acknowledgments

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.item_acknowledgments.view.*
import primoz.com.alarmcontinue.R

class AcknowledgmentsItem(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    var text: String = ""
        set(value) {
            field = value
            tvNameText.text = value
        }

    var subText: String = ""
        set(value) {
            field = value
            tvSubText.text = value
            tvSubText.visibility = View.VISIBLE
        }

    var descriptionText: String = ""
        set(value) {
            field = value
            tvDescriptionText.text = value
            tvDescriptionText.visibility = View.VISIBLE
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.item_acknowledgments, this, true)
    }

}