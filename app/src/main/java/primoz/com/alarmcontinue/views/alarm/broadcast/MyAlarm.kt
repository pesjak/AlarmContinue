package primoz.com.alarmcontinue.views.alarm.broadcast

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import io.realm.RealmList
import primoz.com.alarmcontinue.R
import primoz.com.alarmcontinue.enums.EnumDayOfWeek
import primoz.com.alarmcontinue.extensions.getDateDiff
import primoz.com.alarmcontinue.model.Alarm
import primoz.com.alarmcontinue.model.RealmDayOfWeek
import java.util.*

class MyAlarm : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmID = intent.extras?.getInt(ARG_ALARM_ID)
        context.startActivity(TriggeredAlarmActivity.getIntent(context, alarmID))
    }

    companion object {

        const val ARG_ALARM_ID = "AlarmID"

        fun setAlarm(context: Context, alarm: Alarm, showToast: Boolean = true) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, MyAlarm::class.java)
            if (alarm.songsList?.isNotEmpty() == true) { //TODO Chcek this why you did this
                intent.putExtra(ARG_ALARM_ID, alarm.id)
            }
            val pendingIntent = PendingIntent.getBroadcast(context, alarm.id, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            var hour = 0
            var minute = 0
            var days = alarm.daysList!!

            alarm.hourAlarm?.let {
                hour = it
            }
            alarm.minuteAlarm?.let {
                minute = it
            }

            val alarmClockInfo = AlarmManager.AlarmClockInfo(
                getNextAlarmCalendar(hour, minute, days).timeInMillis,
                pendingIntent
            )
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)

            if (showToast) Toast.makeText(
                context,
                getTimeRemainingFormattedString(context, hour, minute, days),
                Toast.LENGTH_SHORT
            ).show()
        }

        fun cancelAlarm(context: Context, alarmID: Int) {
            val intent = Intent(context, MyAlarm::class.java)
            val sender = PendingIntent.getBroadcast(context, alarmID, intent, 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(sender)
        }

        /*
        Private
         */

        private fun getTimeRemainingFormattedString(
            context: Context,
            hour: Int,
            minute: Int,
            realmDays: RealmList<RealmDayOfWeek>
        ): String {
            val now = Calendar.getInstance()
            now.set(Calendar.SECOND, 0) //Set Seconds to 0, because they don't matter
            val nextAlarm = getNextAlarmCalendar(hour, minute, realmDays)
            val ms = (nextAlarm.timeInMillis - now.timeInMillis)
            now.add(Calendar.DATE, 1) //Temp Add
            return if (now.after(nextAlarm)) {
                val minutes = (ms / (1000 * 60) % 60).toInt()
                val hours = (ms / (1000 * 60 * 60) % 24).toInt()
                if (hours > 0) {
                    if (minutes == 0) {
                        context.getString(R.string.alarm_set_hour, hours)
                    } else {
                        context.getString(R.string.alarm_set_hour_minutes, hours, minutes)
                    }
                } else {
                    context.getString(R.string.alarm_set_minutes, minutes)
                }
            } else {
                now.add(Calendar.DATE, -1) //Remove Temp add
                context.getString(R.string.alarm_set_in_week, now.getDateDiff(nextAlarm))
            }
        }

        private fun getNextAlarmCalendar(
            hour: Int,
            minute: Int,
            realmDays: RealmList<RealmDayOfWeek>
        ): Calendar {
            val now = Calendar.getInstance()
            //now.add(Calendar.SECOND, 3)
            //return now
            val next = Calendar.getInstance()

            next.set(Calendar.HOUR_OF_DAY, hour)
            next.set(Calendar.MINUTE, minute)
            next.set(Calendar.SECOND, 0)

            //Should set for some other day
            if (!now.after(next)) return next

            //Set the next day, because every day is selected
            if (realmDays.size == 7) {
                next.add(Calendar.DATE, 1)
                return next
            }

            //Set next available day
            val monday = realmDays.where().equalTo("nameOfDayEnum", EnumDayOfWeek.MONDAY.toString()).findFirst()
            val tuesday = realmDays.where().equalTo("nameOfDayEnum", EnumDayOfWeek.TUESDAY.toString()).findFirst()
            val wednesday = realmDays.where().equalTo("nameOfDayEnum", EnumDayOfWeek.WEDNESDAY.toString()).findFirst()
            val thursday = realmDays.where().equalTo("nameOfDayEnum", EnumDayOfWeek.THURSDAY.toString()).findFirst()
            val friday = realmDays.where().equalTo("nameOfDayEnum", EnumDayOfWeek.FRIDAY.toString()).findFirst()
            val saturday = realmDays.where().equalTo("nameOfDayEnum", EnumDayOfWeek.SATURDAY.toString()).findFirst()
            val sunday = realmDays.where().equalTo("nameOfDayEnum", EnumDayOfWeek.SUNDAY.toString()).findFirst()

            val statusAllDayOfTheWeekList: MutableList<Boolean> = mutableListOf(
                monday != null,
                tuesday != null,
                wednesday != null,
                thursday != null,
                friday != null,
                saturday != null,
                sunday != null
            )

            var nextDay = next.get(Calendar.DAY_OF_WEEK) - 1 // index on 0-6, rather than the 1-7 returned by Calendar

            var i = 0
            while (i < 7 && !statusAllDayOfTheWeekList[nextDay]) {
                nextDay++
                nextDay %= 7
                i++
            }
            val nextDayToSet = nextDay + 2 //TODO Works but needs further testing
            next.set(Calendar.DAY_OF_WEEK, nextDayToSet) // + 1 = back to 1-7 range

            while (now.after(next)) next.add(Calendar.DATE, 7)
            return next
        }

    }
}
