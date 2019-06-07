package primoz.com.alarmcontinue.views.home

import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_bedtime.*
import primoz.com.alarmcontinue.R
import primoz.com.alarmcontinue.model.Alarm
import primoz.com.alarmcontinue.model.AlarmList
import primoz.com.alarmcontinue.views.BaseActivity

class MainActivity : BaseActivity(), MainActivityContract.View, OnAlarmListener {

    private lateinit var realm: Realm
    private lateinit var mPresenter: MainActivityContract.Presenter
    private var adapter: MyAlarmsRecyclerViewAdapter? = null

    /*
    LifeCycle
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        realm = Realm.getDefaultInstance()
        initRecyclerView()

        MainActivityPresenter(this)
        mPresenter.loadCurrentTime()

    }

    override fun onDestroy() {
        super.onDestroy()
        rvAlarms.adapter = null
        realm.close()
    }

    /*
    OnAlarmListener
     */

    override fun onAlarmClicked(alarm: Alarm) {
        mPresenter.showEditAlarmScreen(alarm)
    }

    override fun onAlarmEnable(alarm: Alarm, shouldEnable: Boolean) {
        mPresenter.enableAlarm(alarm, shouldEnable)
    }

    /*
    View
     */

    override fun showDate(date: String) {
        tvCurrentDate.text = date
    }

    override fun showEnabledBedtime(shouldEnable: Boolean) {
        switchBedtime.isChecked = shouldEnable
    }

    override fun setPresenter(presenter: MainActivityContract.Presenter) {
        mPresenter = presenter
    }

    /*
    Private
     */

    private fun initRecyclerView() {
        adapter = MyAlarmsRecyclerViewAdapter(realm.where(AlarmList::class.java).findFirst()!!.alarmList!!, this)
        rvAlarms.layoutManager = LinearLayoutManager(this)
        rvAlarms.adapter = adapter
        rvAlarms.setHasFixedSize(true)
        rvAlarms.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }
}
