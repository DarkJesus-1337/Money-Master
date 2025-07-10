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
        CameraPreview(
            onNavigateBack = onNavigateBack,
            onReceiptProcessed = onReceiptCaptured
        )
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Kamera-Berechtigung erforderlich")
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }
            ) {
                Text("Berechtigung erteilen")
            }
        }
    }
}