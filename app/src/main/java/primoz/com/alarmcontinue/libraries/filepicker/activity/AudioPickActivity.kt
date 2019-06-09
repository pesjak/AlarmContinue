package primoz.com.alarmcontinue.libraries.filepicker.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.x_filepicker_activity_audio_pick.*
import kotlinx.android.synthetic.main.x_filepicker_layout_toolbar.*
import primoz.com.alarmcontinue.R
import primoz.com.alarmcontinue.libraries.filepicker.Constant
import primoz.com.alarmcontinue.libraries.filepicker.adapter.AudioPickAdapter
import primoz.com.alarmcontinue.libraries.filepicker.adapter.OnSelectStateListener
import primoz.com.alarmcontinue.libraries.filepicker.filter.FileFilter
import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.AudioFile
import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.Directory
import java.io.File


class AudioPickActivity : BaseActivity() {

    private var mMaxNumber: Int = 0
    private var mCurrentNumber = 0
    private var mAdapter: AudioPickAdapter? = null
    private var isTakenAutoSelected: Boolean = false
    private val mSelectedList = ArrayList<AudioFile>()
    private var mAll: List<Directory<AudioFile>>? = null
    private var mAudioPath: String? = null
    private val fileDialog by lazy {
        AlertDialog.Builder(this, R.style.AlertDialogCustom)
    }

    override fun permissionGranted() {
        loadData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.x_filepicker_activity_audio_pick)

        mMaxNumber = intent.getIntExtra(Constant.MAX_NUMBER, DEFAULT_MAX_NUMBER)
        isTakenAutoSelected = intent.getBooleanExtra(IS_TAKEN_AUTO_SELECTED, true)
        initView()
    }

    private fun initView() {
        tvCount.text = "$mCurrentNumber/$mMaxNumber"

        val layoutManager = LinearLayoutManager(this)
        rvAudioPick.layoutManager = layoutManager
        rvAudioPick.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        mAdapter = AudioPickAdapter(this, mMaxNumber)
        rvAudioPick.adapter = mAdapter

        mAdapter?.setOnSelectStateListener(object : OnSelectStateListener<AudioFile> {
            override fun OnSelectStateChanged(state: Boolean, file: AudioFile) {
                if (state) {
                    mSelectedList.add(file)
                    mCurrentNumber++
                } else {
                    mSelectedList.remove(file)
                    mCurrentNumber--
                }
                tvCount?.text = "$mCurrentNumber/$mMaxNumber"
            }
        })

        tvDone.setOnClickListener {
            val intent = Intent()
            intent.putParcelableArrayListExtra(Constant.RESULT_PICK_AUDIO, mSelectedList)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        if (isNeedFolderList) {
            llFolder.visibility = View.VISIBLE
            llFolder.setOnClickListener {
                fileDialog.setTitle(getString(R.string.pick_a_folder))
                fileDialog.show()
            }
            tvFolder.text = resources.getString(R.string.all)
        }
    }

    private fun loadData() {
        FileFilter.getAudios(this) { directories ->
            // Refresh folder list
            if (isNeedFolderList) {
                val list = ArrayList<Directory<*>>()
                val all = Directory<String>()
                all.name = resources.getString(R.string.all)
                list.add(all)
                list.addAll(directories)

                setFoldersToBuilder(list)
            }

            mAll = directories
            refreshData(directories)

        }
    }

    private fun setFoldersToBuilder(listDirectories: ArrayList<Directory<*>>) {
        val tempStringDirectoryArray = arrayListOf<String>()
        for (directory in listDirectories) {
            tempStringDirectoryArray.add(directory.name)
        }
        val directoryCharSequenceList =
            tempStringDirectoryArray.toArray(arrayOfNulls<CharSequence>(tempStringDirectoryArray.size))
        fileDialog.setItems(directoryCharSequenceList) { dialog, which ->
            val directory = listDirectories[which]
            tvFolder.text = directory.name
            mAll?.let {
                if (TextUtils.isEmpty(directory.path)) { //All
                    refreshData(it)
                } else {
                    for (dir in it) {
                        if (dir.path == directory.path) {
                            val list = ArrayList<Directory<AudioFile>>()
                            list.add(dir)
                            refreshData(list)
                            break
                        }
                    }
                }
            }
        }
    }

    private fun refreshData(directories: List<Directory<AudioFile>>?) {
        directories ?: return
        var tryToFindTaken = isTakenAutoSelected

        // if auto-select taken file is enabled, make sure requirements are met
        if (tryToFindTaken && !TextUtils.isEmpty(mAudioPath)) {
            val takenFile = File(mAudioPath!!)
            tryToFindTaken =
                !mAdapter!!.isUpToMax && takenFile.exists() // try to select taken file only if max isn't reached and the file exists
        }

        val list = ArrayList<AudioFile>()
        for (directory in directories) {
            list.addAll(directory.files)

            // auto-select taken file?
            if (tryToFindTaken) {
                tryToFindTaken = findAndAddTaken(directory.files)   // if taken file was found, we're done
            }
        }

        for (file in mSelectedList) {
            val index = list.indexOf(file)
            if (index != -1) {
                list[index].isSelected = true
            }
        }
        mAdapter?.refresh(list)
    }

    private fun findAndAddTaken(list: List<AudioFile>): Boolean {
        for (audioFile in list) {
            if (audioFile.path == mAudioPath) {
                mSelectedList.add(audioFile)
                mCurrentNumber++
                mAdapter?.setCurrentNumber(mCurrentNumber)
                tvCount?.text = "$mCurrentNumber/$mMaxNumber"
                return true   // taken file was found and added
            }
        }
        return false // taken file wasn't found
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constant.REQUEST_CODE_TAKE_AUDIO -> if (resultCode == Activity.RESULT_OK) {
                data?.data?.let {
                    mAudioPath = it.path
                }
                loadData()
            }
        }
    }

    companion object {
        const val IS_TAKEN_AUTO_SELECTED = "IsTakenAutoSelected"
        const val DEFAULT_MAX_NUMBER = 9
    }
}
