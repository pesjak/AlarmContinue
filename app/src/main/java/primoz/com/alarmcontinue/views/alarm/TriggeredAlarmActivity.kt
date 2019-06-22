package primoz.com.alarmcontinue.views.alarm

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_triggered_alarm.*
import primoz.com.alarmcontinue.R
import primoz.com.alarmcontinue.model.Alarm
import primoz.com.alarmcontinue.model.DataHelper
import primoz.com.alarmcontinue.views.BaseActivity
import primoz.com.alarmcontinue.views.alarm.broadcast.MyAlarm

class TriggeredAlarmActivity : BaseActivity() {

    private lateinit var realm: Realm
    private lateinit var vibrator: Vibrator
    private var mediaPlayer: MediaPlayer? = null

    private val alarmID: Int
        get() {
            return intent.extras?.getInt(ARG_ALARM_ID)!!
        }

    private var shouldResumePlaying: Boolean = false

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
            Log.d("Alarm Current", alarm.secondsPlayed.toString())

            DataHelper.shouldEnableAlarm(alarmID, alarm.isEnabled, realm)
            if (alarm.isEnabled) {
                MyAlarm.setAlarm(baseContext, alarm)
            } else {
                MyAlarm.cancelAlarm(baseContext, alarm)
            }
            if (alarm.useDefaultRingtone) {
                var uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                if (uri == null) {
                    uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    if (uri == null) {
                        uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                    }
                }
                mediaPlayer = MediaPlayer.create(this, uri)
                mediaPlayer?.isLooping = true
                mediaPlayer?.start()
            } else {
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
        if (shouldResumePlaying) {
            mediaPlayer?.let {
                DataHelper.updateProgress(alarmID, it.currentPosition)
                Log.d("Alarm Played", it.currentPosition.toString())
            }
        }
        mediaPlayer?.stop()
        vibrator.cancel()
        realm.close()

    }

    private fun initMediaPlayer(alarm: Alarm) {
        mediaPlayer = MediaPlayer.create(this, Uri.parse(alarm.currentlySelectedPath))
        mediaPlayer?.isLooping = false
        mediaPlayer?.setOnCompletionListener {
            it?.stop()
            it?.reset()
            it?.isLooping = false
            val path = alarm.songsList?.random()?.path
            it?.setDataSource(this, Uri.parse(path))
            DataHelper.nextRandomSong(alarmID, path)
            it?.prepare()
            it?.start()
        }
        shouldResumePlaying = alarm.shouldResumePlaying
        if (alarm.shouldResumePlaying) {
            mediaPlayer?.seekTo(alarm.secondsPlayed)
        }
        mediaPlayer?.start()
    }

    private fun startVibrating() {
        val pattern = longArrayOf(500, 500, 500, 500)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else {
            vibrator.vibrate(pattern, 0)
        }
    }

    companion object {
        const val ARG_ALARM_ID = "AlarmID"

        fun getIntent(context: Context, alarmID: Int?): Intent {
            val intent = Intent(context, TriggeredAlarmActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY) //If it doesn't hide in recent use or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra(ARG_ALARM_ID, alarmID)
            return intent
        }
    }
}