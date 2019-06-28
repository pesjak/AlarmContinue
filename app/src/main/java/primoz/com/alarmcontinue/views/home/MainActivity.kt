package primoz.com.alarmcontinue.views.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.transition.TransitionManager
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm
import io.realm.RealmList
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_add_alarm.*
import kotlinx.android.synthetic.main.item_bedtime.*
import primoz.com.alarmcontinue.R
import primoz.com.alarmcontinue.model.Alarm
import primoz.com.alarmcontinue.views.BaseActivity
import primoz.com.alarmcontinue.views.home.adapters.MyAlarmsRecyclerViewAdapter
import primoz.com.alarmcontinue.views.home.listeners.OnAlarmListener

class MainActivity : BaseActivity(), MainActivityContract.View, OnAlarmListener {

    private lateinit var realm: Realm
    private lateinit var mPresenter: MainActivityContract.Presenter
    private var adapter: MyAlarmsRecyclerViewAdapter? = null

    /*
    LifeCycle
     */

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ARG_BEDTIME_SCREEN_REQUEST_CODE) {
            mPresenter.loadBedtime(realm)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        realm = Realm.getDefaultInstance()

        initToolbar()
        initRecyclerView()
        initOnClickListeners()

        MainActivityPresenter(this)
        mPresenter.loadBedtime(realm)
        mPresenter.loadAlarms(realm)
    }

    private fun initToolbar() {
        nestedScrollView.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (v?.canScrollVertically(-1) == true) {
                toolbar.elevation = 8F
            } else {
                toolbar.elevation = 0F
            }
        }
    }

    private fun initOnClickListeners() {
        switchBedtime.setOnCheckedChangeListener { buttonView, isChecked ->
            mPresenter.enableBedtime(realm, isChecked)
        }
        bedtimeContainer.setOnClickListener {
            mPresenter.showBedtimeAlarmScreen()
        }
        ivAddAlarm.setOnClickListener {
            mPresenter.showAddNewAlarmScreen()
        }
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
        mPresenter.enableAlarm(realm, alarm, shouldEnable)
    }

    /*
    View
     */

    override fun showEnabledBedtime(shouldEnable: Boolean) {
        switchBedtime.isChecked = shouldEnable
    }

    override fun setPresenter(presenter: MainActivityContract.Presenter) {
        mPresenter = presenter
    }

    override fun getViewActivity(): Activity {
        return this
    }

    override fun showEmptyState(shouldShow: Boolean) {
        errorEmptyState.visibility = if (shouldShow) View.VISIBLE else View.GONE
    }

    override fun showAlarms(alarmList: RealmList<Alarm>) {
        adapter = MyAlarmsRecyclerViewAdapter(alarmList, this)
        rvAlarms.adapter = adapter
    }

    override fun showBedtimeClocks(shouldShow: Boolean) {
        TransitionManager.beginDelayedTransition(container)
        timeClockViewGroup.visibility = if (shouldShow) View.VISIBLE else View.GONE
    }

    override fun updateBedtime(bedtime: Alarm, shouldEnable: Boolean) {
        switchBedtime.alpha = if (shouldEnable) 1f else 0.5f
        switchBedtime.isChecked = shouldEnable
        textBedtime.alpha = if (shouldEnable) 1f else 0.2f

        showBedtimeClocks(shouldEnable)

        tvBedTime.text = getString(R.string.hour_minutes, bedtime.hourBedtimeSleep, bedtime.minuteBedtimeSleep)
        tvWakeTime.text = getString(R.string.hour_minutes, bedtime.hourAlarm, bedtime.minuteAlarm)
    }

    /*
    Private
     */

    private fun initRecyclerView() {
        rvAlarms.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(baseContext, R.drawable.divider)?.let {
            dividerItemDecoration.setDrawable(it)
        }
        rvAlarms.addItemDecoration(dividerItemDecoration)
        rvAlarms.itemAnimator = null
    }

    companion object {
        var ARG_BEDTIME_SCREEN_REQUEST_CODE = 100
    }
}
