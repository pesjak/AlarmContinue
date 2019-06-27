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
        val activity = view.getViewActivity()
        activity.startActivityForResult(
            AlarmActivity.getIntent(activity, AlarmType.BEDTIME),
            MainActivity.ARG_BEDTIME_SCREEN_REQUEST_CODE
        )
    }

    override fun enableAlarm(realm: Realm, alarm: Alarm, shouldEnable: Boolean) {
        DataHelper.shouldEnableAlarm(alarm.id, shouldEnable, realm)
        if (shouldEnable) {
            MyAlarm.setAlarm(view.getViewActivity(), alarm)
        } else {
            MyAlarm.cancelAlarm(view.getViewActivity(), alarm.id)
        }
    }

    override fun enableBedtime(realm: Realm, shouldEnable: Boolean) {
        val bedtimeAlarm = DataHelper.getBedtimeAlarm(realm)
        bedtimeAlarm?.let { bedtime ->
            DataHelper.shouldEnableAlarm(bedtime.id, shouldEnable, realm)
            if (shouldEnable) {
                MyAlarm.setAlarm(view.getViewActivity(), bedtime)
            } else {
                MyAlarm.cancelAlarm(view.getViewActivity(), bedtime.id)
            }
            view.updateBedtime(bedtime, shouldEnable)
        }
    }

    override fun loadBedtime(realm: Realm) {
        val bedtime = DataHelper.getBedtimeAlarm(realm)
        view.showBedtimeClocks(bedtime != null)
        bedtime?.let {
            view.updateBedtime(it, it.isEnabled)
        }
    }

    override fun showAddNewAlarmScreen() {
        val activity = view.getViewActivity()
        activity.startActivity(AlarmActivity.getIntent(activity, AlarmType.NEW_ALARM))
    }

    override fun showEditAlarmScreen(alarm: Alarm) {
        val activity = view.getViewActivity()
        activity.startActivity(AlarmActivity.getIntent(activity, AlarmType.EDIT_ALARM, alarm.id))
    }

    override fun loadAlarms(realm: Realm) {
        val alarmList = realm.where(AlarmList::class.java).findFirst()?.alarmList!!
        alarmList.addChangeListener { list ->
            view.showEmptyState(list.isEmpty())
        }
        view.showEmptyState(alarmList.isEmpty())
        view.showAlarms(alarmList)
    }

    init {
        view.setPresenter(this)
    }

}