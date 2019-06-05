package primoz.com.alarmcontinue.views

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment

interface BaseView<T> {

    fun setPresenter(presenter: T)

    fun showToast(message: String) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun getContext(): Context? {
        if (this is Fragment) return this.context
        if (this is Activity) return this.baseContext
        return (this as View).context
    }
}
