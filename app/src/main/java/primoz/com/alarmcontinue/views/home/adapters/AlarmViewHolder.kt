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
import java.util.*

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
        itemView.switchAlarm.alpha = if (isChecked) 1f else 0.5f
        itemView.textAlarm.alpha = if (isChecked) 1f else 0.2f
        itemView.textNextDay.alpha = if (isChecked) 1f else 0.2f
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

        if (realmDaysList.isEmpty()) {
            //Either TODAY or Tomorrow 1 time thing
            val now = Calendar.getInstance()
            now.set(Calendar.SECOND, 0)
            val next = Calendar.getInstance()
            next.set(Calendar.HOUR_OF_DAY, alarm.hourAlarm!!)
            next.set(Calendar.MINUTE, alarm.minuteAlarm!!)
            next.set(Calendar.SECOND, 0)

            return if (now.after(next)) {
                itemView.context.getString(R.string.tomorrow)
            } else {
                itemView.context.getString(R.string.today)
            }
        }

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