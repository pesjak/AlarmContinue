package primoz.com.alarmcontinue.views.home

import io.realm.Realm
import primoz.com.alarmcontinue.model.Alarm
import primoz.com.alarmcontinue.model.DataHelper
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivityPresenter(private val view: MainActivityContract.View) : MainActivityContract.Presenter {

    override fun enableAlarm(realm: Realm, alarm: Alarm, shouldEnable: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun enableBedtime(realm: Realm, shouldEnable: Boolean) {

    }

    override fun showAddNewAlarm(realm: Realm) {
        DataHelper.addAlarmAsync(
            realm,
            null,
            "10:00",
            ArrayList(),
            ArrayList()
        )
    }

    override fun showEditAlarmScreen(alarm: Alarm) {
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