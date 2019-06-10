package primoz.com.alarmcontinue.views.alarm.fragments.newAlarm

import io.realm.Realm
import primoz.com.alarmcontinue.enums.EnumDayOfWeek
import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.AudioFile
import primoz.com.alarmcontinue.model.DataHelper

class NewAlarmPresenter(private val view: NewAlarmContract.View) : NewAlarmContract.Presenter {

    override fun saveAlarm(
        realm: Realm,
        hour: Int,
        minute: Int,
        selectedDays: MutableList<EnumDayOfWeek>,
        songList: MutableList<AudioFile>,
        shouldResumePlaying: Boolean,
        shouldVibrate: Boolean
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
        DataHelper.addAlarmAsync(realm, hour, minute, selectedDays, songList, shouldResumePlaying, shouldVibrate)
        view.finish()
    }

    init {
        view.setPresenter(this)
    }

}