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
        bedtimeAlarm: String? = null,
        startTimeOfAlarm: String,
        daysList: ArrayList<EnumDayOfWeek>,
        songList: ArrayList<AudioFile>,
        shouldResumePlaying: Boolean = false,
        shouldVibrate: Boolean = false
    ) {
        realm.executeTransactionAsync { realmInTransaction ->
            var monday = realmInTransaction.createObject(RealmDayOfWeek::class.java)
            monday.saveNameOfDay(EnumDayOfWeek.MONDAY)
            var wednesday = realmInTransaction.createObject(RealmDayOfWeek::class.java)
            monday.saveNameOfDay(EnumDayOfWeek.WEDNESDAY)
            val realmDayOfTheWeekList = RealmList(monday, wednesday)

            val realmSongList = RealmList<Song>()
            for (audio in songList) {
                realmSongList.add(
                    Song.createSong(
                        realmInTransaction,
                        0,
                        audio.name,
                        audio.path,
                        audio.size,
                        audio.bucketId,
                        audio.bucketName
                    )
                )
            }

            Alarm.createAlarm(
                realmInTransaction,
                bedtimeAlarm,
                startTimeOfAlarm,
                realmDayOfTheWeekList,
                realmSongList,
                shouldResumePlaying,
                shouldVibrate
            )
        }
    }

    fun deleteAlarmAsync(realm: Realm, id: Int) {
        realm.executeTransactionAsync { realmInTransaction -> Alarm.delete(realmInTransaction, id) }
    }

    fun editAlarm(
        id: Int,
        realm: Realm,
        bedtimeAlarm: String? = null,
        startTimeOfAlarm: String,
        daysList: RealmList<RealmDayOfWeek>,
        songList: RealmList<AudioFile>,
        shouldResumePlaying: Boolean = false,
        secondsPlayed: Int = 0,
        shouldVibrate: Boolean = false,
        isEnabled: Boolean = false
    ) {
        realm.executeTransactionAsync { realmInTransaction ->
            val realmSongList = RealmList<Song>()
            for (audio in songList) {
                realmSongList.add(
                    Song.createSong(
                        realmInTransaction,
                        0,
                        audio.name,
                        audio.path,
                        audio.size,
                        audio.bucketId,
                        audio.bucketName
                    )
                )
            }
            Alarm.editAlarm(
                id,
                realmInTransaction,
                bedtimeAlarm,
                startTimeOfAlarm,
                daysList,
                realmSongList,
                shouldResumePlaying,
                secondsPlayed,
                shouldVibrate,
                isEnabled
            )
        }
    }
}
