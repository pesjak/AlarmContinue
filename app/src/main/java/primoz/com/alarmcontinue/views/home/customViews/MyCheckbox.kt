package primoz.com.alarmcontinue.views.home.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.item_checkbox.view.*
import primoz.com.alarmcontinue.R

class MyCheckBox @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    var text: String? = null
        set(value) {
            field = value
            checkBox.text = value ?: ""
        }

    var isChecked: Boolean = false
        set(value) {
            field = value
            checkBox.isChecked = value
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.item_checkbox, this, true)

        //Fill out from XML
        val ta = context.obtainStyledAttributes(attrs, R.styleable.MyCheckBox, defStyleAttr, 0)
        text = ta.getString(R.styleable.MyCheckBoxView_text)
        isChecked = ta.getBoolean(R.styleable.MyCheckBoxView_isChecked, false)
        ta.recycle()

        //OnClickListeners
        dummyClickableContainer.setOnClickListener { changeCheckedState() }
    }

    fun changeCheckedState() {
        isChecked = !isChecked
    }

}