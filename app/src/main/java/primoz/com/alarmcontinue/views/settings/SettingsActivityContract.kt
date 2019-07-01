package primoz.com.alarmcontinue.views.settings

import primoz.com.alarmcontinue.views.BaseView

interface SettingsActivityContract {
    interface View : BaseView<Presenter> {
    }

    interface Presenter {
        fun loadEmail()
        fun loadAcknowledgments()
    }
}