package primoz.com.alarmcontinue.views.settings

import io.realm.Realm
import primoz.com.alarmcontinue.enums.AlarmType
import primoz.com.alarmcontinue.model.Alarm
import primoz.com.alarmcontinue.model.AlarmList
import primoz.com.alarmcontinue.model.DataHelper
import primoz.com.alarmcontinue.views.alarm.AlarmActivity
import primoz.com.alarmcontinue.views.alarm.broadcast.MyAlarm

class SettingsActivityPresenter(private val view: SettingsActivityContract.View) : SettingsActivityContract.Presenter {

    init {
        view.setPresenter(this)
    }

}