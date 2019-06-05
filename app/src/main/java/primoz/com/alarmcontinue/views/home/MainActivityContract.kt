package primoz.com.alarmcontinue.views.home

import primoz.com.alarmcontinue.views.BasePresenter
import primoz.com.alarmcontinue.views.BaseView

interface MainActivityContract {
    interface View : BaseView<Presenter> {
        fun showDate(date: String)
        fun showErrorNoAlarmsFound(shouldShow: Boolean = true)
        fun showEnabledBedtime(shouldShow: Boolean = false)
        fun showOtherAlarms()
    }

    interface Presenter : BasePresenter {
        fun loadAlarms()
        fun loadCurrentTime()
    }
}