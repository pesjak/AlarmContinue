package primoz.com.alarmcontinue.views.alarm.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.utils.widget.ImageFilterButton
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_new_alarm.*
import primoz.com.alarmcontinue.R
import primoz.com.alarmcontinue.libraries.filepicker.Constant
import primoz.com.alarmcontinue.libraries.filepicker.activity.AudioPickActivity
import primoz.com.alarmcontinue.libraries.filepicker.activity.AudioPickActivity.IS_NEED_RECORDER
import primoz.com.alarmcontinue.libraries.filepicker.activity.BaseActivity.IS_NEED_FOLDER_LIST
import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.AudioFile

class NewAlarmFragment : Fragment() {

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            Constant.REQUEST_CODE_PICK_AUDIO -> if (resultCode == RESULT_OK) {
                val list = data?.getParcelableArrayListExtra<Parcelable>(Constant.RESULT_PICK_AUDIO) as ArrayList<AudioFile>
                val builder = StringBuilder()
                for (file in list) {
                    val path = file.path
                    builder.append(path + "\n")
                }
                Log.d("Songs", builder.toString())
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_alarm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        initOnClickListeners()
    }

    private fun initUI() {
        timePicker.setIs24HourView(true)
    }

    private fun initOnClickListeners() {
        tvRingtonesTitle.setOnClickListener {
            val intent3 = Intent(context, AudioPickActivity::class.java)
            intent3.putExtra(IS_NEED_RECORDER, true)
            intent3.putExtra(Constant.MAX_NUMBER, 9)
            intent3.putExtra(IS_NEED_FOLDER_LIST, true)
            startActivityForResult(intent3, Constant.REQUEST_CODE_PICK_AUDIO)
        }
    }


}
