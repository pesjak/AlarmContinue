package primoz.com.alarmcontinue.extensions

import android.graphics.Color
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager

/**
 * Created by matejhacin on 18/06/2018.
 */

/**
 * This file contains functions which are supposed
 * to be global across the app. Make sure this doesn't
 * get too crowded as it's not good to have too many
 * of those.
 */

inline fun delay(millis: Long, crossinline func: () -> Unit) {
    Handler().postDelayed({
        run {
            func()
        }
    }, millis)
}

// Enables transparent navigation and status bar and works on lower APIs
fun setTransparentStatusNavigationBar(window: Window) {
    window.decorView.systemUiVisibility =
        (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
    setWindowFlag(
        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
        false,
        window
    )
    window.statusBarColor = Color.TRANSPARENT
    window.navigationBarColor = Color.TRANSPARENT
}

private fun setWindowFlag(bits: Int, on: Boolean, window: Window) {
    val winParams = window.attributes
    if (on) {
        winParams.flags = winParams.flags or bits
    } else {
        winParams.flags = winParams.flags and bits.inv()
    }
    window.attributes = winParams
}

