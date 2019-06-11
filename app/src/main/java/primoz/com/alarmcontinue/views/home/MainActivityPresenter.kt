package primoz.com.alarmcontinue.views.home

import android.media.MediaPlayer
import android.net.Uri
import io.realm.Realm
import primoz.com.alarmcontinue.enums.AlarmType
import primoz.com.alarmcontinue.model.Alarm
import primoz.com.alarmcontinue.model.AlarmList
import primoz.com.alarmcontinue.views.alarm.AlarmActivity
import primoz.com.alarmcontinue.views.alarm.broadcast.MyAlarm
import java.text.SimpleDateFormat
import java.util.*

class MainActivityPresenter(private val view: MainActivityContract.View) : MainActivityContract.Presenter {

    override fun showBedtimeAlarmScreen() {
        val activity = view.getActivity()
        activity.startActivity(AlarmActivity.getIntent(activity, AlarmType.BEDTIME))
    }

    override fun enableAlarm(realm: Realm, alarm: Alarm, shouldEnable: Boolean) {
        if (shouldEnable) {
            MyAlarm.setAlarm(view.getActivity(), alarm)
        } else {
            MyAlarm.cancelAlarm(view.getActivity(), alarm)
        }
    }

    override fun enableBedtime(realm: Realm, shouldEnable: Boolean) {
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
        view.showAlarms(realm.where(AlarmList::class.java).findFirst()!!.alarmList!!)
    }

    override fun loadCurrentTime() {
        view.showDate(SimpleDateFormat("EEE, MMMM d", Locale.getDefault()).format(Date()))
    }

    init {
        view.setPresenter(this)
    }

}