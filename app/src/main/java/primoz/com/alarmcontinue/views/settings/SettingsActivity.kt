package primoz.com.alarmcontinue.views.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_settings.*
import primoz.com.alarmcontinue.R
import primoz.com.alarmcontinue.views.BaseActivity
import primoz.com.alarmcontinue.views.home.adapters.MyAlarmsRecyclerViewAdapter

class SettingsActivity : BaseActivity(), SettingsActivityContract.View {

    private lateinit var realm: Realm
    private lateinit var mPresenter: SettingsActivityContract.Presenter
    private var adapter: MyAlarmsRecyclerViewAdapter? = null

    /*
    LifeCycle
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        realm = Realm.getDefaultInstance()

        initOnClickListener()
        initSettings()

        SettingsActivityPresenter(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    /*
    View
     */

    override fun getViewActivity(): Activity {
        return this
    }

    override fun setPresenter(presenter: SettingsActivityContract.Presenter) {
        mPresenter = presenter
    }

    /*
    Private
     */

    private fun initOnClickListener() {
        ivBack.setOnClickListener { onBackPressed() }
    }

    private fun initSettings() {
        settings.addTitle(getString(R.string.themes))
        settings.addSettings(
            R.drawable.ic_sun,
            getString(R.string.settings_light_theme),
            true
        ) { item, isChecked ->

        }

        settings.addTitle(getString(R.string.about))
        settings.addSettings(
            R.drawable.ic_person,
            getString(R.string.contact_me)
        ) { item ->
            mPresenter.loadEmail()
        }
        settings.addSettings(
            R.drawable.ic_star,
            getString(R.string.acknowledgements),
            true
        ) { item ->
            mPresenter.loadAcknowledgments()
        }
    }

    companion object {

        fun getIntent(activity: Activity): Intent {
            val intent = Intent(activity, SettingsActivity::class.java)
            return intent
        }

    }
}
