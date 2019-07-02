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
        acknowledgmentsGroup.addTitle(getString(R.string.libraries))
        acknowledgmentsGroup.addAcknowledgment(
            getString(R.string.realm),
            getString(R.string.license_name_apache),
            "Copyright (c) 2011-2017 Realm Inc All rights reserved"
        )
        acknowledgmentsGroup.addAcknowledgment(
            getString(R.string.realm_android_adapters),
            getString(R.string.license_name_apache),
            "Copyright (c) 2011-2017 Realm Inc All rights reserved"
        )
        acknowledgmentsGroup.addAcknowledgment(
            getString(R.string.calligraphy),
            getString(R.string.license_name_apache),
            "Copyright 2013 Christopher Jenkins"
        )
        acknowledgmentsGroup.addAcknowledgment(
            getString(R.string.easy_permissions),
            getString(R.string.license_name_apache),
            "Copyright 2017 Google"
        )
        acknowledgmentsGroup.addAcknowledgment(
            getString(R.string.hauler),
            getString(R.string.license_name_mit),
            "Copyright (c) 2018 The FUNTASTY"
        )
        acknowledgmentsGroup.addAcknowledgment(
            getString(R.string.ThreeTenABP),
            getString(R.string.license_name_apache),
            "Copyright (C) 2015 Jake Wharton"
        )
        acknowledgmentsGroup.addAcknowledgment(
            getString(R.string.lottie_android),
            getString(R.string.license_name_apache),
            "Copyright 2018 Airbnb, Inc."
        )

        acknowledgmentsGroup.addTitle(getString(R.string.customized_libraries))
        acknowledgmentsGroup.addAcknowledgment(getString(R.string.sleep_time_picker), "https://github.com/AppSci/SleepTimePicker")
        acknowledgmentsGroup.addAcknowledgment(
            getString(R.string.multi_type_file_picker),
            getString(R.string.license_name_apache),
            "Copyright 2016 Vincent Woo"
        )

        acknowledgmentsGroup.addTitle(getString(R.string.animations))
        acknowledgmentsGroup.addAcknowledgment(
            getString(R.string.lottie_funky_chicken),
            getString(R.string.license_name_cc),
            "@Михаил Голубь"
        )
        acknowledgmentsGroup.addAcknowledgment(
            getString(R.string.lottie_sound_visualizer),
            getString(R.string.license_name_cc),
            "@Nao Komura"
        )
        acknowledgmentsGroup.addAcknowledgment(
            getString(R.string.lottie_techno_penguin),
            getString(R.string.license_name_cc),
            "@Arpit Agarwal"
        )

        acknowledgmentsGroup.addTitle(getString(R.string.licenses))
        acknowledgmentsGroup.addAcknowledgment("Apache 2.0 License", getString(R.string.legal_apache))
        acknowledgmentsGroup.addAcknowledgment("MIT License", getString(R.string.legal_mit))
    }

    companion object {

        fun getIntent(activity: Activity): Intent {
            val intent = Intent(activity, AcknowledgmentsActivity::class.java)
            return intent
        }

    }

}
