package primoz.com.alarmcontinue.views.alarm.fragments.editAlarm

import io.realm.Realm
import primoz.com.alarmcontinue.enums.EnumDayOfWeek
import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.AudioFile
import primoz.com.alarmcontinue.model.DataHelper
import primoz.com.alarmcontinue.views.alarm.broadcast.MyAlarm

class EditAlarmPresenter(private val view: EditAlarmContract.View, var alarmID: Int) : EditAlarmContract.Presenter {

    var secondsPlayed = 0

    override fun updateAlarm(
        realm: Realm,
        hour: Int,
        minute: Int,
        selectedDays: MutableList<EnumDayOfWeek>,
        songList: MutableList<AudioFile>,
        shouldResumePlaying: Boolean,
        shouldVibrate: Boolean,
        isDefaultRingtone: Boolean
    ) {
        if (selectedDays.isEmpty()) {
            selectedDays.add(EnumDayOfWeek.MONDAY)
            selectedDays.add(EnumDayOfWeek.TUESDAY)
            selectedDays.add(EnumDayOfWeek.WEDNESDAY)
            selectedDays.add(EnumDayOfWeek.THURSDAY)
            selectedDays.add(EnumDayOfWeek.FRIDAY)
            selectedDays.add(EnumDayOfWeek.SATURDAY)
            selectedDays.add(EnumDayOfWeek.SUNDAY)
        }
        DataHelper.editAlarmAsync(
            alarmID,
            realm,
            true,
            hour,
            minute,
            selectedDays,
            songList,
            shouldResumePlaying,
            shouldVibrate,
            secondsPlayed,
            null,
            null,
            isDefaultRingtone
        )
        view.finish()
    }

    override fun deleteAlarm(realm: Realm) {
        DataHelper.deleteAlarmAsync(realm, alarmID)
        view.getViewActivity().baseContext?.let {
            //Destroy an alarm if it is enabled
            MyAlarm.cancelAlarm(it, alarmID)
        }
        view.finish()
    }

    override fun restoreUI(realm: Realm) {
        DataHelper.getAlarm(realm, alarmID)?.let { alarm ->
            secondsPlayed = alarm.secondsPlayed
            view.updateUI(alarm)
        }
    }

    init {
        view.setPresenter(this)
    }

}