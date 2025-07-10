package com.pixelpioneer.moneymaster.data.services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OcrSpaceApiClient {
    private const val BASE_URL = "https://api.ocr.space/"
    
    val apiService: OcrSpaceApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OcrSpaceApiService::class.java)
    }
}