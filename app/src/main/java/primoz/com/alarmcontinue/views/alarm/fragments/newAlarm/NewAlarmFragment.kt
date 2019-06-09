package primoz.com.alarmcontinue.views.alarm.fragments.newAlarm

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.media.RingtoneManager
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_new_alarm.*
import primoz.com.alarmcontinue.R
import primoz.com.alarmcontinue.libraries.filepicker.Constant
import primoz.com.alarmcontinue.libraries.filepicker.activity.AudioPickActivity
import primoz.com.alarmcontinue.libraries.filepicker.activity.BaseActivity.Companion.IS_NEED_FOLDER_LIST
import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.AudioFile
import primoz.com.alarmcontinue.views.alarm.fragments.newAlarm.adapters.SelectedSongsRecyclerViewAdapter

class NewAlarmFragment : Fragment() {

    private var adapter: SelectedSongsRecyclerViewAdapter? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            Constant.REQUEST_CODE_PICK_AUDIO -> if (resultCode == RESULT_OK) {
                //val builder = StringBuilder()
                //for (file in list) {
                //    val path = file.path
                //    builder.append(path + "\n")
                //}
                //Log.d("Songs", builder.toString())
                adapter?.songList =
                    data?.getParcelableArrayListExtra<Parcelable>(Constant.RESULT_PICK_AUDIO) as ArrayList<AudioFile>

            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_alarm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        initRecyclerView()
        initOnClickListeners()
    }

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
    }

    /*
    Private
     */

    private fun initRecyclerView() {

        //Get Default Alarm
        val alarmTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val ringtoneAlarm = RingtoneManager.getRingtone(context, alarmTone)
        var defaultRingtone = AudioFile()
        defaultRingtone.name = ringtoneAlarm.getTitle(context)
        defaultRingtone.path = alarmTone.path

        val linearLayoutManager = LinearLayoutManager(context)
        adapter = SelectedSongsRecyclerViewAdapter()
        adapter?.songList = mutableListOf(defaultRingtone)
        rvRingtones.layoutManager = linearLayoutManager
        rvRingtones.adapter = adapter
    }
}
