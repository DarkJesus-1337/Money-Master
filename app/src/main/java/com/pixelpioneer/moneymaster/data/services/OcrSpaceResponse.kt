package com.pixelpioneer.moneymaster.data.services

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

data class OcrSpaceResponse(
    @SerializedName("ParsedResults")
    val parsedResults: List<ParsedResult>?,

    @SerializedName("OCRExitCode")
    val ocrExitCode: Int,

    @SerializedName("IsErroredOnProcessing")
    val isErroredOnProcessing: Boolean,

    @SerializedName("ErrorMessage")
    val errorMessage: Any?, // String oder Array

    @SerializedName("ErrorDetails")
    val errorDetails: Any?  // String oder Array
)

data class ParsedResult(
    @SerializedName("TextOverlay")
    val textOverlay: TextOverlay?,

    @SerializedName("TextOrientation")
    val textOrientation: String?,

    @SerializedName("FileParseExitCode")
    val fileParseExitCode: Int,

    @SerializedName("ParsedText")
    val parsedText: String?,

    @SerializedName("ErrorMessage")
    val errorMessage: Any?, // String oder Array

    @SerializedName("ErrorDetails")
    val errorDetails: Any?  // String oder Array
)

data class TextOverlay(
    @SerializedName("Lines")
    val lines: List<Line>?
)

data class Line(
    @SerializedName("LineText")
    val lineText: String?,

    @SerializedName("Words")
    val words: List<Word>?
)

data class Word(
    @SerializedName("WordText")
    val wordText: String?,

    @SerializedName("Left")
    val left: Double,

    @SerializedName("Top")
    val top: Double,

    @SerializedName("Height")
    val height: Double,

    @SerializedName("Width")
    val width: Double
)

interface OcrSpaceApiService {
    @Multipart
    @POST("parse/image")
    suspend fun parseImage(
        @Part("apikey") apiKey: RequestBody,
        @Part("language") language: RequestBody,
        @Part("isOverlayRequired") overlay: RequestBody,
        @Part("detectOrientation") orientation: RequestBody,
        @Part("isTable") isTable: RequestBody,
        @Part("scale") scale: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<OcrSpaceResponse>
}