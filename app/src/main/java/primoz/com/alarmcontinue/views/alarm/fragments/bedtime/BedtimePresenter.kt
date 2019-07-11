package primoz.com.alarmcontinue.views.alarm.fragments.bedtime

import androidx.appcompat.app.AlertDialog
import io.realm.Realm
import primoz.com.alarmcontinue.R
import primoz.com.alarmcontinue.enums.EnumDayOfWeek
import primoz.com.alarmcontinue.enums.EnumNotificationTime
import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.AudioFile
import primoz.com.alarmcontinue.model.Alarm
import primoz.com.alarmcontinue.model.DataHelper

class BedtimePresenter(private val view: BedtimeContract.View) : BedtimeContract.Presenter {

    var buttonClearShown = true
    var shouldUseDefaultRingtone = false
    var notificationTime: EnumNotificationTime = EnumNotificationTime.NONE

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
        if (DataHelper.alreadySameAlarm(realm, hour, minute)) {
            view.showToast(view.getViewActivity().getString(R.string.already_same_alarm))
        } else {
            DataHelper.updateBedtime(
                realm,
                hourSleep,
                minuteSleep,
                hour,
                minute,
                songList,
                shouldResumePlaying,
                shouldVibrate,
                shouldUseDefaultRingtone,
                notificationTime
            )
            view.finish()
        }
    }

    override fun restoreUI(realm: Realm) {
        DataHelper.getBedtimeAlarm(realm)?.let { alarm ->
            notificationTime = alarm.notificationTime!!.notificationTimeString
            view.showReminder(notificationTime.realName)
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

    override fun loadReminderOptions(realm: Realm) {
        val listOfOptionsEnums = arrayListOf(
            EnumNotificationTime.NONE.realName,
            EnumNotificationTime.MIN_10.realName,
            EnumNotificationTime.MIN_20.realName,
            EnumNotificationTime.MIN_30.realName,
            EnumNotificationTime.MIN_40.realName,
            EnumNotificationTime.MIN_50.realName,
            EnumNotificationTime.HOUR_1.realName,
            EnumNotificationTime.HOUR_2.realName,
            EnumNotificationTime.HOUR_3.realName
        )

        val listOfEnumNotificationTime = arrayListOf(
            EnumNotificationTime.NONE,
            EnumNotificationTime.MIN_10,
            EnumNotificationTime.MIN_20,
            EnumNotificationTime.MIN_30,
            EnumNotificationTime.MIN_40,
            EnumNotificationTime.MIN_50,
            EnumNotificationTime.HOUR_1,
            EnumNotificationTime.HOUR_2,
            EnumNotificationTime.HOUR_3
        )

        val reminderCharSequenceList = listOfOptionsEnums.toArray(arrayOfNulls<CharSequence>(listOfOptionsEnums.size))

        reminderDialog.setSingleChoiceItems(reminderCharSequenceList, -1) { dialog, which ->
            notificationTime = listOfEnumNotificationTime[which]
            view.showReminder(notificationTime.realName)
            dialog.dismiss()
        }

        reminderDialog.setTitle(view.getViewActivity().getString(R.string.bedtime_reminder))
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