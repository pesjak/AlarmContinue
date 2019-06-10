package primoz.com.alarmcontinue.views.alarm.fragments.newAlarm

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_new_alarm.*
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

    /*
    LifeCycle
     */

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_alarm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        realm = Realm.getDefaultInstance()
        initUI()
        initRecyclerView()
        initOnClickListeners()
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
            }
        }
    }

    /*
    NewAlarmContract.View
     */

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
    }

    private fun initOnClickListeners() {
        dummyClickableContainerRingtones.setOnClickListener {
            val intent3 = Intent(context, AudioPickActivity::class.java)
            intent3.putExtra(Constant.MAX_NUMBER, 5)
            intent3.putExtra(IS_NEED_FOLDER_LIST, true)
            startActivityForResult(intent3, Constant.REQUEST_CODE_PICK_AUDIO)
        }

        ivSave.setOnClickListener {
            mPresenter.saveAlarm(
                realm,
                timePicker.hour,
                timePicker.minute,
                daySelectorView.getSelectedDays(),
                adapter!!.songList,
                cbPreferenceResumePlaying.isChecked,
                cbPreferenceVibrate.isChecked
            )
        }

        tvClear.setOnClickListener {
            adapter?.songList?.clear()
            adapter?.notifyDataSetChanged()
            changeClearButtonVisibilityIfNeeded()
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

}
