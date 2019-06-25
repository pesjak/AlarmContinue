package primoz.com.alarmcontinue.views.alarm.fragments.bedtime

import io.realm.Realm
import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.AudioFile
import primoz.com.alarmcontinue.model.Alarm
import primoz.com.alarmcontinue.views.BaseView
import primoz.com.alarmcontinue.views.alarm.BaseAlarmPresenter
import primoz.com.alarmcontinue.views.alarm.BaseAlarmView

interface BedtimeContract {

    interface View : BaseView<Presenter>, BaseAlarmView{
        fun updateUI(alarm: Alarm)
    }

    interface Presenter: BaseAlarmPresenter {
        fun restoreUI(realm: Realm)
        fun loadSongList(alarm: Alarm)
        fun updateBedtime(
            realm: Realm,
            hourSleep: Int,
            minuteSleep: Int,
            hour: Int,
            minute: Int,
            songList: MutableList<AudioFile>,
            shouldResumePlaying: Boolean,
            shouldVibrate: Boolean
        )
    }
}