/*
 * Copyright 2016 Realm Inc.
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
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.concurrent.atomic.AtomicInteger

open class Alarm : RealmObject() {

    @PrimaryKey
    var id: Int = 0
    var bedtimeAlarm: String? = null
    var startTimeOfAlarm: String? = null
    var daysList: RealmList<RealmDayOfWeek>? = null
    var songsLocationList: RealmList<String>? = null
    var shouldResumePlaying: Boolean? = null
    var secondsPlayed: Int? = null
    var shouldVibrate: Boolean? = null
    var isEnabled: Boolean? = null

    companion object {
        const val FIELD_ID = "id"
        private val INTEGER_COUNTER = AtomicInteger(0)

        fun createAlarm(
            realm: Realm,
            bedtimeAlarm: String? = null,
            startTimeOfAlarm: String,
            daysList: RealmList<RealmDayOfWeek>,
            songsLocationList: RealmList<String>,
            shouldResumePlaying: Boolean,
            shouldVibrate: Boolean
        ) {
            val parent = realm.where(AlarmList::class.java).findFirst()
            val alarmList = parent!!.alarmList
            val alarm = realm.createObject(Alarm::class.java, increment())
            alarm.bedtimeAlarm = bedtimeAlarm
            alarm.startTimeOfAlarm = startTimeOfAlarm
            alarm.daysList = daysList
            alarm.songsLocationList = songsLocationList
            alarm.shouldResumePlaying = shouldResumePlaying
            alarm.secondsPlayed = 0
            alarm.shouldVibrate = shouldVibrate
            alarm.isEnabled = true
            alarmList?.add(alarm)
        }

        fun editAlarm(
            id: Int,
            realm: Realm,
            bedtimeAlarm: String?,
            startTimeOfAlarm: String,
            daysList: RealmList<RealmDayOfWeek>,
            songsLocationList: RealmList<String>,
            shouldResumePlaying: Boolean,
            secondsPlayed: Int,
            shouldVibrate: Boolean,
            isEnabled: Boolean
        ) {
            val alarm = realm.where(Alarm::class.java).equalTo(FIELD_ID, id).findFirst()
            alarm?.let {
                alarm.bedtimeAlarm = bedtimeAlarm
                alarm.startTimeOfAlarm = startTimeOfAlarm
                alarm.daysList = daysList
                alarm.songsLocationList = songsLocationList
                alarm.shouldResumePlaying = shouldResumePlaying
                alarm.secondsPlayed = secondsPlayed
                alarm.shouldVibrate = shouldVibrate
                alarm.isEnabled = isEnabled
            }
        }


        internal fun delete(realm: Realm, id: Int) {
            val alarm = realm.where(Alarm::class.java).equalTo(FIELD_ID, id).findFirst()
            alarm?.deleteFromRealm()
        }

        private fun increment(): Int {
            return INTEGER_COUNTER.getAndIncrement()
        }
    }
}