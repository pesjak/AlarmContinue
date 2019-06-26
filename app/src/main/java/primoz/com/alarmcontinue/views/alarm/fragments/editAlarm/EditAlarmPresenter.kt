package primoz.com.alarmcontinue.views.alarm.fragments.editAlarm

import io.realm.Realm
import primoz.com.alarmcontinue.enums.EnumDayOfWeek
import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.AudioFile
import primoz.com.alarmcontinue.model.Alarm
import primoz.com.alarmcontinue.model.DataHelper
import primoz.com.alarmcontinue.views.alarm.broadcast.MyAlarm

class EditAlarmPresenter(private val view: EditAlarmContract.View, var alarmID: Int) : EditAlarmContract.Presenter {

    var buttonClearShown = true
    var shouldUseDefaultRingtone = false
    var secondsPlayed = 0

    override fun updateAlarm(
        realm: Realm,
        hour: Int,
        minute: Int,
        selectedDays: MutableList<EnumDayOfWeek>,
        songList: MutableList<AudioFile>,
        shouldResumePlaying: Boolean,
        shouldVibrate: Boolean
    ) {
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
            shouldUseDefaultRingtone
        )
        view.finish()
    }

    override fun deleteAlarm(realm: Realm) {
        DataHelper.deleteAlarmAsync(realm, alarmID)
        view.getViewActivity().baseContext?.let {
            MyAlarm.cancelAlarm(it, alarmID) //Destroy an alarm if it is enabled
        }
        view.finish()
    }

    override fun restoreUI(realm: Realm) {
        DataHelper.getAlarm(realm, alarmID)?.let { alarm ->
            secondsPlayed = alarm.secondsPlayed
            view.updateUI(alarm)
        }
    }

    override fun loadSongList(alarm: Alarm) {
        shouldUseDefaultRingtone = alarm.useDefaultRingtone
        if (shouldUseDefaultRingtone) {
            view.updateSongList(mutableListOf(getDefaultRingtone(view.getViewActivity())))
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
        view.updateSongList(if (buttonClearShown) mutableListOf() else mutableListOf(getDefaultRingtone(view.getViewActivity())))
        buttonClearShown = !buttonClearShown
        shouldUseDefaultRingtone = buttonClearShown
    }

    init {
        view.setPresenter(this)
    }

}