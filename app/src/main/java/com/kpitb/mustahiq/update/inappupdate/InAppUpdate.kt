package com.kpitb.mustahiq.update.inappupdate
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.widget.Toast
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class InAppUpdate(
    private val appUpdateManager: AppUpdateManager,
    private val activity: Activity
) {
    private var updateType = AppUpdateType.IMMEDIATE

    fun checkForAppUpdates() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            val isUpdateAvailable = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed = when (updateType) {
                AppUpdateType.FLEXIBLE -> info.isFlexibleUpdateAllowed
                AppUpdateType.IMMEDIATE -> info.isImmediateUpdateAllowed
                else -> false
            }
            if (isUpdateAvailable && isUpdateAllowed) {
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    updateType,
                    activity,
                    123
                )
            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int) {
        if (requestCode == 123 && resultCode != RESULT_OK) {
            println("Something went wrong updating...")
        }
    }

    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        Toast.makeText(activity.applicationContext, "Downloading...", Toast.LENGTH_LONG).show()
        CoroutineScope(Dispatchers.Main).launch {
            delay(5000)
            appUpdateManager.completeUpdate()
        }
    }

    fun onCreate() {
        if (updateType == AppUpdateType.IMMEDIATE) {
            appUpdateManager.registerListener(installStateUpdatedListener)
        }
    }

    fun onResume() {
        if (updateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
                if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    appUpdateManager.startUpdateFlowForResult(
                        info,
                        updateType,
                        activity,
                        123
                    )
                }
            }
        }
    }

    fun onDestroy() {
        if (updateType == AppUpdateType.IMMEDIATE) {
            appUpdateManager.unregisterListener(installStateUpdatedListener)
        }
    }
}


