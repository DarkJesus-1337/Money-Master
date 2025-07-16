package com.pixelpioneer.moneymaster.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.pixelpioneer.moneymaster.data.services.AppUpdateManager

@Composable
fun UpdateDialog(
    updateState: AppUpdateManager.UpdateState,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    when (updateState) {
        is AppUpdateManager.UpdateState.Checking -> {
            Dialog(onDismissRequest = onDismiss) {
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Suche nach Updates...")
                    }
                }
            }
        }
        
        is AppUpdateManager.UpdateState.Downloading -> {
            Dialog(onDismissRequest = { }) {
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val progress = if (updateState.total > 0) {
                            updateState.downloaded.toFloat() / updateState.total.toFloat()
                        } else 0f
                        
                        LinearProgressIndicator(
                            progress = progress,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Update wird heruntergeladen...")
                        Text("${(progress * 100).toInt()}%")
                    }
                }
            }
        }
        
        is AppUpdateManager.UpdateState.Error -> {
            Dialog(onDismissRequest = onDismiss) {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Update Fehler")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(updateState.message)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onDismiss) {
                            Text("OK")
                        }
                    }
                }
            }
        }
        
        is AppUpdateManager.UpdateState.NoUpdate -> {
            Dialog(onDismissRequest = onDismiss) {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Keine Updates verfügbar")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onDismiss) {
                            Text("OK")
                        }
                    }
                }
            }
        }
        
        else -> { /* Idle oder Success - kein Dialog */ }
    }
}