package primoz.com.alarmcontinue.enums

enum class EnumNotificationTime(var realName: String) {
    NONE("None"),
    MIN_10("10 min"),
    MIN_20("20 min"),
    MIN_30("30 min"),
    MIN_40("40 min"),
    MIN_50("50 min"),
    HOUR_1("1 h"),
    HOUR_2("2 h"),
    HOUR_3("3 h");


    override fun toString(): String {
        return this.realName
    }
}