package primoz.com.alarmcontinue.views.alarm.broadcast

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.WindowManager
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
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

        showIfScreenIsLocked()
        showDanceAnimation()

        realm = Realm.getDefaultInstance()
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        val alarmFromRealm = DataHelper.getAlarm(realm, alarmID)
        var showToast = true
        if (alarmFromRealm == null) {
            showToast = false
            DataHelper.getBedtimeAlarm(realm)
        }

        alarmFromRealm?.let { alarm ->
            val shouldEnableAlarm = alarm.isEnabled && alarm.daysList!!.isNotEmpty()
            DataHelper.enableAlarm(alarmID, shouldEnableAlarm, realm)
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
    }

    override fun onDestroy() {
        super.onDestroy()
        if (shouldResumePlaying) {
            mediaPlayer?.let {
                DataHelper.updateProgress(alarmID, it.currentPosition)
            }
        }
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentUserVolume, AudioManager.FLAG_PLAY_SOUND)
        timer.cancel()
        mediaPlayer?.stop()
        vibrator.cancel()
        realm.close()
    }

    /*
    Private
     */

    private fun showIfScreenIsLocked() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }

    private fun showDanceAnimation() {
        val lottieFiles = mutableListOf(
            "lottie/dance/chicken_6.json",
            "lottie/dance/sound.json",  //White
            "lottie/dance/pinguin.json" //White
        )
        val file = lottieFiles.random()
        messageLottie.setAnimation(file)
        if (file == "lottie/dance/pinguin.json"
            || file == "lottie/dance/sound.json"
        ) {
            messageLottie.addValueCallback(
                KeyPath("**"), LottieProperty.COLOR_FILTER,
                { PorterDuffColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_ATOP) }
            )
        }
        messageLottie.playAnimation()
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