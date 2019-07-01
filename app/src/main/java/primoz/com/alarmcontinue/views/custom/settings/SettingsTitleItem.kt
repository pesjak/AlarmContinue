package primoz.com.alarmcontinue.views.custom.settings

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.item_settings_title.view.*
import primoz.com.alarmcontinue.R

class SettingsTitleItem(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    var text: String = ""
        set(value) {
            field = value
            textSettings.text = value
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.item_settings_title, this, true)
    }

}