package primoz.com.alarmcontinue

import android.app.Application
import android.content.Context
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext
        ViewPump.init(ViewPump.builder().addInterceptor(CalligraphyInterceptor(CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Raleway-Regular.ttf")
                .setFontAttrId(R.attr.fontPath).build())).build())
    }

    companion object {
        lateinit var appContext: Context
            private set
    }
}
