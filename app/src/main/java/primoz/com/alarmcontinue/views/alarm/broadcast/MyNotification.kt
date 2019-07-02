package primoz.com.alarmcontinue.views.alarm.broadcast

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import primoz.com.alarmcontinue.R
import primoz.com.alarmcontinue.enums.EnumNotificationTime
import primoz.com.alarmcontinue.model.Alarm
import primoz.com.alarmcontinue.views.home.MainActivity
import java.util.*

class MyNotification : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        //Show bedtime notification reminder
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmID = intent.extras?.getInt(ARG_NOTIFICATION_ALARM_ID)!!
        val notification = intent.extras?.getParcelable<Notification>(ARG_NOTIFICATION)
        val REQUEST_CODE_ID = alarmID * MULTIPLY_FOR_REQUEST_CODE_NOTIFICATION

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT)

            // Configure the notification channel.
            notificationChannel.description = "Alarm Continue - BedtimeNotification channel"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = context.getColor(R.color.colorAccent)
            notificationChannel.vibrationPattern = longArrayOf(0, 100, 0, 100, 0, 100)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(REQUEST_CODE_ID, notification)
    }

    companion object {

        private const val ARG_NOTIFICATION_ALARM_ID = "AlarmNotificationID"
        private const val ARG_NOTIFICATION = "AlarmNotification"
        private const val MULTIPLY_FOR_REQUEST_CODE_NOTIFICATION = 50

        var NOTIFICATION_CHANNEL_ID = "AlarmContinueNotificationChannel"

        fun enableNotification(context: Context, alarm: Alarm) {
            if (alarm.notificationTime!!.notificationTimeString == EnumNotificationTime.NONE) {
                cancelNotification(context, alarm)
            } else {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(context, MyNotification::class.java)
                intent.putExtra(ARG_NOTIFICATION_ALARM_ID, alarm.id)
                intent.putExtra(
                    ARG_NOTIFICATION,
                    getNotification(context, alarm.notificationTime!!.notificationTimeString.realName)
                )
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    alarm.id * MULTIPLY_FOR_REQUEST_CODE_NOTIFICATION,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                val timeInMillisToRemove = getTimeInMillis(alarm.notificationTime!!.notificationTimeString)
                val notifyTime = getNextBedtimeAlarmCalendar(alarm.hourBedtimeSleep!!, alarm.minuteBedtimeSleep!!)
                notifyTime.timeInMillis -= timeInMillisToRemove

                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    notifyTime.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            }
        }

        fun cancelNotification(context: Context, alarm: Alarm) {
            val intent = Intent(context, MyNotification::class.java)
            val sender = PendingIntent.getBroadcast(context, alarm.id * MULTIPLY_FOR_REQUEST_CODE_NOTIFICATION, intent, 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(sender)
        }
        /*
        Private
         */

        private fun getNotification(context: Context, timeLeft: String): Notification {
            val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)

            val notifyIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val notifyPendingIntent = PendingIntent.getActivity(
                context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )

            notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_logo_test)
                .setTicker(context.getString(R.string.bedtime))
                .setContentTitle(context.getString(R.string.bedtime_reminder))
                .setContentText(context.getString(R.string.bedtime_description, timeLeft))
                .setContentIntent(notifyPendingIntent)
            return notificationBuilder.build()
        }

        private fun getTimeInMillis(notificationTimeString: EnumNotificationTime): Long {
            return when (notificationTimeString) {
                EnumNotificationTime.NONE -> 0
                EnumNotificationTime.MIN_10 -> 600000
                EnumNotificationTime.MIN_20 -> 1200000
                EnumNotificationTime.MIN_30 -> 1800000
                EnumNotificationTime.MIN_40 -> 2400000
                EnumNotificationTime.MIN_50 -> 3000000
                EnumNotificationTime.HOUR_1 -> 3600000
                EnumNotificationTime.HOUR_2 -> 7200000
                EnumNotificationTime.HOUR_3 -> 10800000
            }
        }

        private fun getNextBedtimeAlarmCalendar(
            hour: Int,
            minute: Int
        ): Calendar {
            val now = Calendar.getInstance()
            //
            //
            //now.add(Calendar.SECOND, 3)
            // return now
            val next = Calendar.getInstance()

            next.set(Calendar.HOUR_OF_DAY, hour)
            next.set(Calendar.MINUTE, minute)
            next.set(Calendar.SECOND, 0)

            //Should set for some other day
            if (!now.after(next)) return next

            //Set the next day, because every day is selected
            next.add(Calendar.DATE, 1)
            return next
        }

    }
}
