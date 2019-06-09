package primoz.com.alarmcontinue.libraries.filepicker.adapter

import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.AudioFile

interface OnPlayListener {
    fun OnPlayClicked(audioFile: AudioFile)
}
