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

object DataHelper {

    fun addAlarmAsync(
        realm: Realm,
        bedtimeAlarm: RealmString? = null,
        startTimeOfAlarm: RealmString,
        daysList: RealmList<RealmDayOfWeek>,
        songsLocationList: RealmList<RealmString>,
        shouldResumePlaying: Boolean = false,
        shouldVibrate: Boolean = false
    ) {
        realm.executeTransactionAsync { realmInTransaction ->
            Alarm.createAlarm(
                realmInTransaction,
                bedtimeAlarm,
                startTimeOfAlarm,
                daysList,
                songsLocationList,
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
        bedtimeAlarm: RealmString? = null,
        startTimeOfAlarm: RealmString,
        daysList: RealmList<RealmDayOfWeek>,
        songsLocationList: RealmList<RealmString>,
        shouldResumePlaying: Boolean = false,
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
                shouldVibrate,
                isEnabled
            )
        }
    }
}
