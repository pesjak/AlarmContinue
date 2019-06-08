package primoz.com.alarmcontinue.extensions

import android.animation.Animator
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/*
Animations
 */

fun View.fadeIn(millis: Long, func: (() -> Unit)? = null) {
    animate().alpha(1.0f).setDuration(millis).setListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {}
        override fun onAnimationCancel(animation: Animator?) {}
        override fun onAnimationStart(animation: Animator?) {}
        override fun onAnimationEnd(animation: Animator?) {
            func?.invoke()
        }
    }).start()
}

fun View.fadeOut(millis: Long, func: (() -> Unit)? = null) {
    animate().alpha(0.0f).setDuration(millis).setListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {}
        override fun onAnimationCancel(animation: Animator?) {}
        override fun onAnimationStart(animation: Animator?) {}
        override fun onAnimationEnd(animation: Animator?) {
            func?.invoke()
        }
    }).start()
}

fun View.scaleToNothing(newAlpha: Float, millis: Long, func: (() -> Unit)? = null) {
    animate().setDuration(millis).alpha(newAlpha)
        .scaleX(0f)
        .scaleY(0f)
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                func?.invoke()
            }
        }).start()
}

fun View.scaleToNormal(newAlpha: Float, millis: Long, func: (() -> Unit)? = null) {
    animate().setDuration(millis)
        .alpha(newAlpha)
        .scaleX(1f)
        .scaleY(1f)
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                func?.invoke()
            }
        }).start()
}

fun View.hideKeyboardIfPossible() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(
        windowToken,
        0
    )
}

fun View.showKeyboardIfPossible() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.toggleSoftInput(
        InputMethodManager.SHOW_FORCED,
        InputMethodManager.HIDE_IMPLICIT_ONLY
    )
}