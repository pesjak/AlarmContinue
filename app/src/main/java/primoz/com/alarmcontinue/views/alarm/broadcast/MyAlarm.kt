package primoz.com.alarmcontinue.views.alarm.broadcast

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import primoz.com.alarmcontinue.model.Alarm
import primoz.com.alarmcontinue.views.alarm.TriggeredAlarmActivity
import java.util.*

class MyAlarm : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmID = intent.extras?.getInt(ARG_ALARM_ID)
        val path = intent.extras?.getString(ARG_PATH)
        context.startActivity(TriggeredAlarmActivity.getIntent(context, alarmID, path))
    }

    companion object {
        val ARG_ALARM_ID = "AlarmID"
        val ARG_PATH = "Path"
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
                intent.putExtra(ARG_PATH, alarm.currentlySelectedPath)
            }
            val pendingIntent = PendingIntent.getBroadcast(context, alarm.id, intent, 0)
            // Set the alarm to start at 8:30 a.m.
            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, alarm.hourAlarm!!)
                set(Calendar.MINUTE, alarm.minuteAlarm!!)
            }
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                1000 * 30 * 1, //30s
                pendingIntent
            ) //TODO Change to Interval Days
            Toast.makeText(context, "Alarm set", Toast.LENGTH_SHORT).show()
        }

        fun cancelAlarm(context: Context, alarm: Alarm) {
            val intent = Intent(context, MyAlarm::class.java)
            val sender = PendingIntent.getBroadcast(context, alarm.id, intent, 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(sender)
            Toast.makeText(context, "Alarm canceled, ${alarm.id}", Toast.LENGTH_SHORT).show()
        }
    }
}