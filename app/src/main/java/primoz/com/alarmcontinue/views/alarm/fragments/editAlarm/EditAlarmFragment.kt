package primoz.com.alarmcontinue.views.alarm.fragments.editAlarm

import android.app.Activity
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
import primoz.com.alarmcontinue.views.alarm.fragments.newAlarm.adapters.SelectedSongsRecyclerViewAdapter

class EditAlarmFragment : Fragment(), EditAlarmContract.View {

    private lateinit var mPresenter: EditAlarmContract.Presenter
    private lateinit var realm: Realm

    private var adapter: SelectedSongsRecyclerViewAdapter? = null
    private var isDefaultRingtone = true

    private val alarmID: Int by lazy {
        arguments?.getInt(KEY_ALARM_ID) as Int
    }

    /*
    LifeCycle
     */

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_custom_alarm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        realm = Realm.getDefaultInstance()
        initUI()
        initRecyclerView()
        initOnClickListeners()
        changeClearButtonVisibilityIfNeeded()

        EditAlarmPresenter(this, alarmID)
        mPresenter.restoreUI(realm)
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            Constant.REQUEST_CODE_PICK_AUDIO -> if (resultCode == Activity.RESULT_OK) {
                val songList = data?.getParcelableArrayListExtra<Parcelable>(Constant.RESULT_PICK_AUDIO) as ArrayList<AudioFile>
                tvRingtonesTitle.text = getString(R.string.ringtones)
                adapter?.songList = songList
                changeClearButtonVisibilityIfNeeded()
                isDefaultRingtone = false
                showCheckBoxResumePlaying(true)
            }
        }
    }

    /*
    NewAlarmContract.View
     */

    override fun getViewActivity(): Activity {
        return activity as Activity
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
        alarm.songsList?.let {
            tvSongNone.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            tvClear.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
            if (alarm.useDefaultRingtone) {
                adapter?.songList = mutableListOf(getDefaultRingtone())
            } else {
                if (it.isNotEmpty()) cbPreferenceResumePlaying.visibility = View.VISIBLE
                val selectedSongList = mutableListOf<AudioFile>()
                for (song in it) {
                    val audioFile = AudioFile()
                    audioFile.name = song.name
                    song.duration?.let { duration -> audioFile.duration = duration }
                    audioFile.path = song.path
                    audioFile.bucketId = song.bucketId
                    audioFile.bucketName = song.bucketName
                    song.size?.let { size -> audioFile.size = size }
                    selectedSongList.add(audioFile)
                }
                adapter?.songList = selectedSongList
            }
        }

        cbPreferenceResumePlaying.isChecked = alarm.shouldResumePlaying
        cbPreferenceVibrate.isChecked = alarm.shouldVibrate

    }

    override fun finish() {
        activity?.finish()
    }

    /*
    Private
     */

    private fun initUI() {
        btnDelete.visibility = View.VISIBLE
        timePicker.setIs24HourView(true)
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
                cbPreferenceVibrate.isChecked,
                isDefaultRingtone
            )
        }

        tvClear.setOnClickListener {
            adapter?.songList?.clear()
            adapter?.notifyDataSetChanged()
            changeClearButtonVisibilityIfNeeded()
            isDefaultRingtone = false
            showCheckBoxResumePlaying(false)
        }

        btnDelete.setOnClickListener {
            mPresenter.deleteAlarm(realm)
        }
    }

    private fun changeClearButtonVisibilityIfNeeded() {
        adapter?.let {
            tvSongNone.visibility = if (it.songList.isEmpty()) View.VISIBLE else View.GONE
            tvClear.visibility = if (it.songList.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(context)
        adapter = SelectedSongsRecyclerViewAdapter()
        rvRingtones.layoutManager = linearLayoutManager
        rvRingtones.adapter = adapter
    }

    private fun getDefaultRingtone(): AudioFile {
        var alarmTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        if (alarmTone == null) {
            alarmTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            if (alarmTone == null) {
                alarmTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            }
        }
        val ringtoneAlarm = RingtoneManager.getRingtone(context, alarmTone)
        val defaultRingtone = AudioFile()
        defaultRingtone.name = ringtoneAlarm.getTitle(context)
        return defaultRingtone
    }

    private fun showCheckBoxResumePlaying(shouldShow: Boolean) {
        cbPreferenceResumePlaying.visibility = if (shouldShow) View.VISIBLE else View.GONE
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
