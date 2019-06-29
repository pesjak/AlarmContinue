package primoz.com.alarmcontinue.model

import io.realm.RealmObject
import primoz.com.alarmcontinue.enums.EnumNotificationTime

open class RealmNotificationTime : RealmObject() {
    private lateinit var notificationTime: String

    fun saveNotificationTime(dayEnum: EnumNotificationTime) {
        this.notificationTime = dayEnum.name
    }

    val notificationTimeString: EnumNotificationTime
        get() = EnumNotificationTime.valueOf(notificationTime)

}