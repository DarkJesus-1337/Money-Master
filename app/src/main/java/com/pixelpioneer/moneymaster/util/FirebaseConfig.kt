package com.pixelpioneer.moneymaster.util

import android.app.Activity
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.pixelpioneer.moneymaster.MoneyMasterApplication
import kotlinx.coroutines.launch

/**
 * Extension für Activities um Remote Config zu laden
 */
fun Activity.loadRemoteConfig(
    onSuccess: (() -> Unit)? = null,
    onError: ((Exception) -> Unit)? = null
) {
    val app = application as MoneyMasterApplication

    if (this is LifecycleOwner) {
        lifecycleScope.launch {
            try {
                val success = app.remoteConfigManager.fetchAndActivate()
                if (success) {
                    onSuccess?.invoke()
                } else {
                    onError?.invoke(Exception("Failed to fetch remote config"))
                }
            } catch (e: Exception) {
                onError?.invoke(e)
            }
        }
    }
}

/**
 * Extension für einfache Toast-Benachrichtigungen
 */
fun Activity.showRemoteConfigStatus() {
    val app = application as MoneyMasterApplication

    if (this is LifecycleOwner) {
        lifecycleScope.launch {
            try {
                val success = app.remoteConfigManager.fetchAndActivate()
                val message = if (success) {
                    "Remote Config erfolgreich geladen"
                } else {
                    "Remote Config konnte nicht geladen werden - verwende lokale Werte"
                }

                runOnUiThread {
                    Toast.makeText(this@showRemoteConfigStatus, message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@showRemoteConfigStatus,
                        "Fehler beim Laden der Remote Config: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}