package com.pixelpioneer.moneymaster.ui.components

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pixelpioneer.moneymaster.data.model.Receipt
import com.pixelpioneer.moneymaster.data.services.ReceiptOCRService
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraPreview(
    onNavigateBack: () -> Unit,
    onReceiptProcessed: (Receipt) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val ocrService = remember { ReceiptOCRService() }
    
    var isProcessing by remember { mutableStateOf(false) }
    
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    
    LaunchedEffect(previewView) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()
        
        try {
            cameraProvider.unbindAll()
            
            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(previewView.surfaceProvider)
            
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (exc: Exception) {
            // Handle camera binding error
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )
        
        // Top Bar mit Zurück-Button
        TopAppBar(
            title = { Text("Kassenzettel scannen") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Zurück")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Black.copy(alpha = 0.5f),
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
            ),
            modifier = Modifier.align(Alignment.TopCenter)
        )
        
        // Capture Button
        FloatingActionButton(
            onClick = {
                if (!isProcessing) {
                    isProcessing = true
                    captureImage(imageCapture, context, ocrService) { receipt ->
                        isProcessing = false
                        onReceiptProcessed(receipt)
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            if (isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Icon(
                    Icons.Default.CameraAlt,
                    contentDescription = "Foto aufnehmen"
                )
            }
        }
    }
}

private fun captureImage(
    imageCapture: ImageCapture,
    context: Context,
    ocrService: ReceiptOCRService,
    onResult: (Receipt) -> Unit
) {
    val outputFile = File(context.filesDir, "receipt_${System.currentTimeMillis()}.jpg")
    val outputFileOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

    imageCapture.takePicture(
        outputFileOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val bitmap = BitmapFactory.decodeFile(outputFile.absolutePath)
                ocrService.recognizeReceipt(
                    bitmap,
                    onSuccess = { receipt ->
                        onResult(receipt)
                    },
                    onError = { exception ->
                        // Handle error
                        Toast.makeText(context, "Fehler beim Scannen: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(context, "Foto-Fehler: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    )
}