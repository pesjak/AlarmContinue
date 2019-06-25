package primoz.com.alarmcontinue.views.alarm

import io.realm.Realm
import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.AudioFile
import primoz.com.alarmcontinue.model.Alarm
import java.util.ArrayList

interface BaseAlarmPresenter {
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
