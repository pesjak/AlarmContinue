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
import primoz.com.alarmcontinue.MyApplication
import primoz.com.alarmcontinue.views.alarm.broadcast.MyAlarm

open class Alarm : RealmObject() {

    @PrimaryKey
    var id: Int = 0
    var hourAlarm: Int? = null
    var minuteAlarm: Int? = null
    var daysList: RealmList<RealmDayOfWeek>? = null
    var songsList: RealmList<Song>? = null
    var shouldResumePlaying: Boolean = false
    var secondsPlayed: Int = 0
    var shouldVibrate: Boolean = false
    var isEnabled: Boolean = false
    var hourBedtimeSleep: Int? = null
    var minuteBedtimeSleep: Int? = null
    var currentlySelectedPath: String? = null
    var useDefaultRingtone: Boolean = false

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
            minuteBedtimeSleep: Int? = null,
            isDefaultRingtone: Boolean = false
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
            alarm.isEnabled = true
            alarm.hourBedtimeSleep = hourBedtimeSleep
            alarm.minuteBedtimeSleep = minuteBedtimeSleep
            alarm.useDefaultRingtone = isDefaultRingtone
            alarmList?.add(alarm)

            MyAlarm.setAlarm(MyApplication.appContext, alarm)
        }

        private fun getNextID(realm: Realm): Int {
            return try {
                val number = realm.where(Alarm::class.java).max(FIELD_ID)
                when {
                    number != null -> number.toInt() + 1
                    else -> 0
                }
            } catch (e: ArrayIndexOutOfBoundsException) {
                0
            }
        }
    }
}