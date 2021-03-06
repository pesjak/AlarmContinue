package primoz.com.alarmcontinue.libraries.filepicker.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import primoz.com.alarmcontinue.R
import primoz.com.alarmcontinue.libraries.filepicker.Util
import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.AudioFile
import java.io.File
import java.util.*

class AudioPickAdapter(
    context: Context,
    private val mMaxNumber: Int,
    list: ArrayList<AudioFile> = arrayListOf()
) :
    BaseAdapter<AudioFile, AudioPickViewHolder>(context, list) {

    private var mCurrentNumber = 0

    val isUpToMax: Boolean
        get() = mCurrentNumber >= mMaxNumber

    private val onCheckedChangeListener = object : OnSelectViewListener {
        override fun OnSelectStateChanged(isChecked: Boolean, position: Int): Boolean {
            if (isChecked && isUpToMax) {
                Toast.makeText(context, R.string.vw_up_to_max, Toast.LENGTH_SHORT).show()
                return false
            }
            if (isChecked) mCurrentNumber++ else mCurrentNumber--
            mListener?.OnSelectStateChanged(isChecked, mList[position])
            return true
        }

    }

    private val onPlayListener = object : OnPlayListener {
        override fun OnPlayClicked(audioFile: AudioFile) {
            val intent = Intent(Intent.ACTION_VIEW)
            val uri: Uri
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                val f = File(audioFile.path)
                uri = FileProvider.getUriForFile(mContext, context.applicationContext.packageName + ".provider", f)
            } else {
                uri = Uri.parse("file://" + audioFile.path)
            }
            intent.setDataAndType(uri, "audio/mp3")
            if (Util.detectIntent(mContext, intent)) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context, R.string.vw_no_audio_play_app, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioPickViewHolder {
        val itemView = LayoutInflater.from(mContext).inflate(R.layout.x_filepicker_item_audio_pick, parent, false)
        return AudioPickViewHolder(itemView, onCheckedChangeListener, onPlayListener)
    }

    override fun onBindViewHolder(holder: AudioPickViewHolder, position: Int) {
        val file = mList[position]
        holder.setData(file)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun setCurrentNumber(number: Int) {
        mCurrentNumber = number
    }
}
