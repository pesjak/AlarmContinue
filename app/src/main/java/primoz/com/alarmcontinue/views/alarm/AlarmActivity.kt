package primoz.com.alarmcontinue.views.alarm

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_alarm.*
import primoz.com.alarmcontinue.R
import primoz.com.alarmcontinue.enums.AlarmType
import primoz.com.alarmcontinue.extensions.inTransaction
import primoz.com.alarmcontinue.views.BaseActivity
import primoz.com.alarmcontinue.views.alarm.fragments.bedtime.BedtimeAlarmFragment
import primoz.com.alarmcontinue.views.alarm.fragments.editAlarm.EditAlarmFragment
import primoz.com.alarmcontinue.views.alarm.fragments.newAlarm.NewAlarmFragment

class AlarmActivity : BaseActivity() {

    private val alarmType: AlarmType
        get() {
            return intent.extras?.getSerializable(ARG_ALARM_TYPE) as AlarmType
        }

    private val alarmID: Int
        get() {
            return intent.extras?.getInt(ARG_ALARM_ID) ?: 0
        }

    private val newAlarmFragment: NewAlarmFragment by lazy {
        NewAlarmFragment()
    }
    private val editAlarmFragment: EditAlarmFragment by lazy {
        EditAlarmFragment.getInstance(alarmID)
    }
    private val bedtimeAlarmFragment: BedtimeAlarmFragment by lazy {
        BedtimeAlarmFragment()
    }

    /*
    LifeCycle
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        showInitialFragment()
    }

    /*
    Private
     */

    private fun showInitialFragment() {
        when (alarmType) {
            AlarmType.BEDTIME -> changeFragment(bedtimeAlarmFragment)
            AlarmType.EDIT_ALARM -> changeFragment(editAlarmFragment)
            AlarmType.NEW_ALARM -> changeFragment(newAlarmFragment)
        }
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager.inTransaction {
            replace(container.id, fragment)
        }
    }

    companion object {
        const val ARG_ALARM_TYPE = "ARG_ALARM_TYPE"
        const val ARG_ALARM_ID = "ARG_ALARM_ID"

        fun getIntent(activity: Activity, alarmType: AlarmType, alarmID: Int? = null): Intent {
            val intent = Intent(activity, AlarmActivity::class.java)
            intent.putExtra(ARG_ALARM_TYPE, alarmType)
            intent.putExtra(ARG_ALARM_ID, alarmID)
            return intent
        }
    }
}