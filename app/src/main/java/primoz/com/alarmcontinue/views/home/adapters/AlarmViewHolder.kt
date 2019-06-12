package primoz.com.alarmcontinue.views.home.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_alarm.view.*
import primoz.com.alarmcontinue.R
import primoz.com.alarmcontinue.enums.EnumDayOfWeek
import primoz.com.alarmcontinue.extensions.getFirst3Letters
import primoz.com.alarmcontinue.model.Alarm
import primoz.com.alarmcontinue.model.RealmDayOfWeek
import primoz.com.alarmcontinue.views.home.listeners.OnAlarmListener

class AlarmViewHolder(itemView: View, private val onAlarmListener: OnAlarmListener) :
    RecyclerView.ViewHolder(itemView) {

    private lateinit var alarm: Alarm
    private var isChecked = false

    init {
        itemView.setOnClickListener { onAlarmListener.onAlarmClicked(alarm) }
        itemView.switchAlarm.setOnClickListener { buttonView ->
            isChecked = !isChecked
            syncWithIsChecked()
            onAlarmListener.onAlarmEnable(alarm, isChecked)
        }
    }

    fun setData(alarm: Alarm) {
        this.alarm = alarm
        itemView.textAlarm.text =
            "${alarm.hourAlarm.toString().padStart(2, '0')} : ${alarm.minuteAlarm.toString().padStart(2, '0')}"
        isChecked = alarm.isEnabled
        syncWithIsChecked()
        itemView.textNextDay.text = getAllDaysString(alarm)
    }

    /*
    Private
     */

    private fun syncWithIsChecked() {
        itemView.switchAlarm.isChecked = isChecked
    }

    private fun getAllDaysString(alarm: Alarm): String {
        val realmDaysList = alarm.daysList
        realmDaysList ?: return "ErrorDays"

        val monday = realmDaysList.where().equalTo("nameOfDayEnum", EnumDayOfWeek.MONDAY.toString()).findFirst()
        val tuesday = realmDaysList.where().equalTo("nameOfDayEnum", EnumDayOfWeek.TUESDAY.toString()).findFirst()
        val wednesday = realmDaysList.where().equalTo("nameOfDayEnum", EnumDayOfWeek.WEDNESDAY.toString()).findFirst()
        val thursday = realmDaysList.where().equalTo("nameOfDayEnum", EnumDayOfWeek.THURSDAY.toString()).findFirst()
        val friday = realmDaysList.where().equalTo("nameOfDayEnum", EnumDayOfWeek.FRIDAY.toString()).findFirst()
        val saturday = realmDaysList.where().equalTo("nameOfDayEnum", EnumDayOfWeek.SATURDAY.toString()).findFirst()
        val sunday = realmDaysList.where().equalTo("nameOfDayEnum", EnumDayOfWeek.SUNDAY.toString()).findFirst()

        if (realmDaysList.size == 7) {
            return itemView.context.getString(R.string.every_day)
        } else if (realmDaysList.size == 2 && saturday != null && sunday != null) {
            return itemView.context.getString(R.string.weekend)
        }

        var dayString = ""
        val writeFirst3Letters: (RealmDayOfWeek) -> Unit = {
            dayString += "${it.nameOfDayString.toString().getFirst3Letters()}, "
        }
        monday?.let(writeFirst3Letters)
        tuesday?.let(writeFirst3Letters)
        wednesday?.let(writeFirst3Letters)
        thursday?.let(writeFirst3Letters)
        friday?.let(writeFirst3Letters)
        saturday?.let(writeFirst3Letters)
        sunday?.let(writeFirst3Letters)

        dayString = dayString.dropLast(2)

        return dayString
    }

}