package primoz.com.alarmcontinue.views.home

import android.app.Activity
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        realm = Realm.getDefaultInstance()

        initToolbar()
        initRecyclerView()
        initOnClickListeners()

        MainActivityPresenter(this)
        mPresenter.loadCurrentTime()
        mPresenter.loadAlarms(realm)
    }

    private fun initToolbar() {
        nestedScrollView.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (v?.canScrollVertically(-1) == true) {
                toolbar.elevation = 8F
                line0.visibility = View.GONE
            } else {
                toolbar.elevation = 0F
                line0.visibility = View.VISIBLE
            }
        }
    }

    private fun initOnClickListeners() {
        switchBedtime.setOnCheckedChangeListener { buttonView, isChecked -> mPresenter.enableBedtime(realm, isChecked) }
        textBedtime.setOnClickListener {
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

    override fun getActivity(): Activity {
        return this
    }

    override fun showAlarms(alarmList: RealmList<Alarm>) {
        adapter = MyAlarmsRecyclerViewAdapter(alarmList, this)
        rvAlarms.adapter = adapter
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
    }
}
