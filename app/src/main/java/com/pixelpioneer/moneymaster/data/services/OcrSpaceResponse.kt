package com.pixelpioneer.moneymaster.data.services

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import retrofit2.http.*

data class OcrSpaceResponse(
    val ParsedResults: List<ParsedResult>?,
    val OCRExitCode: Int,
    val IsErroredOnProcessing: Boolean,
    val ErrorMessage: String?,
    val ErrorDetails: String?
)

data class ParsedResult(
    val TextOverlay: TextOverlay?,
    val TextOrientation: String?,
    val FileParseExitCode: Int,
    val ParsedText: String?,
    val ErrorMessage: String?,
    val ErrorDetails: String?
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
        @Part("language") language: RequestBody = "ger".toRequestBody(MultipartBody.FORM),
        @Part("isOverlayRequired") overlay: RequestBody = "true".toRequestBody(MultipartBody.FORM),
        @Part("detectOrientation") orientation: RequestBody = "true".toRequestBody(MultipartBody.FORM),
        @Part("isTable") isTable: RequestBody = "true".toRequestBody(MultipartBody.FORM),
        @Part("scale") scale: RequestBody = "true".toRequestBody(MultipartBody.FORM),
        @Part image: MultipartBody.Part
    ): Response<OcrSpaceResponse>
}