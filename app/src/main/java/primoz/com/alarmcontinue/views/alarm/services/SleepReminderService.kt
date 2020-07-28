package primoz.com.alarmcontinue.views.alarm.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import io.realm.Realm
import io.realm.RealmList
import primoz.com.alarmcontinue.MyApplication
import primoz.com.alarmcontinue.R
import primoz.com.alarmcontinue.enums.EnumDayOfWeek
import primoz.com.alarmcontinue.model.Alarm
import primoz.com.alarmcontinue.model.DataHelper
import primoz.com.alarmcontinue.model.RealmDayOfWeek
import primoz.com.alarmcontinue.views.alarm.broadcast.MyAlarmDisable
import primoz.com.alarmcontinue.views.alarm.broadcast.MyAlarmDisable.Companion.ARG_ALARM_DISABLE_ID
import primoz.com.alarmcontinue.views.alarm.broadcast.MyAlarmDismiss
import primoz.com.alarmcontinue.views.alarm.broadcast.MyAlarmDismiss.Companion.ARG_ALARM_ID
import java.lang.ref.WeakReference
import java.util.*

class SleepReminderService : Service() {
    private var powerManager: PowerManager? = null
    private var receiver: ScreenReceiver? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        receiver = ScreenReceiver(this)
        refreshState()

        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_SCREEN_ON)
        registerReceiver(receiver, filter)
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        refreshState() //TODO needs FOREGROUND service
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * Refresh the state of the sleepy stuff. This will either show a notification if a notification
     * should be shown, or stop the service if it shouldn't.
     */
    fun refreshState() {
        if (powerManager?.isInteractive == true) {
            val nextAlarm = getSleepyAlarm()

            val snoozeIntent = Intent(this, MyAlarmDismiss::class.java)
            snoozeIntent.putExtra(ARG_ALARM_ID, nextAlarm?.id)
            val snoozePendingIntent = PendingIntent.getBroadcast(this, 0, snoozeIntent, 0)

            val disableIntent = Intent(this, MyAlarmDisable::class.java)
            disableIntent.putExtra(ARG_ALARM_DISABLE_ID, nextAlarm?.id)
            val disablePendingIntent = PendingIntent.getBroadcast(this, 0, disableIntent, 0)

            nextAlarm?.let {
                val builder: NotificationCompat.Builder
                builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    manager.createNotificationChannel(
                        NotificationChannel(
                            "sleepReminderAlarmContinue",
                            "Alarm continue",
                            NotificationManager.IMPORTANCE_DEFAULT
                        )
                    )
                    NotificationCompat.Builder(this, "sleepReminderAlarmContinue")
                } else
                    NotificationCompat.Builder(this)

                startForeground(
                    50, builder.setContentTitle("Alarm Continue")
                        .setContentText("Alarm will trigger soon.")
                        .setSmallIcon(R.drawable.ic_icon)
                        .setPriority(NotificationCompat.PRIORITY_LOW)
                        .setCategory(NotificationCompat.CATEGORY_SERVICE)
                        .addAction(R.drawable.ic_alarm_off_dark, "Turn OFF", disablePendingIntent)
                        .addAction(R.drawable.ic_close_dark, "Dismiss", snoozePendingIntent)
                        .build()
                )
                return
            }
        }

        stopForeground(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            stopSelf()
    }

    companion object {
        /**
         * To be called whenever an alarm is changed, might change, or when time might have
         * unexpectedly leaped forwards. This will start the service if there is a
         * [sleepy alarm](#getsleepyalarm) present.
         *
         * @param context       An active context instance.
         */
        fun refreshSleepTime(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.FOREGROUND_SERVICE
                ) != PackageManager.PERMISSION_GRANTED
            ) return
            val alarm = getSleepyAlarm()
            alarm?.let {
                val alarmCalendar = getNextAlarmCalendar(alarm.hourAlarm!!, alarm.minuteAlarm!!, alarm.daysList!!)
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = alarmCalendar.timeInMillis - 1800000L
                if (Calendar.getInstance().after(calendar)) {
                    ContextCompat.startForegroundService(
                        context,
                        Intent(MyApplication.appContext, SleepReminderService::class.java)
                    )
                }
            }
        }


        /**
         * Get a sleepy alarm. Well, get the next alarm that should trigger a sleep alert.
         *
         * @return              The next [AlarmData](../data/AlarmData) that should trigger a
         * sleep alert, or null if there isn't one.
         */
        fun getSleepyAlarm(): Alarm? {
            return DataHelper.getNextAlarm(Realm.getDefaultInstance())
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

    private class ScreenReceiver(service: SleepReminderService) : BroadcastReceiver() {
        private val serviceReference: WeakReference<SleepReminderService> = WeakReference(service)

        override fun onReceive(context: Context, intent: Intent) {
            val service = serviceReference.get()
            service?.refreshState()
        }
    }

}