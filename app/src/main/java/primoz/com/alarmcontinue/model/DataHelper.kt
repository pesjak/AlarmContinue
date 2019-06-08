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

object DataHelper {

    fun addAlarmAsync(
        realm: Realm,
        bedtimeAlarm: String? = null,
        startTimeOfAlarm: String,
        daysList: ArrayList<EnumDayOfWeek>,
        songsLocationList: ArrayList<String>,
        shouldResumePlaying: Boolean = false,
        shouldVibrate: Boolean = false
    ) {
        realm.executeTransactionAsync { realmInTransaction ->
            var monday = realmInTransaction.createObject(RealmDayOfWeek::class.java)
            monday.saveNameOfDay(EnumDayOfWeek.MONDAY)
            var wednesday =  realmInTransaction.createObject(RealmDayOfWeek::class.java)
            monday.saveNameOfDay(EnumDayOfWeek.WEDNESDAY)
            val realmDayOfTheWeekList = RealmList(monday,wednesday)
            val realmSongLocationList = RealmList("aslgkhaskl//alksgh.com","c://sakh./")

            Alarm.createAlarm(
                realmInTransaction,
                bedtimeAlarm,
                startTimeOfAlarm,
                realmDayOfTheWeekList,
                realmSongLocationList,
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
        songsLocationList: RealmList<String>,
        shouldResumePlaying: Boolean = false,
        secondsPlayed: Int = 0,
        shouldVibrate: Boolean = false,
        isEnabled: Boolean = false
    ) {
        realm.executeTransactionAsync { realmInTransaction ->
            Alarm.editAlarm(
                id,
                realmInTransaction,
                bedtimeAlarm,
                startTimeOfAlarm,
                daysList,
                songsLocationList,
                shouldResumePlaying,
                secondsPlayed,
                shouldVibrate,
                isEnabled
            )
        }
    }
}
