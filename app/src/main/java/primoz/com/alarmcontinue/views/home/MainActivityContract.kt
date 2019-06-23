package primoz.com.alarmcontinue.views.home

import android.app.Activity
import io.realm.Realm
import io.realm.RealmList
import primoz.com.alarmcontinue.model.Alarm
import primoz.com.alarmcontinue.views.BaseView

interface MainActivityContract {
    interface View : BaseView<Presenter> {
        fun getActivity(): Activity
        fun showAlarms(alarmList: RealmList<Alarm>)
        fun showEnabledBedtime(shouldEnable: Boolean = false)
        fun updateBedtime(bedtime: Alarm, shouldEnable: Boolean)
    }

    interface Presenter {
        fun loadAlarms(realm: Realm)
        fun showEditAlarmScreen(alarm: Alarm)
        fun enableAlarm(realm: Realm, alarm: Alarm, shouldEnable: Boolean)
        fun enableBedtime(realm: Realm, shouldEnable: Boolean)
        fun showAddNewAlarmScreen()
        fun showBedtimeAlarmScreen()
        fun loadBedtime(realm: Realm)
    }
}