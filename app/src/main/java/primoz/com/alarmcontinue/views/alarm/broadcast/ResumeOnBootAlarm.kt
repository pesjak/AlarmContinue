package primoz.com.alarmcontinue.views.alarm.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.realm.Realm
import primoz.com.alarmcontinue.model.DataHelper

class ResumeOnBootAlarm : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val realm = Realm.getDefaultInstance()

        val bedtime = DataHelper.getBedtimeAlarm(realm)
        bedtime?.let {
            if (it.isEnabled) MyAlarm.setAlarm(context, it, false)
        }

        val alarmList = DataHelper.getAllAlarms(realm)?.toMutableList()
        alarmList?.let {
            for (alarm in it) {
                if (alarm.isEnabled) MyAlarm.setAlarm(context, alarm, false)
            }
        }

        realm.close()
    }
}
