package com.pixelpioneer.moneymaster.util

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.pixelpioneer.moneymaster.data.model.Receipt
import com.pixelpioneer.moneymaster.data.services.ReceiptOCRService
import com.pixelpioneer.moneymaster.ui.ReceiptResultActivity
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : ComponentActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var ocrService: ReceiptOCRService

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(this, "Kamera-Berechtigung erforderlich", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ocrService = ReceiptOCRService()
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Layout mit PreviewView erstellen
        previewView = PreviewView(this)
        setContentView(previewView)

        // Kamera-Berechtigung prüfen
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(previewView.surfaceProvider)

            val imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

                // Touch-to-capture
                previewView.setOnTouchListener { _, _ ->
                    captureImage(imageCapture)
                    true
                }

            } catch (exc: Exception) {
                Toast.makeText(this, "Kamera-Start fehlgeschlagen", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun captureImage(imageCapture: ImageCapture) {
        val outputFile = File(filesDir, "receipt_${System.currentTimeMillis()}.jpg")
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

        imageCapture.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val bitmap = BitmapFactory.decodeFile(outputFile.absolutePath)
                    processReceipt(bitmap)
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(this@CameraActivity, "Foto-Fehler: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun processReceipt(bitmap: Bitmap) {
        ocrService.recognizeReceipt(
            bitmap,
            onSuccess = { receipt ->
                runOnUiThread {
                    // Receipt-Daten verarbeiten
                    showReceiptResult(receipt)
                }
            },
            onError = { exception ->
                runOnUiThread {
                    Toast.makeText(this, "OCR-Fehler: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun showReceiptResult(receipt: Receipt) {
        val intent = Intent(this, ReceiptResultActivity::class.java)
        intent.putExtra("receipt", receipt)
        startActivity(intent)
        finish() // Schließe die CameraActivity
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun handleReceiptResult(receipt: Receipt) {
        if (receipt.items.isNotEmpty()) {
            val intent = Intent(this, ReceiptResultActivity::class.java)
            intent.putExtra("receipt", receipt)
            startActivity(intent)
        } else {
            showError("Keine Artikel erkannt. Bitte versuchen Sie es erneut.")
        }
    }

    private fun showLoading(show: Boolean) {
        // Implementiere Loading UI
    }

    private fun showError(message: String) {
        // Zeige Fehlermeldung
    }
}