package com.pixelpioneer.moneymaster.data.services

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

data class OcrSpaceResponse(
    val ParsedResults: List<ParsedResult>?,
    val OCRExitCode: Int,
    val IsErroredOnProcessing: Boolean,
    val ErrorMessage: Any?, // String oder Array
    val ErrorDetails: Any?  // String oder Array
)

data class ParsedResult(
    val TextOverlay: TextOverlay?,
    val TextOrientation: String?,
    val FileParseExitCode: Int,
    val ParsedText: String?,
    val ErrorMessage: Any?, // String oder Array
    val ErrorDetails: Any?  // String oder Array
)

data class TextOverlay(
    val Lines: List<Line>?
)

data class Line(
    val LineText: String?,
    val Words: List<Word>?
)

data class Word(
    val WordText: String?,
    val Left: Double,
    val Top: Double,
    val Height: Double,
    val Width: Double
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