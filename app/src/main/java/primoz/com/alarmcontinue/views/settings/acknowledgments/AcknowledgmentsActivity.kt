package primoz.com.alarmcontinue.views.settings.acknowledgments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_acknowledgments.*
import primoz.com.alarmcontinue.R
import primoz.com.alarmcontinue.views.BaseActivity

class AcknowledgmentsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acknowledgments)

        initAcknowledgments()
    }

    private fun initAcknowledgments() {
        acknowledgmentsGroup.addTitle(getString(R.string.acknowledgements))
        acknowledgmentsGroup.addAcknowledgment("Some acknowledgment", "Apache 2.0 License", "Copyright @ 2013 Google Inc.")
        acknowledgmentsGroup.addAcknowledgment("Some acknowledgment", "Apache 2.0 License", "Copyright @ 2013 Google Inc.")
        acknowledgmentsGroup.addAcknowledgment("Some acknowledgment", "Apache 2.0 License", "Copyright @ 2013 Google Inc.")
        acknowledgmentsGroup.addAcknowledgment("Some acknowledgment", "Apache 2.0 License", "Copyright @ 2013 Google Inc.")
        acknowledgmentsGroup.addAcknowledgment("Some acknowledgment", "Apache 2.0 License", "Copyright @ 2013 Google Inc.")
        acknowledgmentsGroup.addTitle(getString(R.string.licenses))
        acknowledgmentsGroup.addAcknowledgment("Apache 2.0 License", getString(R.string.legal_apache))
    }

    companion object {

        fun getIntent(activity: Activity): Intent {
            val intent = Intent(activity, AcknowledgmentsActivity::class.java)
            return intent
        }

    }

}
