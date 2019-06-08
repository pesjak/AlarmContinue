package primoz.com.alarmcontinue.extensions

import android.animation.Animator
import android.view.ViewPropertyAnimator

/**
 * Executes immediately if animation has already ended, otherwise waits to finish
 */
fun ViewPropertyAnimator.onAnimationEnd(func: (() -> Unit)) {
    setListener(object : Animator.AnimatorListener {
        override fun onAnimationCancel(p0: Animator?) {}
        override fun onAnimationRepeat(p0: Animator?) {}
        override fun onAnimationStart(p0: Animator?) {}
        override fun onAnimationEnd(p0: Animator?) {
            func()
        }
    })
}