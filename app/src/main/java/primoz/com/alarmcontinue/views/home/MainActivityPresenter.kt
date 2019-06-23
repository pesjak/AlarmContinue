package primoz.com.alarmcontinue.views.home

import io.realm.Realm
import primoz.com.alarmcontinue.enums.AlarmType
import primoz.com.alarmcontinue.model.Alarm
import primoz.com.alarmcontinue.model.AlarmList
import primoz.com.alarmcontinue.model.DataHelper
import primoz.com.alarmcontinue.views.alarm.AlarmActivity
import primoz.com.alarmcontinue.views.alarm.broadcast.MyAlarm


class MainActivityPresenter(private val view: MainActivityContract.View) : MainActivityContract.Presenter {

    override fun showBedtimeAlarmScreen() {
        val activity = view.getActivity()
        activity.startActivity(AlarmActivity.getIntent(activity, AlarmType.BEDTIME))
    }

    override fun enableAlarm(realm: Realm, alarm: Alarm, shouldEnable: Boolean) {
        DataHelper.shouldEnableAlarm(alarm.id, shouldEnable, realm)
        if (shouldEnable) {
            MyAlarm.setAlarm(view.getActivity(), alarm)
        } else {
            MyAlarm.cancelAlarm(view.getActivity(), alarm.id)
        }
    }

    override fun enableBedtime(realm: Realm, shouldEnable: Boolean) {
        DataHelper.getBedtimeAlarm(realm)?.let { bedtime ->
            DataHelper.shouldEnableAlarm(bedtime.id, shouldEnable, realm)
            if (shouldEnable) {
                MyAlarm.setAlarm(view.getActivity(), bedtime)
            } else {
                MyAlarm.cancelAlarm(view.getActivity(), bedtime.id)
            }
            view.updateBedtime(bedtime, shouldEnable)
        }
    }

    override fun showAddNewAlarmScreen() {
        val activity = view.getActivity()
        activity.startActivity(AlarmActivity.getIntent(activity, AlarmType.NEW_ALARM))
    }

    override fun showEditAlarmScreen(alarm: Alarm) {
        val activity = view.getActivity()
        activity.startActivity(AlarmActivity.getIntent(activity, AlarmType.EDIT_ALARM, alarm.id))
    }

    override fun loadAlarms(realm: Realm) {
        val alarmList = realm.where(AlarmList::class.java).findFirst()?.alarmList!!
        view.showAlarms(alarmList)
    }

    override fun loadBedtime(realm: Realm) {
        DataHelper.getBedtimeAlarm(realm)?.let { bedtime ->
            view.updateBedtime(bedtime, bedtime.isEnabled)
        }
    }

    init {
        view.setPresenter(this)
    }

}