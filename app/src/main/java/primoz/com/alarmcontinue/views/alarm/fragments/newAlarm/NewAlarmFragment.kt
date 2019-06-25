package primoz.com.alarmcontinue.views.alarm.fragments.newAlarm

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
import primoz.com.alarmcontinue.libraries.filepicker.Constant
import primoz.com.alarmcontinue.libraries.filepicker.activity.AudioPickActivity
import primoz.com.alarmcontinue.libraries.filepicker.activity.BaseActivity.Companion.IS_NEED_FOLDER_LIST
import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.AudioFile
import primoz.com.alarmcontinue.views.alarm.fragments.newAlarm.adapters.SelectedSongsRecyclerViewAdapter

class NewAlarmFragment : Fragment(), NewAlarmContract.View {

    private lateinit var mPresenter: NewAlarmContract.Presenter
    private lateinit var realm: Realm

    private var adapter: SelectedSongsRecyclerViewAdapter? = null
    private var isDefaultRingtone = true

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
        NewAlarmPresenter(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            Constant.REQUEST_CODE_PICK_AUDIO -> if (resultCode == RESULT_OK) {
                val songList = data?.getParcelableArrayListExtra<Parcelable>(Constant.RESULT_PICK_AUDIO) as ArrayList<AudioFile>
                tvRingtonesTitle.text = getString(R.string.ringtones)
                adapter?.songList = songList
                changeClearButtonVisibilityIfNeeded()
                isDefaultRingtone = false
            }
        }
    }

    /*
    NewAlarmContract.View
     */

    override fun getViewActivity(): Activity {
        return activity!!
    }

    override fun setPresenter(presenter: NewAlarmContract.Presenter) {
        this.mPresenter = presenter
    }

    override fun finish() {
        activity?.finish()
    }

    /*
    Private
     */

    private fun initUI() {
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
                cbPreferenceVibrate.isChecked,
                isDefaultRingtone
            )
        }

        tvClear.setOnClickListener {
            adapter?.songList?.clear()
            adapter?.notifyDataSetChanged()
            changeClearButtonVisibilityIfNeeded()
            isDefaultRingtone = false
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
        adapter?.songList = mutableListOf(getDefaultRingtone())
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

}
