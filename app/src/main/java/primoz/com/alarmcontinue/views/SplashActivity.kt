package primoz.com.alarmcontinue.views

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import primoz.com.alarmcontinue.views.home.MainActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}