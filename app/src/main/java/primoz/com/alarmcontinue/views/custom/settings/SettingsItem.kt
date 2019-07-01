package primoz.com.alarmcontinue.views.custom.settings

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.item_settings.view.*
import primoz.com.alarmcontinue.R

class SettingsItem(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    var isLineVisible: Boolean = false
        set(value) {
            field = value
            line?.visibility = if (value) View.VISIBLE else View.GONE
        }
    var image: Int = R.mipmap.ic_launcher
        set(value) {
            field = value
            imageViewSettings.setImageDrawable(ContextCompat.getDrawable(context, value))
        }

    var text: String = ""
        set(value) {
            field = value
            textSettings.text = value
        }

    var descriptionText: String = ""
        set(value) {
            field = value
            descriptionTextSettings.text = value
            descriptionTextSettings.visibility = View.VISIBLE
        }

    var listener: OnClickListener? = null
        set(value) {
            field = value
            container.setOnClickListener(listener)
        }

    var switchListener: CompoundButton.OnCheckedChangeListener? = null
        set(value) {
            field = value
            switchSettings.visibility = if (value != null) View.VISIBLE else View.GONE
            value?.let {
                container.setOnClickListener {
                    switchSettings.isChecked = !switchSettings.isChecked
                    value.onCheckedChanged(switchSettings, switchSettings.isChecked)
                }
            }
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.item_settings, this, true)
    }

}