package primoz.com.alarmcontinue.views.alarm.fragments.newAlarm

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm
import kotlinx.android.synthetic.main.layout_custom_alarm.*
import primoz.com.alarmcontinue.R
import primoz.com.alarmcontinue.libraries.filepicker.Constant
import primoz.com.alarmcontinue.libraries.filepicker.activity.AudioPickActivity
import primoz.com.alarmcontinue.libraries.filepicker.activity.BaseActivity.Companion.IS_NEED_FOLDER_LIST
import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.AudioFile
import primoz.com.alarmcontinue.views.alarm.fragments.adapters.SelectedSongsRecyclerViewAdapter

class NewAlarmFragment : Fragment(), NewAlarmContract.View {

    private lateinit var mPresenter: NewAlarmContract.Presenter
    private lateinit var realm: Realm

    private var adapter: SelectedSongsRecyclerViewAdapter? = null

    /*
    LifeCycle
     */

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            Constant.REQUEST_CODE_PICK_AUDIO -> if (resultCode == RESULT_OK) {
                val songList = data?.getParcelableArrayListExtra<Parcelable>(Constant.RESULT_PICK_AUDIO) as ArrayList<AudioFile>
                mPresenter.handleSelectedAudioFileList(songList)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_custom_alarm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        realm = Realm.getDefaultInstance()

        initUI()

        NewAlarmPresenter(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    /*
    NewAlarmContract.View
     */

    override fun finish() {
        getViewActivity().finish()
    }

    override fun getViewActivity(): Activity {
        return activity!!
    }

    override fun setPresenter(presenter: NewAlarmContract.Presenter) {
        this.mPresenter = presenter
    }


    override fun showDefaultUI() {
        mPresenter.loadSongList()
        //Checkboxes
        cbPreferenceResumePlaying.isChecked = false
        cbPreferenceVibrate.isChecked = false
    }

    override fun updateSongList(selectedSongList: MutableList<AudioFile>) {
        adapter?.songList = selectedSongList
    }

    override fun showNoneSelectedSongs(shouldShow: Boolean) {
        tvSongNone.visibility = if (shouldShow) View.VISIBLE else View.GONE
    }

    override fun showTextSetDefaultButton(shouldShow: Boolean) {
        btnDefaultAndClear.text = if (shouldShow) getString(R.string.set_default) else getString(R.string.clear)
    }

    /*
    Private
     */

    private fun initUI() {
        initRecyclerView()
        initOnClickListeners()
        setOnScrollListener()
        initTimePicker()
        timePicker.setIs24HourView(true)
    }

    private fun initTimePicker() {
        timePicker.setIs24HourView(true)
        setImageButtonToGone(timePicker)
    }

    private fun setImageButtonToGone(viewGroup: ViewGroup) {
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            if (child is LinearLayout) {
                setImageButtonToGone(child)
            } else if (child is AppCompatImageButton) {
                child.visibility = View.GONE
            }
        }
    }

    private fun setOnScrollListener() {
        scrollView.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (v?.canScrollVertically(-1) == true) {
                toolbar.elevation = 8F
                line0.visibility = View.GONE
            } else {
                toolbar.elevation = 0F
                line0.visibility = View.VISIBLE
            }
        }
    }

    private fun initOnClickListeners() {
        dummyClickableContainerRingtones.setOnClickListener {
            val intentAudioPick = Intent(context, AudioPickActivity::class.java)
            intentAudioPick.putExtra(Constant.MAX_NUMBER, AudioPickActivity.DEFAULT_MAX_NUMBER)
            intentAudioPick.putExtra(IS_NEED_FOLDER_LIST, true)
            startActivityForResult(intentAudioPick, Constant.REQUEST_CODE_PICK_AUDIO)
        }

        ivClose.setOnClickListener {
            finish()
        }

        ivSave.setOnClickListener {
            mPresenter.saveAlarm(
                realm,
                timePicker.hour,
                timePicker.minute,
                daySelectorView.selectedDays,
                adapter!!.songList,
                cbPreferenceResumePlaying.isChecked,
                cbPreferenceVibrate.isChecked
            )
        }

        btnDefaultAndClear.setOnClickListener {
            mPresenter.clearOrSetDefaultSong()
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(context)
        adapter = SelectedSongsRecyclerViewAdapter()
        rvRingtones.layoutManager = linearLayoutManager
        rvRingtones.adapter = adapter
    }

}
