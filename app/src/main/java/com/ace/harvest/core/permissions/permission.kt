package com.ace.harvest.core.permissions

import android.Manifest
import android.app.Activity
import android.content.IntentSender
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume

class PermissionManager(private val activity: ComponentActivity) {

    private val permissionMutex = Mutex()
    private var permissionContinuation: CancellableContinuation<Boolean>? = null
    private val requestPermissionLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            permissionContinuation?.resume(isGranted)
            permissionContinuation = null
        }

    // A separate Mutex for location settings requests.
    private val settingsMutex = Mutex()
    private var settingsContinuation: CancellableContinuation<Boolean>? = null
    private val settingResultLauncher =
        activity.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
            val isSuccess = activityResult.resultCode == Activity.RESULT_OK
            settingsContinuation?.resume(isSuccess)
            settingsContinuation = null
        }

    suspend fun requestLocationPermission(): Boolean = permissionMutex.withLock {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return@withLock true
        }

        return@withLock suspendCancellableCoroutine { continuation ->
            // Store the continuation to be resumed by the launcher's callback.
            this.permissionContinuation = continuation
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

            // Clean up if the coroutine is cancelled.
            continuation.invokeOnCancellation {
                this.permissionContinuation = null
            }
        }
    }

    suspend fun requestLocationSettings(): Boolean = settingsMutex.withLock {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000L).build()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(activity)
        val task = client.checkLocationSettings(builder.build())

        // Create a continuation that will be resumed by the task listeners or the launcher callback.
        return@withLock suspendCancellableCoroutine { continuation ->
            // If settings are already enabled, resume immediately.
            task.addOnSuccessListener {
                if (continuation.isActive) {
                    continuation.resume(true)
                }
            }

            task.addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        // Store continuation to be resumed by the launcher.
                        this.settingsContinuation = continuation
                        val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                        settingResultLauncher.launch(intentSenderRequest)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // If we can't even launch the resolution activity, fail.
                        if (continuation.isActive) {
                            continuation.resume(false)
                        }
                    }
                } else {
                    // For non-resolvable exceptions, fail.
                    if (continuation.isActive) {
                        continuation.resume(false)
                    }
                }
            }

            // Clean up if the coroutine is cancelled.
            continuation.invokeOnCancellation {
                this.settingsContinuation = null
            }
        }
    }
}
