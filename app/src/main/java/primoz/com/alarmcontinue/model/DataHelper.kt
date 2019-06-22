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

import io.realm.Realm
import io.realm.RealmList
import primoz.com.alarmcontinue.enums.EnumDayOfWeek
import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.AudioFile

object DataHelper {

    fun addAlarmAsync(
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
        realm.executeTransactionAsync { realmInTransaction ->
            val (realmDayOfTheWeekList, realmSongList) = convertSelectedDaysAndSongsToRealmList(
                daysList,
                realmInTransaction,
                songList
            )

            //Save object
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
        minuteBedtimeSleep: Int? = null
    ) {
        realm.executeTransactionAsync { realmInTransaction ->
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
            }
        }
    }

    fun shouldEnableAlarm(alarmID: Int, isEnabled: Boolean, realm: Realm) {
        realm.executeTransactionAsync {
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

    fun getAlarm(realm: Realm, alarmID: Int): Alarm? {
        return realm.where(Alarm::class.java).equalTo(Alarm.FIELD_ID, alarmID).findFirst()
    }

    fun deleteAlarmAsync(realm: Realm, id: Int) {
        realm.executeTransactionAsync { realmInTransaction ->
            val alarm = realmInTransaction.where(Alarm::class.java).equalTo(Alarm.FIELD_ID, id).findFirst()
            alarm?.deleteFromRealm()
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
        val realmDayOfTheWeekList = RealmList<RealmDayOfWeek>()
        for (day in daysList) {
            val realmDay = realmInTransaction.createObject(RealmDayOfWeek::class.java)
            realmDay.saveNameOfDay(day)
            realmDayOfTheWeekList.add(realmDay)
        }

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
        return Pair(realmDayOfTheWeekList, realmSongList)
    }
}
