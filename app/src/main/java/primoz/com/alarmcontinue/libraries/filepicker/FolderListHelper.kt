package primoz.com.alarmcontinue.libraries.filepicker

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import primoz.com.alarmcontinue.R
import primoz.com.alarmcontinue.libraries.filepicker.adapter.FolderListAdapter
import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.Directory

import java.util.ArrayList

/**
 * Created by Vincent Woo
 * Date: 2018/2/27
 * Time: 13:43
 */

class FolderListHelper {
    private var mPopupWindow: PopupWindow? = null
    private var mContentView: View? = null
    private var rv_folder: RecyclerView? = null
    private var mAdapter: FolderListAdapter? = null

    fun initFolderListView(ctx: Context) {
        if (mPopupWindow == null) {
            mContentView = LayoutInflater.from(ctx).inflate(R.layout.vw_layout_folder_list, null)
            rv_folder = mContentView!!.findViewById<View>(R.id.rv_folder) as RecyclerView
            mAdapter = FolderListAdapter(ctx, ArrayList())
            rv_folder!!.adapter = mAdapter
            rv_folder!!.layoutManager = LinearLayoutManager(ctx)
            mContentView!!.isFocusable = true
            mContentView!!.isFocusableInTouchMode = true

            mPopupWindow = PopupWindow(mContentView)
            mPopupWindow!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            mPopupWindow!!.isFocusable = true
            mPopupWindow!!.isOutsideTouchable = false
            mPopupWindow!!.isTouchable = true
        }
    }

    fun setFolderListListener(listener: FolderListAdapter.FolderListListener) {
        mAdapter!!.setListener(listener)
    }

    fun fillData(list: List<Directory<*>>) {
        mAdapter!!.refresh(list)
    }

    fun toggle(anchor: View) {
        if (mPopupWindow!!.isShowing) {
            mPopupWindow!!.dismiss()
        } else {
            mContentView!!.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            mPopupWindow!!.showAsDropDown(
                anchor,
                (anchor.measuredWidth - mContentView!!.measuredWidth) / 2,
                0
            )
            mPopupWindow!!.update(
                anchor, mContentView!!.measuredWidth,
                mContentView!!.measuredHeight
            )
        }
    }
}
