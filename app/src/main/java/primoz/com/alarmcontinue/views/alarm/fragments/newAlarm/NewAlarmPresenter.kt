package primoz.com.alarmcontinue.views.alarm.fragments.newAlarm

import io.realm.Realm
import primoz.com.alarmcontinue.enums.EnumDayOfWeek
import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.AudioFile
import primoz.com.alarmcontinue.model.DataHelper
import java.util.*

class NewAlarmPresenter(private val view: NewAlarmContract.View) : NewAlarmContract.Presenter {

    var buttonClearShown = true
    var shouldUseDefaultRingtone = false

    override fun saveAlarm(
        realm: Realm,
        hour: Int,
        minute: Int,
        selectedDays: MutableList<EnumDayOfWeek>,
        songList: MutableList<AudioFile>,
        shouldResumePlaying: Boolean,
        shouldVibrate: Boolean
    ) {
        DataHelper.addAlarm(
            realm,
            hour,
            minute,
            selectedDays,
            songList,
            shouldResumePlaying,
            shouldVibrate,
            null,
            null,
            shouldUseDefaultRingtone
        )
        view.finish()
    }

    override fun loadSongList() {
        shouldUseDefaultRingtone = true
        view.updateSongList(mutableListOf(getDefaultRingtone(view.getViewActivity())))
        view.showNoneSelectedSongs(false)
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
        view.showDefaultUI()
    }
}