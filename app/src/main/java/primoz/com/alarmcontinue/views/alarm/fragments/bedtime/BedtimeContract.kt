package primoz.com.alarmcontinue.views.alarm.fragments.bedtime

import android.app.Activity
import io.realm.Realm
import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.AudioFile
import primoz.com.alarmcontinue.model.Alarm
import primoz.com.alarmcontinue.views.BaseView

interface BedtimeContract {
    interface View : BaseView<Presenter> {
        fun finish()
        fun updateUI(alarm: Alarm)
    }

    interface Presenter {
        fun updateBedtime(
            realm: Realm,
            hourSleep: Int,
            minuteSleep: Int,
            hour: Int,
            minute: Int,
            songList: MutableList<AudioFile>,
            shouldResumePlaying: Boolean,
            shouldVibrate: Boolean,
            isDefaultRingtone: Boolean
        )

        fun restoreUI(realm: Realm)
    }
}