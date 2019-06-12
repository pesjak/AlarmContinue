package primoz.com.alarmcontinue.views.alarm

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_triggered_alarm.*
import primoz.com.alarmcontinue.MyApplication
import primoz.com.alarmcontinue.R
import primoz.com.alarmcontinue.model.Alarm
import primoz.com.alarmcontinue.model.DataHelper
import primoz.com.alarmcontinue.views.BaseActivity

class TriggeredAlarmActivity : BaseActivity() {

    private lateinit var realm: Realm
    private lateinit var vibrator: Vibrator
    private var mediaPlayer: MediaPlayer? = null
    private val pathOfSong: String?
        get() {
            return intent.extras?.getString(ARG_PATH)
        }

    private val alarmID: Int
        get() {
            return intent.extras?.getInt(ARG_ALARM_ID)!!
        }

    /*
    LifeCycle
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_triggered_alarm)
        realm = Realm.getDefaultInstance()
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        Log.d("Triggered", "onCreated")
        DataHelper.getAlarm(realm, alarmID)?.let { alarm ->
            Log.d("Triggered", "Alarm - OK")
            pathOfSong?.let {
                initMediaPlayer(alarm)
            }
            if (alarm.shouldVibrate) {
                startVibrating()
            }

        }

        haulerView.setOnDragDismissedListener {
            finish() // finish activity when dismissed
        }

        /*
        Toast.makeText(context, "Should play", Toast.LENGTH_LONG).show() // For example
        path?.let {
            val mediaPlayer = MediaPlayer.create(context, Uri.parse(path))
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        }*/
        //TODO Set another alarm if it DOESN'T have ON resumePlaying
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Triggered", "onDestroy")
            mediaPlayer?.stop()
            vibrator.cancel()
            realm.close()

    }

    private fun initMediaPlayer(alarm: Alarm) {
        mediaPlayer = MediaPlayer.create(this, Uri.parse(pathOfSong))
        mediaPlayer?.isLooping = false
        mediaPlayer?.setOnCompletionListener {
            it?.stop()
            it?.reset()
            it?.isLooping = false
            it?.setDataSource(this, Uri.parse(alarm.songsList?.random()?.path))
            it?.prepare()
            it?.start()
        }
        if (alarm.shouldResumePlaying) {
            mediaPlayer?.seekTo(alarm.secondsPlayed)
        }
        startPlayingSelectedSong()
    }

    private fun startVibrating() {
        val pattern = longArrayOf(500, 500, 500, 500)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else {
            vibrator.vibrate(pattern, 0)
        }
    }

    private fun startPlayingSelectedSong() {
        pathOfSong ?: return
        mediaPlayer?.start()
    }

    companion object {
        const val ARG_ALARM_ID = "AlarmID"
        const val ARG_PATH = "Path"

        fun getIntent(context: Context, alarmID: Int?, alarmPath: String?): Intent {
            val intent = Intent(context, TriggeredAlarmActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY) //If it doesn't hide in recent use or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra(ARG_ALARM_ID, alarmID)
            intent.putExtra(ARG_PATH, alarmPath)
            return intent
        }
    }
}