package primoz.com.alarmcontinue.views.alarm.broadcast

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import io.realm.RealmList
import primoz.com.alarmcontinue.enums.EnumDayOfWeek
import primoz.com.alarmcontinue.model.Alarm
import primoz.com.alarmcontinue.model.RealmDayOfWeek
import primoz.com.alarmcontinue.views.alarm.TriggeredAlarmActivity
import java.util.*

//TODO CHANGE TO NOTIFICATION
class MyAlarm : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmID = intent.extras?.getInt(ARG_ALARM_ID)
        context.startActivity(TriggeredAlarmActivity.getIntent(context, alarmID))
    }

    companion object {
        val ARG_ALARM_ID = "AlarmID"
        fun setAlarm(context: Context, alarm: Alarm) {
            /*
            TODO
            - setRandomSongID as intent
            - getRealm and getThatSong
            - if the song is completed go next random
              else just continue currently selected song
             */
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, MyAlarm::class.java)
            if (alarm.songsList?.isNotEmpty() == true) {
                intent.putExtra(ARG_ALARM_ID, alarm.id)
            }
            val pendingIntent = PendingIntent.getBroadcast(context, alarm.id, intent, 0)

            //TODO Add Days, and if today is already passed just set for tomrrow
            //TODO Show toast when will it trigger
            /*
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                1000 * 30 * 1, //30s
                pendingIntent
            ) //TODO Change to Interval Days
            */

            var hour = 0
            var minute = 0
            var days = alarm.daysList!!

            alarm.hourAlarm?.let {
                hour = it
            }

            alarm.hourBedtimeSleep?.let {
                hour = it
            }

            alarm.minuteBedtimeSleep?.let {
                minute = it
            }

            alarm.minuteAlarm?.let {
                minute = it
            }

            val alarmClockInfo = AlarmManager.AlarmClockInfo(
                getNext(hour, minute, days).timeInMillis,
                pendingIntent
            )

            Log.d("TimeInMilis", alarmClockInfo.triggerTime.toString())

            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)

            //Toast.makeText(context, "Alarm set", Toast.LENGTH_SHORT).show()
        }

        fun cancelAlarm(context: Context, alarm: Alarm) {
            val intent = Intent(context, MyAlarm::class.java)
            val sender = PendingIntent.getBroadcast(context, alarm.id, intent, 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(sender)
            Toast.makeText(context, "Alarm canceled, ${alarm.id}", Toast.LENGTH_SHORT).show()
        }

        private fun getNext(
            hour: Int,
            minute: Int,
            realmDays: RealmList<RealmDayOfWeek>
        ): Calendar {
            val now = Calendar.getInstance()
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
            var nextDayToSet = nextDay + 2 //TODO Works but needs further testing
            next.set(Calendar.DAY_OF_WEEK, nextDayToSet) // + 1 = back to 1-7 range

            while (now.after(next)) next.add(Calendar.DATE, 7)

            return next
        }
    }
}
