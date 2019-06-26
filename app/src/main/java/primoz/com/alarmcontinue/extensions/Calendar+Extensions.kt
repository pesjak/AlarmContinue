package primoz.com.alarmcontinue.extensions

import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

fun Calendar.getDateDiff(next: Calendar): Long {
    val end = next.timeInMillis
    val start = timeInMillis
    return TimeUnit.MILLISECONDS.toDays(abs(end - start))
}
