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


open class Alarm : RealmObject() {

    @PrimaryKey
    var id: Int = 0
    var hourAlarm: Int? = null
    var minuteAlarm: Int? = null
    var daysList: RealmList<RealmDayOfWeek>? = null
    var songsList: RealmList<Song>? = null
    var shouldResumePlaying: Boolean? = null
    var secondsPlayed: Int? = null
    var shouldVibrate: Boolean? = null
    var isEnabled: Boolean? = null
    var hourBedtimeSleep: Int? = null
    var minuteBedtimeSleep: Int? = null
    var currentlySelectedPath: String? = null

    companion object {
        const val FIELD_ID = "id"

        fun createAlarm(
            realm: Realm,
            hourAlarm: Int,
            minuteAlarm: Int,
            daysList: RealmList<RealmDayOfWeek>,
            songList: RealmList<Song>,
            shouldResumePlaying: Boolean,
            shouldVibrate: Boolean,
            hourBedtimeSleep: Int? = null,
            minuteBedtimeSleep: Int? = null
        ) {
            val parent = realm.where(AlarmList::class.java).findFirst()
            val alarmList = parent!!.alarmList
            val alarm = realm.createObject(Alarm::class.java, getNextID(realm))
            alarm.hourAlarm = hourAlarm
            alarm.minuteAlarm = minuteAlarm
            alarm.daysList = daysList
            alarm.songsList = songList
            if (songList.isNotEmpty()) {
                alarm.currentlySelectedPath = songList.random()?.path
            }
            alarm.shouldResumePlaying = shouldResumePlaying
            alarm.secondsPlayed = 0
            alarm.shouldVibrate = shouldVibrate
            alarm.isEnabled = false
            alarm.hourBedtimeSleep = hourBedtimeSleep
            alarm.minuteBedtimeSleep = minuteBedtimeSleep
            alarmList?.add(alarm)
        }

        fun editAlarm(
            id: Int,
            realm: Realm,
            isEnabled: Boolean,
            hourAlarm: Int,
            minuteAlarm: Int,
            daysList: RealmList<RealmDayOfWeek>,
            songList: RealmList<Song>,
            shouldResumePlaying: Boolean,
            shouldVibrate: Boolean,
            secondsPlayed: Int,
            hourBedtimeSleep: Int? = null,
            minuteBedtimeSleep: Int? = null
        ) {
            /*
            val alarm = realm.where(Alarm::class.java).equalTo(FIELD_ID, id).findFirst()
            alarm?.let {
                alarm.bedtimeAlarm = bedtimeAlarm
                alarm.startTimeOfAlarm = startTimeOfAlarm
                alarm.daysList = daysList
                alarm.songsList = songList
                alarm.shouldResumePlaying = shouldResumePlaying
                alarm.secondsPlayed = secondsPlayed
                alarm.shouldVibrate = shouldVibrate
                alarm.isEnabled = isEnabled
            }
            */
        }


        internal fun delete(realm: Realm, id: Int) {
            val alarm = realm.where(Alarm::class.java).equalTo(FIELD_ID, id).findFirst()
            alarm?.deleteFromRealm()
        }

        private fun getNextID(realm: Realm): Int {
            return try {
                val number = realm.where(Alarm::class.java).max(FIELD_ID)
                if (number != null) {
                    number.toInt() + 1
                } else {
                    0
                }
            } catch (e: ArrayIndexOutOfBoundsException) {
                0
            }
        }
    }
}