package primoz.com.alarmcontinue.views

import android.app.Activity
import android.widget.Toast

interface BaseView<T> {
    fun setPresenter(presenter: T)
    fun getViewActivity(): Activity
    fun showToast(message: String) {
        Toast.makeText(getViewActivity(), message, Toast.LENGTH_SHORT).show()
    }
}
