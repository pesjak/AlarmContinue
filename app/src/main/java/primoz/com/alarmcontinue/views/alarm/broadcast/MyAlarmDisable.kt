package primoz.com.alarmcontinue.views.alarm.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.realm.Realm
import primoz.com.alarmcontinue.model.DataHelper
import primoz.com.alarmcontinue.views.alarm.services.SleepReminderService

class MyAlarmDisable : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmID = intent.extras?.getInt(ARG_ALARM_DISABLE_ID)

        //Immediatly set new alarm
        val realm = Realm.getDefaultInstance()
        val alarmFromRealm = DataHelper.getAlarm(realm, alarmID!!)
        alarmFromRealm?.let { alarm ->
            DataHelper.enableAlarm(alarmID, false, realm)
            MyAlarm.cancelAlarm(context, alarm.id)
        }
        context.stopService(Intent(context, SleepReminderService::class.java))
    }

    companion object {

        const val ARG_ALARM_DISABLE_ID = "AlarmIDDisable"

    }
}
