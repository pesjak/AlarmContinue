package primoz.com.alarmcontinue.views.alarm.broadcast

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import io.realm.Realm
import io.realm.RealmList
import primoz.com.alarmcontinue.R
import primoz.com.alarmcontinue.enums.EnumDayOfWeek
import primoz.com.alarmcontinue.extensions.getDateDiff
import primoz.com.alarmcontinue.model.Alarm
import primoz.com.alarmcontinue.model.DataHelper
import primoz.com.alarmcontinue.model.RealmDayOfWeek
import primoz.com.alarmcontinue.views.alarm.services.SleepReminderService
import primoz.com.alarmcontinue.views.home.MainActivity
import java.util.*

class MyAlarmDismiss : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmID = intent.extras?.getInt(ARG_ALARM_ID)

        //Immediatly set new alarm
        val realm = Realm.getDefaultInstance()
        val alarmFromRealm = DataHelper.getAlarm(realm, alarmID!!)
        alarmFromRealm?.let { alarm ->
            val shouldEnableAlarm = alarm.isEnabled && alarm.daysList!!.isNotEmpty()
            DataHelper.enableAlarm(alarmID, false, realm)
            if (shouldEnableAlarm) {
                MyAlarm.setAlarm(context, alarm, false)
            } else {
                MyAlarm.cancelAlarm(context, alarm.id)
            }
        }
        context.stopService(Intent(context, SleepReminderService::class.java))
    }

    companion object {

        const val ARG_ALARM_ID = "AlarmID"

    }
}
