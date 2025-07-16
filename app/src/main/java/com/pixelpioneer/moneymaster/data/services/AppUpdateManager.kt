package com.pixelpioneer.moneymaster.data.services

import android.app.Activity
import android.util.Log
import com.google.firebase.appdistribution.FirebaseAppDistribution
import com.google.firebase.appdistribution.FirebaseAppDistributionException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AppUpdateManager {
    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState

    fun checkForUpdates(activity: Activity) {
        _updateState.value = UpdateState.Checking

        FirebaseAppDistribution.getInstance().updateIfNewReleaseAvailable()
            .addOnProgressListener { updateProgress ->
                _updateState.value = UpdateState.Downloading(updateProgress.apkBytesDownloaded, updateProgress.apkFileTotalBytes)
            }
            .addOnSuccessListener {
                _updateState.value = UpdateState.Success
                Log.d("AppUpdate", "Update erfolgreich installiert")
            }
            .addOnFailureListener { exception ->
                when (exception) {
                    is FirebaseAppDistributionException -> {
                        when (exception.errorCode) {
                            FirebaseAppDistributionException.Status.NOT_IMPLEMENTED -> {
                                Log.d("AppUpdate", "Keine Updates verfügbar")
                                _updateState.value = UpdateState.NoUpdate
                            }
                            else -> {
                                Log.e("AppUpdate", "Update fehlgeschlagen: ${exception.message}")
                                _updateState.value = UpdateState.Error(exception.message ?: "Unbekannter Fehler")
                            }
                        }
                    }
                    else -> {
                        Log.e("AppUpdate", "Update fehlgeschlagen: ${exception.message}")
                        _updateState.value = UpdateState.Error(exception.message ?: "Unbekannter Fehler")
                    }
                }
            }
    }

    sealed class UpdateState {
        object Idle : UpdateState()
        object Checking : UpdateState()
        data class Downloading(val downloaded: Long, val total: Long) : UpdateState()
        object Success : UpdateState()
        object NoUpdate : UpdateState()
        data class Error(val message: String) : UpdateState()
    }
}