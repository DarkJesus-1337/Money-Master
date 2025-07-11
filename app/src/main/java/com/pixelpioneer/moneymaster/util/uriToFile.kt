package com.pixelpioneer.moneymaster.util

import android.net.Uri
import java.io.File

fun uriToFile(uri: Uri, context: android.content.Context): File {
    val inputStream = context.contentResolver.openInputStream(uri)
    val tempFile = File.createTempFile("receipt", ".jpg", context.cacheDir)
    inputStream?.use { input ->
        tempFile.outputStream().use { output ->
            input.copyTo(output)
        }
    }
    return tempFile
}