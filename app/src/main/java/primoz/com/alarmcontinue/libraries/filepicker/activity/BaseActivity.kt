package primoz.com.alarmcontinue.libraries.filepicker.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.x_filepicker_activity_audio_pick.*
import primoz.com.alarmcontinue.R
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

abstract class BaseActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    var isNeedFolderList: Boolean = false

    abstract fun permissionGranted()

    /*
    LifeCycle
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isNeedFolderList = intent.getBooleanExtra(IS_NEED_FOLDER_LIST, false)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        readExternalStorage()
        ivBack.setOnClickListener { finish() }
        llFolder.visibility = if (isNeedFolderList) View.VISIBLE else View.GONE
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    /**
     * Read external storage file
     */
    @AfterPermissionGranted(RC_READ_EXTERNAL_STORAGE)
    private fun readExternalStorage() {
        val isGranted = EasyPermissions.hasPermissions(this, "android.permission.READ_EXTERNAL_STORAGE")
        if (isGranted) {
            permissionGranted()
        } else {
            EasyPermissions.requestPermissions(
                this, getString(R.string.vw_rationale_storage),
                RC_READ_EXTERNAL_STORAGE, "android.permission.READ_EXTERNAL_STORAGE"
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        permissionGranted()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        // If Permission permanently denied, ask user again
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.
            if (EasyPermissions.hasPermissions(this, "android.permission.READ_EXTERNAL_STORAGE")) {
                permissionGranted()
            } else {
                finish()
            }
        }
    }

    companion object {
        private const val RC_READ_EXTERNAL_STORAGE = 123
        const val IS_NEED_FOLDER_LIST = "isNeedFolderList"
    }
}
