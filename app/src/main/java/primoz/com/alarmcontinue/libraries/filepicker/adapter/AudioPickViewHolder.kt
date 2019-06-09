package primoz.com.alarmcontinue.libraries.filepicker.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.x_filepicker_item_audio_pick.view.*
import primoz.com.alarmcontinue.libraries.filepicker.Util
import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.AudioFile

class AudioPickViewHolder(
    itemView: View,
    var onSelectStateListener: OnSelectViewListener,
    var onPlayPressed: OnPlayListener
) : RecyclerView.ViewHolder(itemView) {

    private lateinit var audioFile: AudioFile
    var isChecked: Boolean = false

    init {
        itemView.checkBox.setOnClickListener {
            checkedPressed()
        }
        itemView.rootView.setOnClickListener {
            checkedPressed()
        }
        itemView.ivAudio.setOnClickListener {
            onPlayPressed.OnPlayClicked(audioFile)
        }
    }

    fun setData(audioFile: AudioFile) {
        this.audioFile = audioFile

        itemView.tvSongTitle.text = audioFile.name
        itemView.tvSongTitle.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        itemView.tvDuration.text = Util.getDurationString(audioFile.duration)
        isChecked = audioFile.isSelected
        itemView.checkBox.isChecked = isChecked
    }

    private fun checkedPressed() {
        isChecked = !isChecked
        itemView.checkBox.isChecked = isChecked
        if (!onSelectStateListener.OnSelectStateChanged(isChecked, adapterPosition)) {
            isChecked = !isChecked
        }
        itemView.checkBox.isChecked = isChecked
        audioFile.isSelected = isChecked
    }
}
