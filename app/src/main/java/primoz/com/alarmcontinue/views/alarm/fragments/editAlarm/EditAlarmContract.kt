package primoz.com.alarmcontinue.views.alarm.fragments.editAlarm

import io.realm.Realm
import primoz.com.alarmcontinue.enums.EnumDayOfWeek
import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.AudioFile
import primoz.com.alarmcontinue.model.Alarm
import primoz.com.alarmcontinue.views.BaseView
import primoz.com.alarmcontinue.views.alarm.BaseAlarmPresenter
import primoz.com.alarmcontinue.views.alarm.BaseAlarmView

interface EditAlarmContract {

    interface View : BaseView<Presenter>, BaseAlarmView {
        fun updateUI(alarm: Alarm)
    }

    interface Presenter : BaseAlarmPresenter {
        fun restoreUI(realm: Realm)
        fun loadSongList(alarm: Alarm)
        fun updateAlarm(
            realm: Realm,
            hour: Int,
            minute: Int,
            selectedDays: MutableList<EnumDayOfWeek>,
            songList: MutableList<AudioFile>,
            shouldResumePlaying: Boolean,
            shouldVibrate: Boolean
        )

        fun deleteAlarm(realm: Realm)
    }
}