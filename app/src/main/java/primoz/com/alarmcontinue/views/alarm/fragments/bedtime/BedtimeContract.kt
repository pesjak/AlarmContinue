package primoz.com.alarmcontinue.views.alarm.fragments.bedtime

import io.realm.Realm
import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.AudioFile
import primoz.com.alarmcontinue.model.Alarm
import primoz.com.alarmcontinue.views.BaseView
import java.util.*

interface BedtimeContract {
    interface View : BaseView<Presenter> {
        fun finish()
        fun updateUI(alarm: Alarm)
        fun updateSongList(selectedSongList: MutableList<AudioFile>)
        fun showNoneSelectedSongs(shouldShow: Boolean = true)
        fun showTextSetDefaultButton(shouldShow: Boolean = true)
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
            shouldVibrate: Boolean
        )

        fun restoreUI(realm: Realm)
        fun getAudioFilesListFrom(alarm: Alarm): MutableList<AudioFile> {
            val selectedSongList = mutableListOf<AudioFile>()
            alarm.songsList?.let {
                for (song in it) {
                    val audioFile = AudioFile()
                    audioFile.name = song.name
                    song.duration?.let { duration -> audioFile.duration = duration }
                    audioFile.path = song.path
                    audioFile.bucketId = song.bucketId
                    audioFile.bucketName = song.bucketName
                    song.size?.let { size -> audioFile.size = size }
                    selectedSongList.add(audioFile)
                }
            }
            return selectedSongList
        }

        fun loadSongList(alarm: Alarm)
        fun handleSelectedAudioFileList(songList: ArrayList<AudioFile>)
        fun clearOrSetDefaultSong()
    }
}