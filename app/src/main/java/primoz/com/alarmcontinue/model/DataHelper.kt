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
import java.util.*

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

    //For New Alarms
    fun alreadySameAlarm(
        realm: Realm,
        hour: Int,
        minute: Int,
        selectedDays: MutableList<EnumDayOfWeek>
    ): Boolean {
        var disableAlarm = false
        val alarmList = getAllAlarms(realm)
        alarmList?.let {
            for (alarm in it) {
                val alarmHour = alarm.hourAlarm
                val alarmMinute = alarm.minuteAlarm
                if (hour == alarmHour && minute == alarmMinute) {
                    if (selectedDays.isEmpty()) { //Ugly but will handle if it is one time alarm
                        disableAlarm = true
                    }
                    alarm.daysList?.let { realmDaysList ->
                        val monday = realmDaysList.where().equalTo("nameOfDayEnum", EnumDayOfWeek.MONDAY.toString()).findFirst()
                        val tuesday = realmDaysList.where().equalTo("nameOfDayEnum", EnumDayOfWeek.TUESDAY.toString()).findFirst()
                        val wednesday =
                            realmDaysList.where().equalTo("nameOfDayEnum", EnumDayOfWeek.WEDNESDAY.toString()).findFirst()
                        val thursday =
                            realmDaysList.where().equalTo("nameOfDayEnum", EnumDayOfWeek.THURSDAY.toString()).findFirst()
                        val friday = realmDaysList.where().equalTo("nameOfDayEnum", EnumDayOfWeek.FRIDAY.toString()).findFirst()
                        val saturday =
                            realmDaysList.where().equalTo("nameOfDayEnum", EnumDayOfWeek.SATURDAY.toString()).findFirst()
                        val sunday = realmDaysList.where().equalTo("nameOfDayEnum", EnumDayOfWeek.SUNDAY.toString()).findFirst()

                        if (selectedDays.contains(monday?.nameOfDayString)
                            || selectedDays.contains(tuesday?.nameOfDayString)
                            || selectedDays.contains(wednesday?.nameOfDayString)
                            || selectedDays.contains(thursday?.nameOfDayString)
                            || selectedDays.contains(friday?.nameOfDayString)
                            || selectedDays.contains(saturday?.nameOfDayString)
                            || selectedDays.contains(sunday?.nameOfDayString)
                        ) {
                            disableAlarm = true
                        }
                    }
                }
            }
        }

        //Check bedtime
        val bedtimeAlarm = getBedtimeAlarm(realm)
        bedtimeAlarm?.let {
            if (hour == bedtimeAlarm.hourAlarm && minute == bedtimeAlarm.minuteAlarm) {
                disableAlarm = true
            }
        }

        return disableAlarm
    }

    //For updating alarm
    fun alreadySameAlarm(
        id: Int,
        realm: Realm,
        hour: Int,
        minute: Int,
        selectedDays: MutableList<EnumDayOfWeek>
    ): Boolean {
        val myAlarm = realm.where(Alarm::class.java).equalTo(Alarm.FIELD_ID, id).findFirst()
        var disableAlarm = false
        val alarmList = getAllAlarms(realm)
        alarmList?.let {
            for (alarm in it) {
                if (alarm.id == myAlarm?.id) continue

                val alarmHour = alarm.hourAlarm
                val alarmMinute = alarm.minuteAlarm
                if (hour == alarmHour && minute == alarmMinute) {
                    if (selectedDays.isEmpty()) { //Ugly but will handle if it is one time alarm
                        disableAlarm = true
                    }
                    alarm.daysList?.let { realmDaysList ->
                        val monday = realmDaysList.where().equalTo("nameOfDayEnum", EnumDayOfWeek.MONDAY.toString()).findFirst()
                        val tuesday = realmDaysList.where().equalTo("nameOfDayEnum", EnumDayOfWeek.TUESDAY.toString()).findFirst()
                        val wednesday =
                            realmDaysList.where().equalTo("nameOfDayEnum", EnumDayOfWeek.WEDNESDAY.toString()).findFirst()
                        val thursday =
                            realmDaysList.where().equalTo("nameOfDayEnum", EnumDayOfWeek.THURSDAY.toString()).findFirst()
                        val friday = realmDaysList.where().equalTo("nameOfDayEnum", EnumDayOfWeek.FRIDAY.toString()).findFirst()
                        val saturday =
                            realmDaysList.where().equalTo("nameOfDayEnum", EnumDayOfWeek.SATURDAY.toString()).findFirst()
                        val sunday = realmDaysList.where().equalTo("nameOfDayEnum", EnumDayOfWeek.SUNDAY.toString()).findFirst()

                        if (selectedDays.contains(monday?.nameOfDayString)
                            || selectedDays.contains(tuesday?.nameOfDayString)
                            || selectedDays.contains(wednesday?.nameOfDayString)
                            || selectedDays.contains(thursday?.nameOfDayString)
                            || selectedDays.contains(friday?.nameOfDayString)
                            || selectedDays.contains(saturday?.nameOfDayString)
                            || selectedDays.contains(sunday?.nameOfDayString)
                        ) {
                            disableAlarm = true
                        }
                    }
                }
            }
        }

        //Check bedtime
        val bedtimeAlarm = getBedtimeAlarm(realm)
        bedtimeAlarm?.let {
            if (hour == bedtimeAlarm.hourAlarm && minute == bedtimeAlarm.minuteAlarm) {
                disableAlarm = true
            }
        }

        return disableAlarm
    }

    //For Bedtime Alarm
    fun alreadySameAlarm(
        realm: Realm,
        hour: Int,
        minute: Int
    ): Boolean {
        var disableAlarm = false
        val alarmList = getAllAlarms(realm)
        alarmList?.let {
            for (alarm in it) {
                val alarmHour = alarm.hourAlarm
                val alarmMinute = alarm.minuteAlarm
                if (hour == alarmHour && minute == alarmMinute) {
                    disableAlarm = true
                }
            }
        }
        return disableAlarm
    }

    fun getNextAlarm(realm: Realm?): Alarm? {
        realm ?: return null
        val alarmList = getAllAlarms(realm)
        alarmList ?: return null
        var nextAlarm: Alarm? = null
        for (alarm in alarmList) {
            if (alarm.isEnabled) {
                if (nextAlarm == null) {
                    nextAlarm = alarm
                } else {
                    val currentAlarmCalendar = getNextAlarmCalendar(alarm.hourAlarm!!, alarm.minuteAlarm!!, alarm.daysList!!)
                    val nextAlarmCalendar =
                        getNextAlarmCalendar(nextAlarm.hourAlarm!!, nextAlarm.minuteAlarm!!, nextAlarm.daysList!!)
                    if (currentAlarmCalendar.before(nextAlarmCalendar)) nextAlarm = alarm
                }
            }
        }
        val bedtime = getBedtimeAlarm(realm)
        bedtime?.let {
            if (bedtime.isEnabled) {
                if (nextAlarm == null) {
                    nextAlarm = bedtime
                } else {
                    val currentAlarmCalendar = getNextAlarmCalendar(bedtime.hourAlarm!!, bedtime.minuteAlarm!!, bedtime.daysList!!)
                    val nextAlarmCalendar =
                        getNextAlarmCalendar(nextAlarm!!.hourAlarm!!, nextAlarm!!.minuteAlarm!!, nextAlarm!!.daysList!!)
                    if (currentAlarmCalendar.before(nextAlarmCalendar)) nextAlarm = bedtime
                }
            }
        }

        return nextAlarm
    }

    private fun getNextAlarmCalendar(
        hour: Int,
        minute: Int,
        realmDays: RealmList<RealmDayOfWeek>
    ): Calendar {
        val now = Calendar.getInstance()
        //now.add(Calendar.SECOND, 3)
        //return now
        val next = Calendar.getInstance()

        next.set(Calendar.HOUR_OF_DAY, hour)
        next.set(Calendar.MINUTE, minute)
        next.set(Calendar.SECOND, 0)

        //Should set for some other day
        if (!now.after(next)) return next

        //Set the next day, because every day is selected
        if (realmDays.size == 7) {
            next.add(Calendar.DATE, 1)
            return next
        }

        //Set next available day
        val monday = realmDays.where().equalTo("nameOfDayEnum", EnumDayOfWeek.MONDAY.toString()).findFirst()
        val tuesday = realmDays.where().equalTo("nameOfDayEnum", EnumDayOfWeek.TUESDAY.toString()).findFirst()
        val wednesday = realmDays.where().equalTo("nameOfDayEnum", EnumDayOfWeek.WEDNESDAY.toString()).findFirst()
        val thursday = realmDays.where().equalTo("nameOfDayEnum", EnumDayOfWeek.THURSDAY.toString()).findFirst()
        val friday = realmDays.where().equalTo("nameOfDayEnum", EnumDayOfWeek.FRIDAY.toString()).findFirst()
        val saturday = realmDays.where().equalTo("nameOfDayEnum", EnumDayOfWeek.SATURDAY.toString()).findFirst()
        val sunday = realmDays.where().equalTo("nameOfDayEnum", EnumDayOfWeek.SUNDAY.toString()).findFirst()

        val statusAllDayOfTheWeekList: MutableList<Boolean> = mutableListOf(
            monday != null,
            tuesday != null,
            wednesday != null,
            thursday != null,
            friday != null,
            saturday != null,
            sunday != null
        )

        var nextDay = next.get(Calendar.DAY_OF_WEEK) - 1 // index on 0-6, rather than the 1-7 returned by Calendar

        var i = 0
        while (i < 7 && !statusAllDayOfTheWeekList[nextDay]) {
            nextDay++
            nextDay %= 7
            i++
        }
        val nextDayToSet = nextDay + 2 //TODO Works but needs further testing
        next.set(Calendar.DAY_OF_WEEK, nextDayToSet) // + 1 = back to 1-7 range

        while (now.after(next)) next.add(Calendar.DATE, 7)
        return next
    }
}
