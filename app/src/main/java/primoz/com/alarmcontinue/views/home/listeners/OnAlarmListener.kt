package primoz.com.alarmcontinue.views.home.listeners

import primoz.com.alarmcontinue.model.Alarm

interface OnAlarmListener {
    fun onAlarmClicked(alarm: Alarm)
    fun onAlarmEnable(alarm: Alarm, shouldEnable: Boolean)
}
