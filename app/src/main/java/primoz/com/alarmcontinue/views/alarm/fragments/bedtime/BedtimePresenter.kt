package primoz.com.alarmcontinue.views.alarm.fragments.bedtime

import io.realm.Realm
import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.AudioFile
import primoz.com.alarmcontinue.model.DataHelper

class BedtimePresenter(private val view: BedtimeContract.View) : BedtimeContract.Presenter {

    var secondsPlayed = 0

    override fun updateBedtime(
        realm: Realm,
        hourSleep: Int,
        minuteSleep: Int,
        hour: Int,
        minute: Int,
        songList: MutableList<AudioFile>,
        shouldResumePlaying: Boolean,
        shouldVibrate: Boolean,
        isDefaultRingtone: Boolean
    ) {
        DataHelper.updateBedtime(
            realm,
            hourSleep,
            minuteSleep,
            hour,
            minute,
            songList,
            shouldResumePlaying,
            shouldVibrate,
            isDefaultRingtone
        )
        view.finish()
    }

    override fun restoreUI(realm: Realm) {
        DataHelper.getBedtimeAlarm(realm)?.let { alarm ->
            secondsPlayed = alarm.secondsPlayed
            view.updateUI(alarm)
        }
    }

    init {
        view.setPresenter(this)
    }

}