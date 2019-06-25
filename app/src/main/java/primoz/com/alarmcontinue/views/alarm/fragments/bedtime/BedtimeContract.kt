package primoz.com.alarmcontinue.views.alarm.fragments.bedtime

import io.realm.Realm
import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.AudioFile
import primoz.com.alarmcontinue.model.Alarm
import primoz.com.alarmcontinue.views.BaseView
import primoz.com.alarmcontinue.views.alarm.BaseAlarmPresenter
import java.util.*

interface BedtimeContract {
    interface View : BaseView<Presenter> {
        fun finish()
        fun updateUI(alarm: Alarm)
        fun updateSongList(selectedSongList: MutableList<AudioFile>)
        fun showNoneSelectedSongs(shouldShow: Boolean = true)
        fun showTextSetDefaultButton(shouldShow: Boolean = true)
    }

    interface Presenter: BaseAlarmPresenter {
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