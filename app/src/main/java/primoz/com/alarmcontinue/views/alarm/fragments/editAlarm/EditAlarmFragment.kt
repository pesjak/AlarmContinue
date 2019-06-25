package primoz.com.alarmcontinue.views.alarm.fragments.editAlarm

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.media.RingtoneManager
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm
import kotlinx.android.synthetic.main.layout_custom_alarm.*
import primoz.com.alarmcontinue.R
import primoz.com.alarmcontinue.enums.EnumDayOfWeek
import primoz.com.alarmcontinue.libraries.filepicker.Constant
import primoz.com.alarmcontinue.libraries.filepicker.activity.AudioPickActivity
import primoz.com.alarmcontinue.libraries.filepicker.activity.BaseActivity
import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.AudioFile
import primoz.com.alarmcontinue.model.Alarm
import primoz.com.alarmcontinue.views.alarm.fragments.adapters.SelectedSongsRecyclerViewAdapter

class EditAlarmFragment : Fragment(), EditAlarmContract.View {

    private lateinit var mPresenter: EditAlarmContract.Presenter
    private lateinit var realm: Realm

    private var adapter: SelectedSongsRecyclerViewAdapter? = null

    private val alarmID: Int by lazy {
        arguments?.getInt(KEY_ALARM_ID) as Int
    }

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

        EditAlarmPresenter(this, alarmID)
        mPresenter.restoreUI(realm)
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

    override fun setPresenter(presenter: EditAlarmContract.Presenter) {
        this.mPresenter = presenter
    }

    override fun updateUI(alarm: Alarm) {
        //Time
        alarm.hourAlarm?.let { hour ->
            timePicker.hour = hour
        }
        alarm.minuteAlarm?.let { minute ->
            timePicker.minute = minute
        }

        //Selected Days
        val selectedDays = mutableListOf<EnumDayOfWeek>()
        alarm.daysList?.let {
            for (day in it.iterator()) {
                selectedDays.add(day.nameOfDayString)
            }
        }
        daySelectorView.selectedDays = selectedDays

        //SongList
        mPresenter.loadSongList(alarm)

        //Checkboxes
        cbPreferenceResumePlaying.isChecked = alarm.shouldResumePlaying
        cbPreferenceVibrate.isChecked = alarm.shouldVibrate
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
        btnDelete.visibility = View.VISIBLE
        timePicker.setIs24HourView(true)
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
            intentAudioPick.putExtra(Constant.MAX_NUMBER, 5)
            intentAudioPick.putExtra(BaseActivity.IS_NEED_FOLDER_LIST, true)
            startActivityForResult(intentAudioPick, Constant.REQUEST_CODE_PICK_AUDIO)
        }

        ivClose.setOnClickListener {
            finish()
        }

        ivSave.setOnClickListener {
            mPresenter.updateAlarm(
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

        btnDelete.setOnClickListener {
            mPresenter.deleteAlarm(realm)
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(context)
        adapter = SelectedSongsRecyclerViewAdapter()
        rvRingtones.layoutManager = linearLayoutManager
        rvRingtones.adapter = adapter
    }

    companion object {
        const val KEY_ALARM_ID = "alarmID"
        fun getInstance(alarmID: Int): EditAlarmFragment {
            return EditAlarmFragment().apply {
                arguments = Bundle().apply {
                    putInt(KEY_ALARM_ID, alarmID)
                }
            }
        }
    }
}
