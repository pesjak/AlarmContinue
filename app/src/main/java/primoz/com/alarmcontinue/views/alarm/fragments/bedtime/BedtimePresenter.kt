package primoz.com.alarmcontinue.views.alarm.fragments.bedtime

import androidx.appcompat.app.AlertDialog
import io.realm.Realm
import primoz.com.alarmcontinue.R
import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.AudioFile
import primoz.com.alarmcontinue.model.Alarm
import primoz.com.alarmcontinue.model.DataHelper

class BedtimePresenter(private val view: BedtimeContract.View) : BedtimeContract.Presenter {

    var buttonClearShown = true
    var shouldUseDefaultRingtone = false

    private val reminderDialog by lazy {
        AlertDialog.Builder(view.getViewActivity(), R.style.AlertDialogCustom)
    }

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

    override fun loadReminderOptions() { //TODO Implement logic for notifications and handling these values, maybe enums?
        val listOfOptions = arrayListOf(
            "Never",
            "10 min",
            "20 min",
            "30 min",
            "1 h",
            "2 h"
        )
        val reminderCharSequenceList =
            listOfOptions.toArray(arrayOfNulls<CharSequence>(listOfOptions.size))

        reminderDialog.setSingleChoiceItems(reminderCharSequenceList, 0) { dialog, which ->
            val reminder = reminderCharSequenceList[which]
            reminder?.let { view.showReminder(reminder) }
            dialog.dismiss()
        }

        reminderDialog.setTitle(view.getViewActivity().getString(R.string.pick_a_folder))
        reminderDialog.show()
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