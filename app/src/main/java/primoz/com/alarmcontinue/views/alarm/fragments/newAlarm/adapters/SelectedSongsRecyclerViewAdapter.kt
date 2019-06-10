package primoz.com.alarmcontinue.views.alarm.fragments.newAlarm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import primoz.com.alarmcontinue.R
import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.AudioFile

class SelectedSongsRecyclerViewAdapter : RecyclerView.Adapter<SongViewHolder>() {

    var songList: MutableList<AudioFile> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false))
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        songList[position].let {
            holder.setData(it, itemCount == 1)
        }
    }

    override fun getItemCount(): Int {
        return songList.size
    }

}
