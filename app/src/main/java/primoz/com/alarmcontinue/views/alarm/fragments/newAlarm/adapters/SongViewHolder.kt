package primoz.com.alarmcontinue.views.alarm.fragments.newAlarm.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_song.view.*
import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.AudioFile

class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private lateinit var song: AudioFile

    fun setData(song: AudioFile, isOnly1Song: Boolean) {
        this.song = song
        itemView.tvSongTitle.text = if (isOnly1Song) song.name else "- ${song.name}"
    }
}