/*
 * Copyright 2017 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package primoz.com.alarmcontinue.model

import android.media.RingtoneManager
import io.realm.Realm
import io.realm.RealmList
import primoz.com.alarmcontinue.MyApplication
import primoz.com.alarmcontinue.enums.EnumDayOfWeek
import primoz.com.alarmcontinue.enums.EnumNotificationTime
import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.AudioFile
import primoz.com.alarmcontinue.views.alarm.broadcast.MyAlarm
import primoz.com.alarmcontinue.views.alarm.broadcast.MyNotification

object DataHelper {

    fun addAlarm(
        realm: Realm,
        hourAlarm: Int,
        minuteAlarm: Int,
        daysList: MutableList<EnumDayOfWeek>,
        songList: MutableList<AudioFile>,
        shouldResumePlaying: Boolean = false,
        shouldVibrate: Boolean = false,
        hourBedtimeSleep: Int? = null,
        minuteBedtimeSleep: Int? = null,
        useDefaultRingtone: Boolean = false
    ) {
        realm.executeTransaction { realmInTransaction ->
            val (realmDayOfTheWeekList, realmSongList) = convertSelectedDaysAndSongsToRealmList(
                daysList,
                realmInTransaction,
                songList
            )

            //Save
            Alarm.createAlarm(
                realmInTransaction,
                hourAlarm,
                minuteAlarm,
                realmDayOfTheWeekList,
                realmSongList,
                shouldResumePlaying,
                shouldVibrate,
                hourBedtimeSleep,
                minuteBedtimeSleep,
                useDefaultRingtone
            )
        }
    }

    fun editAlarmAsync(
        id: Int,
        realm: Realm,
        isEnabled: Boolean = false,
        hourAlarm: Int,
        minuteAlarm: Int,
        daysList: MutableList<EnumDayOfWeek>,
        songList: MutableList<AudioFile>,
        shouldResumePlaying: Boolean = false,
        shouldVibrate: Boolean = false,
        secondsPlayed: Int = 0,
        hourBedtimeSleep: Int? = null,
        minuteBedtimeSleep: Int? = null,
        useDefaultRingtone: Boolean = false
    ) {
        realm.executeTransaction { realmInTransaction ->
            val (realmDayOfTheWeekList, realmSongList) = convertSelectedDaysAndSongsToRealmList(
                daysList,
                realmInTransaction,
                songList
            )

            val alarm = realmInTransaction.where(Alarm::class.java).equalTo(Alarm.FIELD_ID, id).findFirst()
            alarm?.let {
                alarm.hourAlarm = hourAlarm
                alarm.minuteAlarm = minuteAlarm
                alarm.daysList = realmDayOfTheWeekList
                alarm.songsList = realmSongList
                if (songList.isNotEmpty()) alarm.currentlySelectedPath = songList.random()?.path
                alarm.shouldResumePlaying = shouldResumePlaying
                alarm.secondsPlayed = secondsPlayed
                alarm.shouldVibrate = shouldVibrate
                alarm.isEnabled = isEnabled
                alarm.hourBedtimeSleep = hourBedtimeSleep
                alarm.minuteBedtimeSleep = minuteBedtimeSleep
                alarm.useDefaultRingtone = useDefaultRingtone
                if (isEnabled) {
                    MyAlarm.setAlarm(MyApplication.appContext, alarm)
                }
            }
        }
    }

    fun enableAlarm(alarmID: Int, isEnabled: Boolean, realm: Realm) {
        realm.executeTransaction {
            it.where(Alarm::class.java).equalTo(Alarm.FIELD_ID, alarmID).findFirst()?.let { alarm ->
                alarm.isEnabled = isEnabled
                if (alarm.songsList?.isNotEmpty() == true && !alarm.shouldResumePlaying) { //TODO Maybe add if user wants to change the song with every on/off
                    alarm.currentlySelectedPath = alarm.songsList?.random()?.path
                }
            }
        }
    }

    fun updateProgress(alarmID: Int, currentPosition: Int) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransactionAsync({
            it.where(Alarm::class.java).equalTo(Alarm.FIELD_ID, alarmID).findFirst()?.let { alarm ->
                alarm.secondsPlayed = currentPosition
            }
        }, { realm.close() }, { realm.close() })
    }

    fun nextRandomSong(alarmID: Int, path: String?) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransactionAsync({
            it.where(Alarm::class.java).equalTo(Alarm.FIELD_ID, alarmID).findFirst()?.let { alarm ->
                alarm.currentlySelectedPath = path
                alarm.secondsPlayed = 0
            }
        }, { realm.close() }, { realm.close() })
    }

    fun getAllAlarms(realm: Realm): RealmList<Alarm>? {
        return realm.where(AlarmList::class.java).findFirst()?.alarmList
    }

    fun getAlarm(realm: Realm, alarmID: Int): Alarm? {
        return realm.where(Alarm::class.java).equalTo(Alarm.FIELD_ID, alarmID).findFirst()
    }

    fun getBedtimeAlarm(realm: Realm): Alarm? {
        return realm.where(Alarm::class.java).isNotNull(Alarm.FIELD_BEDTIME_HOUR).findFirst()
    }

    fun deleteAlarmAsync(realm: Realm, id: Int) {
        realm.executeTransactionAsync { realmInTransaction ->
            val alarm = realmInTransaction.where(Alarm::class.java).equalTo(Alarm.FIELD_ID, id).findFirst()
            alarm?.deleteFromRealm()
        }
    }

    fun createDefaultBedtimeAlarm(realmInTransaction: Realm) {
        //Default ALL Days
        val selectedDays = mutableListOf<EnumDayOfWeek>()
        selectedDays.add(EnumDayOfWeek.MONDAY)
        selectedDays.add(EnumDayOfWeek.TUESDAY)
        selectedDays.add(EnumDayOfWeek.WEDNESDAY)
        selectedDays.add(EnumDayOfWeek.THURSDAY)
        selectedDays.add(EnumDayOfWeek.FRIDAY)
        selectedDays.add(EnumDayOfWeek.SATURDAY)
        selectedDays.add(EnumDayOfWeek.SUNDAY)

        //Default alarm tone, so it is shown in fragment that default is selected
        var alarmTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        if (alarmTone == null) {
            alarmTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            if (alarmTone == null) {
                alarmTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            }
        }
        val ringtoneAlarm = RingtoneManager.getRingtone(MyApplication.appContext, alarmTone)
        val defaultRingtone = AudioFile()
        defaultRingtone.name = ringtoneAlarm.getTitle(MyApplication.appContext)
        val songList = mutableListOf(defaultRingtone)

        val (realmDayOfTheWeekList, realmSongList) = convertSelectedDaysAndSongsToRealmList(
            selectedDays,
            realmInTransaction,
            songList
        )

        //Save
        Alarm.createBedtime(
            realmInTransaction,
            hourAlarm = 7,
            minuteAlarm = 0,
            daysList = realmDayOfTheWeekList,
            songList = realmSongList,
            shouldResumePlaying = true,
            shouldVibrate = true,
            hourBedtimeSleep = 23,
            minuteBedtimeSleep = 0,
            isDefaultRingtone = true,
            isEnabled = false
        )
    }

    fun updateBedtime(
        realm: Realm,
        hourSleep: Int,
        minuteSleep: Int,
        hour: Int,
        minute: Int,
        songList: MutableList<AudioFile>,
        shouldResumePlaying: Boolean,
        shouldVibrate: Boolean,
        defaultRingtone: Boolean,
        enumNotificationTime: EnumNotificationTime
    ) {
        realm.executeTransaction { realmInTransaction ->
            getBedtimeAlarm(realmInTransaction)?.let { bedtimeAlarm ->
                bedtimeAlarm.hourAlarm = hour
                bedtimeAlarm.minuteAlarm = minute
                bedtimeAlarm.hourBedtimeSleep = hourSleep
                bedtimeAlarm.minuteBedtimeSleep = minuteSleep
                bedtimeAlarm.shouldResumePlaying = shouldResumePlaying
                bedtimeAlarm.shouldVibrate = shouldVibrate
                bedtimeAlarm.useDefaultRingtone = defaultRingtone

                //Notify before triggering
                val notificationTime = realm.createObject(RealmNotificationTime::class.java)
                notificationTime.saveNotificationTime(enumNotificationTime)
                bedtimeAlarm.notificationTime = notificationTime

                val convertSongListToRealm = convertSongListToRealm(songList, realmInTransaction)
                if (songList.isNotEmpty()) bedtimeAlarm.currentlySelectedPath = convertSongListToRealm.random().path
                bedtimeAlarm.songsList = convertSongListToRealm

                if (bedtimeAlarm.isEnabled) {
                    MyAlarm.setAlarm(MyApplication.appContext, bedtimeAlarm)
                    MyNotification.enableNotification(MyApplication.appContext, bedtimeAlarm)
                }
            }
        }
    }

    /*
    Private
     */

    private fun convertSelectedDaysAndSongsToRealmList(
        daysList: MutableList<EnumDayOfWeek>,
        realmInTransaction: Realm,
        songList: MutableList<AudioFile>
    ): Pair<RealmList<RealmDayOfWeek>, RealmList<Song>> {
        //Create Realm objects
        val realmDayOfTheWeekList = convertDayListToRealm(daysList, realmInTransaction)
        val realmSongList = convertSongListToRealm(songList, realmInTransaction)
        return Pair(realmDayOfTheWeekList, realmSongList)
    }

    private fun convertSongListToRealm(
        songList: MutableList<AudioFile>,
        realmInTransaction: Realm
    ): RealmList<Song> {
        val realmSongList = RealmList<Song>()
        for (audio in songList) {
            realmSongList.add(
                Song.createSong(
                    realmInTransaction,
                    0,
                    audio.name,
                    audio.path,
                    audio.size,
                    audio.duration,
                    audio.bucketId,
                    audio.bucketName
                )
            )
        }
        return realmSongList
    }

    private fun convertDayListToRealm(
        daysList: MutableList<EnumDayOfWeek>,
        realmInTransaction: Realm
    ): RealmList<RealmDayOfWeek> {
        val realmDayOfTheWeekList = RealmList<RealmDayOfWeek>()
        for (day in daysList) {
            val realmDay = realmInTransaction.createObject(RealmDayOfWeek::class.java)
            realmDay.saveNameOfDay(day)
            realmDayOfTheWeekList.add(realmDay)
        }
        return realmDayOfTheWeekList
    }

}
