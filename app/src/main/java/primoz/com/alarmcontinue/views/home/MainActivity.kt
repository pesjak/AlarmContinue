package primoz.com.alarmcontinue.views.home

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import primoz.com.alarmcontinue.R
import primoz.com.alarmcontinue.views.BaseActivity

class MainActivity : BaseActivity(), MainActivityContract.View {

    lateinit var mPresenter: MainActivityContract.Presenter

    override fun showDate(date: String) {
        tvCurrentDate.text = date
    }

    override fun showErrorNoAlarmsFound(shouldShow: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showEnabledBedtime(shouldShow: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showOtherAlarms() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setPresenter(presenter: MainActivityContract.Presenter) {
        mPresenter = presenter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MainActivityPresenter(this)
        mPresenter.loadCurrentTime()
    }
}
