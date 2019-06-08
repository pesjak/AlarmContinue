package primoz.com.alarmcontinue.extensions

import android.animation.Animator
import android.animation.ObjectAnimator

/**
 * Executes immediately if animation has already ended, otherwise waits to finish
 */
fun ObjectAnimator.onAnimationEnd(func: (() -> Unit)) {
    if (!isRunning) {
        func()
        return
    }

    addListener(object : Animator.AnimatorListener {
        override fun onAnimationCancel(p0: Animator?) {}
        override fun onAnimationRepeat(p0: Animator?) {}
        override fun onAnimationStart(p0: Animator?) {}
        override fun onAnimationEnd(p0: Animator?) {
            removeListener(this)
            func()
        }
    })
}