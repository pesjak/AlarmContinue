package primoz.com.alarmcontinue.views.alarm.fragments.editAlarm

import android.app.Activity
import io.realm.Realm
import primoz.com.alarmcontinue.enums.EnumDayOfWeek
import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.AudioFile
import primoz.com.alarmcontinue.model.Alarm
import primoz.com.alarmcontinue.views.BaseView

interface EditAlarmContract {
    interface View : BaseView<Presenter> {
        fun finish()
        fun viewActivity(): Activity?
        fun updateUI(alarm: Alarm)
    }

    interface Presenter {
        fun updateAlarm(
            realm: Realm,
            hour: Int,
            minute: Int,
            selectedDays: MutableList<EnumDayOfWeek>,
            songList: MutableList<AudioFile>,
            shouldResumePlaying: Boolean,
            shouldVibrate: Boolean,
            isDefaultRingtone: Boolean
        )

        fun deleteAlarm(realm: Realm)
        fun restoreUI(realm: Realm)
    }
}