package primoz.com.alarmcontinue

import android.app.Application
import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kobakei.ratethisapp.RateThisApp
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import io.realm.Realm
import io.realm.RealmConfiguration
import primoz.com.alarmcontinue.model.AlarmList
import primoz.com.alarmcontinue.model.DataHelper
import primoz.com.alarmcontinue.model.SongList

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext

        //Init Realm
        Realm.init(this)
        val realmConfig = RealmConfiguration.Builder()
            .initialData { realm ->
                realm.createObject(AlarmList::class.java)
                realm.createObject(SongList::class.java)
                DataHelper.createDefaultBedtimeAlarm(realm)
            }
            .build()
        //Realm.deleteRealm(realmConfig) // Delete Realm between app restarts.
        Realm.setDefaultConfiguration(realmConfig)

        ViewPump.init(
            ViewPump.builder().addInterceptor(
                CalligraphyInterceptor(
                    CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Lato-Regular.ttf")
                        .setFontAttrId(R.attr.fontPath).build()
                )
            ).build()
        )

        //Timezone init for SleepTimePicker
        AndroidThreeTen.init(appContext)
        // Set custom criteria (optional)
        RateThisApp.init(RateThisApp.Config(3, 4))
    }

    companion object {
        lateinit var appContext: Context
            private set
    }
}
