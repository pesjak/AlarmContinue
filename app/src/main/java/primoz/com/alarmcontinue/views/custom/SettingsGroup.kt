package primoz.com.alarmcontinue.views.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.CompoundButton
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.dummy_linear_layout.view.*
import kotlinx.android.synthetic.main.item_settings.view.*
import primoz.com.alarmcontinue.R

class SettingsGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyle, defStyleRes) {

    private var settingItems = mutableListOf<Any>()

    init {
        LayoutInflater.from(context).inflate(R.layout.dummy_linear_layout, this, true)
    }

    fun addSettings(
        drawable: Int,
        text: String,
        isLast: Boolean = false,
        subText: String? = null,
        switchClickedListener: ((SettingsItem, Boolean) -> Unit)
    ) {
        val settingsItem = SettingsItem(context)
        settingsItem.image = drawable
        settingsItem.text = text
        subText?.let { settingsItem.descriptionText = subText }
        settingsItem.switchListener = CompoundButton.OnCheckedChangeListener { switch, isChecked ->
            switchClickedListener.invoke(settingsItem, isChecked)
        }
        settingsItem.isLineVisible = isLast

        settingItems.add(settingsItem)
        layoutList.addView(settingsItem)
    }

    fun addSettings(
        drawable: Int,
        text: String,
        isLast: Boolean = false,
        descriptionText: String? = null,
        settingsClickedListener: ((SettingsItem) -> Unit)
    ) {
        val settingsItem = SettingsItem(context)
        settingsItem.image = drawable
        settingsItem.text = text
        descriptionText?.let { settingsItem.descriptionText = descriptionText }
        settingsItem.listener = OnClickListener {
            settingsClickedListener.invoke(settingsItem)
        }
        settingsItem.isLineVisible = isLast

        settingItems.add(settingsItem)
        layoutList.addView(settingsItem)
    }

    fun addTitle(text: String) {
        val settingsItem = SettingsTitleItem(context)
        settingsItem.text = text

        settingItems.add(settingsItem)
        layoutList.addView(settingsItem)
    }

}