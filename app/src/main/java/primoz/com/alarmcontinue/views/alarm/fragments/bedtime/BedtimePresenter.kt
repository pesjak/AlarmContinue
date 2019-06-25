package primoz.com.alarmcontinue.views.alarm.fragments.bedtime

import android.media.RingtoneManager
import io.realm.Realm
import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.AudioFile
import primoz.com.alarmcontinue.model.Alarm
import primoz.com.alarmcontinue.model.DataHelper

class BedtimePresenter(private val view: BedtimeContract.View) : BedtimeContract.Presenter {

    var buttonClearShown = true
    var shouldUseDefaultRingtone = false
    var secondsPlayed = 0

    override fun updateBedtime(
        realm: Realm,
        hourSleep: Int,
        minuteSleep: Int,
        hour: Int,
        minute: Int,
        songList: MutableList<AudioFile>,
        shouldResumePlaying: Boolean,
        shouldVibrate: Boolean
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
            shouldUseDefaultRingtone
        )
        view.finish()
    }

    override fun restoreUI(realm: Realm) {
        DataHelper.getBedtimeAlarm(realm)?.let { alarm ->
            secondsPlayed = alarm.secondsPlayed
            view.updateUI(alarm)
        }
    }

    override fun loadSongList(alarm: Alarm) {
        shouldUseDefaultRingtone = alarm.useDefaultRingtone
        if (shouldUseDefaultRingtone) {
            view.updateSongList(mutableListOf(getDefaultRingtone()))
            view.showNoneSelectedSongs(false)
        } else {
            val selectedSongList = getAudioFilesListFrom(alarm)
            val isEmpty = selectedSongList.isEmpty()
            view.showNoneSelectedSongs(isEmpty)
            view.showTextSetDefaultButton(isEmpty)
            if (isEmpty) {
                view.updateSongList(mutableListOf())
                buttonClearShown = !buttonClearShown
            } else {
                view.updateSongList(selectedSongList)
            }
        }
    }

    override fun handleSelectedAudioFileList(songList: ArrayList<AudioFile>) {
        view.updateSongList(songList)
        view.showNoneSelectedSongs(songList.isEmpty())
        view.showTextSetDefaultButton(songList.isEmpty())
        buttonClearShown = songList.isNotEmpty()
        shouldUseDefaultRingtone = false
    }

    override fun clearOrSetDefaultSong() {
        view.showTextSetDefaultButton(buttonClearShown)
        view.showNoneSelectedSongs(buttonClearShown)
        view.updateSongList(if (buttonClearShown) mutableListOf() else mutableListOf(getDefaultRingtone()))
        buttonClearShown = !buttonClearShown
        shouldUseDefaultRingtone = buttonClearShown
    }

    private fun getDefaultRingtone(): AudioFile {
        var alarmTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        if (alarmTone == null) {
            alarmTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            if (alarmTone == null) {
                alarmTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            }
        }
        val ringtoneAlarm = RingtoneManager.getRingtone(view.getViewActivity(), alarmTone)
        val defaultRingtone = AudioFile()
        defaultRingtone.name = ringtoneAlarm.getTitle(view.getViewActivity())
        return defaultRingtone
    }

    init {
        view.setPresenter(this)
    }

}