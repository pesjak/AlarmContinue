package primoz.com.alarmcontinue.views.alarm.broadcast

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
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
import java.util.*
import kotlin.math.roundToInt

class TriggeredAlarmActivity : BaseActivity() {

    private var currentUserVolume: Int = 0
    private lateinit var timer: Timer
    private lateinit var realm: Realm
    private lateinit var vibrator: Vibrator
    private var mediaPlayer: MediaPlayer? = null
    private var shouldResumePlaying: Boolean = false

    private val alarmID: Int
        get() {
            return intent.extras?.getInt(ARG_ALARM_ID)!!
        }

    private val mAudioManager: AudioManager by lazy {
        baseContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
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

        val alarmFromRealm = DataHelper.getAlarm(realm, alarmID)
        var showToast = true
        if (alarmFromRealm == null) {
            DataHelper.getBedtimeAlarm(realm)
            showToast = false
        }

        alarmFromRealm?.let { alarm ->
            Log.d("Triggered", "Alarm - OK")
            Log.d("Alarm Current", alarm.secondsPlayed.toString())

            val shouldEnableAlarm = alarm.isEnabled && alarm.daysList!!.isNotEmpty()
            DataHelper.shouldEnableAlarm(alarmID, shouldEnableAlarm, realm)
            if (shouldEnableAlarm) {
                MyAlarm.setAlarm(baseContext, alarm, showToast)
            } else {
                MyAlarm.cancelAlarm(baseContext, alarm.id)
            }
            if (alarm.useDefaultRingtone) {
                var uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                if (uri == null) {
                    uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    if (uri == null) {
                        uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                    }
                }
                mediaPlayer = MediaPlayer()
                mediaPlayer?.let { increaseVolumeOverTime(it, alarm.shouldVibrate) }
                mediaPlayer?.setDataSource(this, uri)
                mediaPlayer?.isLooping = true
                shouldResumePlaying = alarm.shouldResumePlaying
                mediaPlayer?.setOnPreparedListener {
                    if (alarm.shouldResumePlaying) {
                        mediaPlayer?.seekTo(alarm.secondsPlayed)
                    }
                    mediaPlayer?.start()
                }
                mediaPlayer?.prepareAsync()
            } else {
                initMediaPlayer(alarm)
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
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentUserVolume, AudioManager.FLAG_PLAY_SOUND)
        timer.cancel()
        mediaPlayer?.stop()
        vibrator.cancel()
        realm.close()
    }

    private fun increaseVolumeOverTime(mediaPlayer: MediaPlayer, shouldVibrate: Boolean) {
        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )

        currentUserVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        var currentVolume = 1
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_PLAY_SOUND)

        if (shouldVibrate) {
            startVibrating()
        }

        timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                currentVolume += 1
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, AudioManager.FLAG_PLAY_SOUND)
                if (currentVolume % 10 == 0) {
                    if (shouldVibrate) {
                        startVibrating(currentVolume)
                    }
                }

                if (currentVolume >= 90) this.cancel()
            }
        }, 0, 2000)
    }

    private fun startVibrating(currentVolume: Int = 10) {
        val vibratorLength = ((50 * currentVolume) / 1.2).roundToInt().toLong()
        val patternShort = longArrayOf(1200, vibratorLength)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(patternShort, 0))
        } else {
            vibrator.vibrate(patternShort, 0)
        }
    }

    private fun initMediaPlayer(alarm: Alarm) {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.let { increaseVolumeOverTime(it, alarm.shouldVibrate) }
        mediaPlayer?.setDataSource(this, Uri.parse(alarm.currentlySelectedPath))
        mediaPlayer?.isLooping = false
        mediaPlayer?.setOnCompletionListener {
            it?.stop()
            it?.reset()
            it?.isLooping = false
            val path = alarm.songsList?.random()?.path
            it?.setDataSource(this, Uri.parse(path))
            DataHelper.nextRandomSong(alarmID, path)
            it?.setOnPreparedListener {
                mediaPlayer?.start()
            }
            it?.prepareAsync()
        }
        mediaPlayer?.setOnPreparedListener {
            if (alarm.shouldResumePlaying) {
                mediaPlayer?.seekTo(alarm.secondsPlayed)
            }
            mediaPlayer?.start()
        }
        mediaPlayer?.prepareAsync()
        shouldResumePlaying = alarm.shouldResumePlaying
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