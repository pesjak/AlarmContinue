package primoz.com.alarmcontinue.views.settings

import android.content.Intent
import android.net.Uri
import primoz.com.alarmcontinue.R
import primoz.com.alarmcontinue.views.settings.acknowledgments.AcknowledgmentsActivity

class SettingsActivityPresenter(var view: SettingsActivityContract.View) : SettingsActivityContract.Presenter {

    init {
        view.setPresenter(this)
    }

    //TODO Different contact mail
    override fun loadEmail() {
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "alarmcontinue@gmail.com", null))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, view.getViewActivity().getString(R.string.app_name))
        view.getViewActivity().startActivity(emailIntent)
    }

    override fun loadAcknowledgments() {
        view.getViewActivity().startActivity(AcknowledgmentsActivity.getIntent(view.getViewActivity()))
    }

}