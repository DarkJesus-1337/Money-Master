package com.pixelpioneer.moneymaster.ui.screens.camera

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.pixelpioneer.moneymaster.data.model.Receipt
import com.pixelpioneer.moneymaster.ui.components.CameraPreview


@Composable
fun CameraScreen(
    onNavigateBack: () -> Unit,
    onReceiptCaptured: (Receipt) -> Unit
) {
    val context = LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasCameraPermission) {
        EnhancedCameraPreview(
            onNavigateBack = onNavigateBack,
            onReceiptProcessed = onReceiptCaptured
        )
    } else {
        PermissionRequestScreen(
            onRequestPermission = {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            },
            onNavigateBack = onNavigateBack
        )
    }
}

@Composable
private fun PermissionRequestScreen(
    onRequestPermission: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Kamera-Berechtigung erforderlich",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Um Kassenbons zu scannen, benötigt die App Zugriff auf Ihre Kamera.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Abbrechen")
                    }

                    Button(
                        onClick = onRequestPermission,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Berechtigung erteilen")
                    }
                }
            }
        }
    }
}