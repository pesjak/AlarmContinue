package primoz.com.alarmcontinue.views.alarm.fragments.bedtime

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_bedtime_alarm.*
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import primoz.com.alarmcontinue.R
import primoz.com.alarmcontinue.libraries.filepicker.Constant
import primoz.com.alarmcontinue.libraries.filepicker.activity.AudioPickActivity
import primoz.com.alarmcontinue.libraries.filepicker.activity.BaseActivity
import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.AudioFile
import primoz.com.alarmcontinue.model.Alarm
import primoz.com.alarmcontinue.views.alarm.fragments.adapters.SelectedSongsRecyclerViewAdapter

class BedtimeFragment : Fragment(), BedtimeContract.View {

    private lateinit var realm: Realm
    private lateinit var mPresenter: BedtimeContract.Presenter

    private var adapter: SelectedSongsRecyclerViewAdapter? = null

    /*
    LifeCycle
     */

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            Constant.REQUEST_CODE_PICK_AUDIO -> if (resultCode == Activity.RESULT_OK) {
                val songList = data?.getParcelableArrayListExtra<Parcelable>(Constant.RESULT_PICK_AUDIO) as ArrayList<AudioFile>
                mPresenter.handleSelectedAudioFileList(songList)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bedtime_alarm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        realm = Realm.getDefaultInstance()

        initUI()

        BedtimePresenter(this)
        mPresenter.restoreUI(realm)
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    /*
    Presenter
     */

    override fun finish() {
        getViewActivity().finish()
    }

    override fun getViewActivity(): Activity {
        return activity!!
    }

    override fun setPresenter(presenter: BedtimeContract.Presenter) {
        mPresenter = presenter
    }

    override fun updateUI(alarm: Alarm) {
        //TimePicker
        timePicker.setTime(
            LocalTime.of(alarm.hourBedtimeSleep!!, alarm.minuteBedtimeSleep!!),
            LocalTime.of(alarm.hourAlarm!!, alarm.minuteAlarm!!)
        )
        timePicker.listener = { bedTime: LocalTime, wakeTime: LocalTime ->
            handleUpdate(bedTime, wakeTime)
        }
        handleUpdate(timePicker.getBedTime(), timePicker.getWakeTime())

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

    override fun showReminder(reminder: CharSequence) {
        reminderBedtime.text = reminder
    }

    /*
    Private
     */

    private fun initUI() {
        initRecyclerView()
        initOnClickListeners()
        setOnScrollListener()
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(context)
        adapter = SelectedSongsRecyclerViewAdapter()
        rvRingtones.layoutManager = linearLayoutManager
        rvRingtones.adapter = adapter
    }

    private fun initOnClickListeners() {
        dummyClickableContainerRingtones.setOnClickListener {
            val intentAudioPick = Intent(context, AudioPickActivity::class.java)
            intentAudioPick.putExtra(Constant.MAX_NUMBER, AudioPickActivity.DEFAULT_MAX_NUMBER)
            intentAudioPick.putExtra(BaseActivity.IS_NEED_FOLDER_LIST, true)
            startActivityForResult(intentAudioPick, Constant.REQUEST_CODE_PICK_AUDIO)
        }

        ivClose.setOnClickListener {
            finish()
        }

        ivSave.setOnClickListener {
            mPresenter.updateBedtime(
                realm,
                timePicker.getBedTime().hour,
                timePicker.getBedTime().minute,
                timePicker.getWakeTime().hour,
                timePicker.getWakeTime().minute,
                adapter!!.songList,
                cbPreferenceResumePlaying.isChecked,
                cbPreferenceVibrate.isChecked
            )
        }

        containerPreferenceNotifyMeBeforeBed.setOnClickListener {
            mPresenter.loadReminderOptions(realm)
        }

        btnDefaultAndClear.setOnClickListener {
            mPresenter.clearOrSetDefaultSong()
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

    private fun handleUpdate(bedTime: LocalTime, wakeTime: LocalTime) {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        tvBedTime.text = bedTime.format(formatter)
        tvWakeTime.text = wakeTime.format(formatter)

        val bedDate = bedTime.atDate(LocalDate.now())
        var wakeDate = wakeTime.atDate(LocalDate.now())
        if (bedDate >= wakeDate) wakeDate = wakeDate.plusDays(1)
        val duration = Duration.between(bedDate, wakeDate)
        val hours = duration.toHours()
        val minutes = duration.toMinutes() % 60
        tvHours.text = hours.toString()
        tvMins.text = minutes.toString()

        if (minutes > 0) llMins.visibility = View.VISIBLE else llMins.visibility = View.GONE
    }
}
