package primoz.com.alarmcontinue.views.home

import io.realm.Realm
import primoz.com.alarmcontinue.model.Alarm
import primoz.com.alarmcontinue.views.BasePresenter
import primoz.com.alarmcontinue.views.BaseView

interface MainActivityContract {
    interface View : BaseView<Presenter> {
        fun showDate(date: String)
        fun showEnabledBedtime(shouldEnable: Boolean = false)
    }

    interface Presenter : BasePresenter {
        fun loadAlarms()
        fun loadCurrentTime()
        fun showEditAlarmScreen(alarm: Alarm)
        fun enableAlarm(realm: Realm, alarm: Alarm, shouldEnable: Boolean)
        fun enableBedtime(realm: Realm, shouldEnable: Boolean)
        fun showAddNewAlarm(realm: Realm)
    }
}