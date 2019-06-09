package primoz.com.alarmcontinue.libraries.filepicker.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import java.util.*


abstract class BaseAdapter<T, VH : RecyclerView.ViewHolder>(
    protected var mContext: Context,
    protected var mList: ArrayList<T>
) : RecyclerView.Adapter<VH>() {
    var mListener: OnSelectStateListener<T>? = null

    val dataSet: List<T>
        get() = mList

    fun add(list: List<T>) {
        mList.addAll(list)
        notifyDataSetChanged()
    }

    fun add(file: T) {
        mList.add(file)
        notifyDataSetChanged()
    }

    fun add(index: Int, file: T) {
        mList.add(index, file)
        notifyDataSetChanged()
    }

    fun refresh(list: List<T>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    fun refresh(file: T) {
        mList.clear()
        mList.add(file)
        notifyDataSetChanged()
    }

    fun setOnSelectStateListener(listener: OnSelectStateListener<T>) {
        mListener = listener
    }
}
