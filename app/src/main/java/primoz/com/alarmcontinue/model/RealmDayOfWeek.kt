package primoz.com.alarmcontinue.model

import io.realm.RealmObject

open class RealmDayOfWeek : RealmObject() {
    private lateinit var nameOfDayEnum: String

    fun saveNameOfDay(dayEnum: EnumDayOfWeek) {
        this.nameOfDayEnum = dayEnum.toString()
    }

    val nameOfDayString: EnumDayOfWeek
        get() = EnumDayOfWeek.valueOf(nameOfDayEnum)

}

enum class EnumDayOfWeek {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY
}