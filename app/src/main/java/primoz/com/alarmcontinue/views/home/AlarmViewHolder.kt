package primoz.com.alarmcontinue.views.home

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.item_alarm.view.*
import primoz.com.alarmcontinue.model.Alarm

class AlarmViewHolder(itemView: View, private val onAlarmListener: OnAlarmListener) :
    RecyclerView.ViewHolder(itemView) {

    private lateinit var alarm: Alarm

    init {
        itemView.setOnClickListener { onAlarmListener.onAlarmClicked(alarm) }
        itemView.switchAlarm.setOnCheckedChangeListener { buttonView, isChecked ->
            onAlarmListener.onAlarmEnable(alarm, isChecked)
        }
    }

    fun setData(alarm: Alarm) {
        this.alarm = alarm
        itemView.tvNextAlarmText.text = "00:00"
        itemView.textNextDay.text = "Test"
    }

}