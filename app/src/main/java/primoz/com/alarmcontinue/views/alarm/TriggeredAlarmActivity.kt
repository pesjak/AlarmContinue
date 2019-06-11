package primoz.com.alarmcontinue.views.alarm

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_triggered_alarm.*
import primoz.com.alarmcontinue.R
import primoz.com.alarmcontinue.views.BaseActivity

class TriggeredAlarmActivity : BaseActivity() {

    private var mediaPlayer: MediaPlayer? = null

    private val pathOfSong: String?
        get() {
            return intent.extras?.getString(ARG_PATH)
        }

    private val alarmID: Int?
        get() {
            return intent.extras?.getInt(ARG_ALARM_ID)
        }

    /*
    LifeCycle
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_triggered_alarm)

        startPlayingSelectedSong()

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

    override fun onPause() {
        super.onPause()
        mediaPlayer?.stop()
    }

    private fun startPlayingSelectedSong() {
        pathOfSong ?: return
        mediaPlayer = MediaPlayer.create(this, Uri.parse(pathOfSong))
        mediaPlayer?.isLooping = true
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