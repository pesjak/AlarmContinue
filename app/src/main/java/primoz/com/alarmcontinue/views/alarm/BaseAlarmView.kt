package primoz.com.alarmcontinue.views.alarm

import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.AudioFile
import primoz.com.alarmcontinue.model.Alarm

interface BaseAlarmView {
    fun finish()
    fun updateSongList(selectedSongList: MutableList<AudioFile>)
    fun showNoneSelectedSongs(shouldShow: Boolean = true)
    fun showTextSetDefaultButton(shouldShow: Boolean = true)
}
