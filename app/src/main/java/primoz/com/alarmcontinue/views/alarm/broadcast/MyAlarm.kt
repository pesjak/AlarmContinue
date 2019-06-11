package primoz.com.alarmcontinue.views.alarm.broadcast

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import primoz.com.alarmcontinue.model.Alarm
import java.util.*

class MyAlarm : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Put here YOUR code.
        //val path = intent.extras?.getString("Path") as String
        Toast.makeText(context, "Dela", Toast.LENGTH_LONG).show() // For example
        /*mp = MediaPlayer.create(context, Uri.parse());
        mp?.isLooping = true
        mp?.start()
        */
        Log.d("ALARM", "WORKING")

    }

    companion object {
        fun setAlarm(context: Context, alarm: Alarm) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, MyAlarm::class.java)
            //  val song = alarm.songsList?.get(0)
            //  song?.let {
            //      intent.putExtra("Path", it.path)
            //  }
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            // Set the alarm to start at 8:30 a.m.
            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, alarm.hourAlarm!!)
                set(Calendar.MINUTE, alarm.minuteAlarm!!)
            }
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                1000 * 60 * 1,
                pendingIntent
            ) //TODO Change to Interval Days
            Toast.makeText(context, "Alarm set", Toast.LENGTH_SHORT).show()
        }

        fun cancelAlarm(context: Context, alarm: Alarm) {
            val intent = Intent(context, MyAlarm::class.java)
            val sender = PendingIntent.getBroadcast(context, 0, intent, 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(sender)
            Toast.makeText(context, "Alarm canceled, ${alarm.id}", Toast.LENGTH_SHORT).show()
        }
    }
}