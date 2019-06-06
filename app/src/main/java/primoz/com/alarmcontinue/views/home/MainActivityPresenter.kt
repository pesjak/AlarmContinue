package primoz.com.alarmcontinue.views.home

import primoz.com.alarmcontinue.model.Alarm
import java.text.SimpleDateFormat
import java.util.*

class MainActivityPresenter(private val view: MainActivityContract.View) : MainActivityContract.Presenter {

    override fun showEditAlarmScreen(alarm: Alarm) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun enableAlarm(alarm: Alarm, shouldEnable: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadAlarms() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadCurrentTime() {

        view.showDate(SimpleDateFormat("EEE, MMMM d", Locale.getDefault()).format(Date()))
    }

    init {
        view.setPresenter(this)
    }

}