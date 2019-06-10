package primoz.com.alarmcontinue.extensions

fun String.getFirst3Letters(): String {
    return this.substring(0, 3).toLowerCase().capitalize()
}
