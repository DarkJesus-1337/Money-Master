package com.pixelpioneer.moneymaster.data.services

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CoinCapApiClient(private val remoteConfigManager: RemoteConfigManager) {

    private val authInterceptor = Interceptor { chain ->
        val original: Request = chain.request()
        val apiKey = remoteConfigManager.getCoinCapApiKey()
        val request = original.newBuilder()
            .addHeader("Authorization", "Bearer $apiKey")
            .build()
        chain.proceed(request)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    val api: CoinCapApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://rest.coincap.io/v3/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CoinCapApiService::class.java)
    }
}