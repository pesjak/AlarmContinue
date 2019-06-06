package primoz.com.alarmcontinue.views.home

import android.view.LayoutInflater
import android.view.ViewGroup
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import primoz.com.alarmcontinue.R
import primoz.com.alarmcontinue.model.Alarm

class MyAlarmsRecyclerViewAdapter(
    data: OrderedRealmCollection<Alarm>,
    var listener: OnAlarmListener
) : RealmRecyclerViewAdapter<Alarm, AlarmViewHolder>(data, true) {

    init {
        // Only set this if the model class has a primary key that is also a integer or long.
        // In that case, {@code getItemId(int)} must also be overridden to return the key.
        // See https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#hasStableIds()
        // See https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html#getItemId(int)
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_alarm, parent, false)
        return AlarmViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        getItem(position)?.let { alarm ->
            holder.setData(alarm)
        }
    }

    override fun getItemId(index: Int): Long {
        return getItem(index)?.id?.toLong() ?: 0
    }
}
