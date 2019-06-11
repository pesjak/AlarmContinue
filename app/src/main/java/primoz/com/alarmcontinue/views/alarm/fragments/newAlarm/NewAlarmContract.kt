package primoz.com.alarmcontinue.views.alarm.fragments.newAlarm

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import io.realm.Realm
import primoz.com.alarmcontinue.enums.EnumDayOfWeek
import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.AudioFile
import primoz.com.alarmcontinue.views.BaseView

interface NewAlarmContract {
    interface View : BaseView<Presenter> {
        fun finish()
        fun viewActivity(): Activity?
    }

    interface Presenter {
        fun saveAlarm(
            realm: Realm,
            hour: Int,
            minute: Int,
            selectedDays: MutableList<EnumDayOfWeek>,
            songList: MutableList<AudioFile>,
            shouldResumePlaying: Boolean,
            shouldVibrate: Boolean
        )
    }
}