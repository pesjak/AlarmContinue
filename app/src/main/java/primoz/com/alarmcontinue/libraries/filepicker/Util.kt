package primoz.com.alarmcontinue.libraries.filepicker

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.DisplayMetrics
import android.view.WindowManager

object Util {
    fun detectIntent(ctx: Context, intent: Intent): Boolean {
        val packageManager = ctx.packageManager
        val list = packageManager.queryIntentActivities(
            intent, PackageManager.MATCH_DEFAULT_ONLY
        )
        return list.size > 0
    }

    fun getDurationString(duration: Long): String {
        //        long days = duration / (1000 * 60 * 60 * 24);
        val hours = duration % (1000 * 60 * 60 * 24) / (1000 * 60 * 60)
        val minutes = duration % (1000 * 60 * 60) / (1000 * 60)
        val seconds = duration % (1000 * 60) / 1000

        val hourStr = if (hours < 10) "0$hours" else hours.toString() + ""
        val minuteStr = if (minutes < 10) "0$minutes" else minutes.toString() + ""
        val secondStr = if (seconds < 10) "0$seconds" else seconds.toString() + ""

        return if (hours != 0L) {
            "$hourStr:$minuteStr:$secondStr"
        } else {
            "$minuteStr:$secondStr"
        }
    }

    fun getScreenWidth(ctx: Context): Int {
        val wm = ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        wm.defaultDisplay.getMetrics(dm)
        return dm.widthPixels
    }

    fun getScreenHeight(ctx: Context): Int {
        val wm = ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        wm.defaultDisplay.getMetrics(dm)
        return dm.heightPixels
    }

    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * Extract the file name in a URL
     * /storage/emulated/legacy/Download/sample.pptx = sample.pptx
     *
     * @param url String of a URL
     * @return the file name of URL with suffix
     */
    fun extractFileNameWithSuffix(url: String): String {
        return url.substring(url.lastIndexOf("/") + 1)
    }

    /**
     * Extract the file name in a URL
     * /storage/emulated/legacy/Download/sample.pptx = sample
     *
     * @param url String of a URL
     * @return the file name of URL without suffix
     */
    fun extractFileNameWithoutSuffix(url: String): String {
        try {
            return url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."))
        } catch (e: StringIndexOutOfBoundsException) {
            e.printStackTrace()
            return ""
        }

    }

    /**
     * Extract the path in a URL
     * /storage/emulated/legacy/Download/sample.pptx = /storage/emulated/legacy/Download/
     *
     * @param url String of a URL
     * @return the path of URL with the file separator
     */
    fun extractPathWithSeparator(url: String): String {
        return url.substring(0, url.lastIndexOf("/") + 1)
    }

    /**
     * Extract the path in a URL
     * /storage/emulated/legacy/Download/sample.pptx = /storage/emulated/legacy/Download
     *
     * @param url String of a URL
     * @return the path of URL without the file separator
     */
    fun extractPathWithoutSeparator(url: String): String {
        return url.substring(0, url.lastIndexOf("/"))
    }

    /**
     * Extract the suffix in a URL
     * /storage/emulated/legacy/Download/sample.pptx = pptx
     *
     * @param url String of a URL
     * @return the suffix of URL
     */
    fun extractFileSuffix(url: String): String {
        return if (url.contains(".")) {
            url.substring(url.lastIndexOf(".") + 1)
        } else {
            ""
        }
    }
}
