package com.wajahat.jetfiles.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.wajahat.jetfiles.JetFilesApplication
import com.wajahat.jetfiles.ui.utils.DialogUtils
import com.wajahat.jetfiles.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (hasPermissions()) {
            renderComposeView()
        } else {
            requestPermission()
        }
    }

    /**
     * Before rendering the compose view, we make sure that we have the required storage permissions. Depending upon
     * the current permission state, if it's already granted then render the compose view, otherwise this method
     * will be called as soon as the permission is granted
     * */
    private fun renderComposeView() {
        val appContainer = (application as JetFilesApplication).container
        setContent {
            JetFilesApp(appContainer)
        }
    }

    /**
     * Permission result receiver after user has completed the specified action.
     * Called only for API level >= 30
     * */
    @RequiresApi(Build.VERSION_CODES.R)
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (Environment.isExternalStorageManager()) {
            renderComposeView()
        } else {
            showPermissionAllowDialog()
        }
    }

    /**
     * Show the request permission dialog to access external storage. It is a non-cancelable dialog
     * */
    private fun showPermissionAllowDialog() {
        DialogUtils.showAlertDialog(
            this,
            R.string.permission_required_title,
            R.string.permission_required_message,
            R.string.permission_required_button
        ) {
            requestPermission()
        }
    }

    /**
     * To access all the files, from API level 30, there is a special permission called MANAGE_EXTERNAL_STORAGE.
     * Since this is highly sensitive permission, Android does not allow requesting it in the traditional way
     * that we use to grant the other permissions. We need to explicitly take user to the Settings screen and let
     * them toggle the permission button to allow accessing the files.
     * */
    private fun requestPermission() {
        if (isAndroid11OrAbove()) {
            try {
                // We first try to open our application's detail screen in the Settings application via
                // package name.
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(String.format("package:%s", applicationContext.packageName))
                resultLauncher.launch(intent)
            } catch (e: Exception) {
                // If for some reason, Settings screen can't locate our app in the list, just open the Files Access
                // screen and let user find our app
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                resultLauncher.launch(intent)
            }
        } else {
            // For API level < 30, handle the permission in the normal way
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            STORAGE_PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty()) {
                val writeStoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED
                // Make sure both the permissions are given
                if (writeStoragePermission) {
                    renderComposeView()
                } else {
                    showPermissionAllowDialog()
                }
            }
        }
    }

    private fun isAndroid11OrAbove() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

    private fun hasPermissions(): Boolean {
        // Check if the API level is >= 30.
        return if (isAndroid11OrAbove()) {
            Environment.isExternalStorageManager()
        }
        // If the API level is < 30, check the permissions in the traditional way.
        else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private const val STORAGE_PERMISSION_REQUEST_CODE = 101
    }
}