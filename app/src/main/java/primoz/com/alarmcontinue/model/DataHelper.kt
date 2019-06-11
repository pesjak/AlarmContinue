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
        minuteBedtimeSleep: Int? = null
    ) {
        realm.executeTransactionAsync { realmInTransaction ->
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
                minuteBedtimeSleep
            )
        }
    }

    fun deleteAlarmAsync(realm: Realm, id: Int) {
        realm.executeTransactionAsync { realmInTransaction -> Alarm.delete(realmInTransaction, id) }
    }

    fun editAlarm(
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

            //Create Realm objects
            val realmDayOfTheWeekList = RealmList<RealmDayOfWeek>()
            for (day in daysList) {
                val realmDay = realmInTransaction.createObject(RealmDayOfWeek::class.java)
                realmDay.saveNameOfDay(day)
                realmDayOfTheWeekList.add(realmDay)
            }

            //Save object
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

            Alarm.editAlarm(
                id,
                realmInTransaction,
                isEnabled,
                hourAlarm,
                minuteAlarm,
                realmDayOfTheWeekList,
                realmSongList,
                shouldResumePlaying,
                shouldVibrate,
                secondsPlayed,
                hourBedtimeSleep,
                minuteBedtimeSleep
            )
        }
    }

    fun shouldEnableAlarm(alarm: Alarm, isEnabled: Boolean) {
        val realm = Realm.getDefaultInstance()
        realm.executeTransactionAsync({ alarm.isEnabled = isEnabled },
            { realm.close() }, //Success
            { realm.close() }  //Fail
        )
    }
}
