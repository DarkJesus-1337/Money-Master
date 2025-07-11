package com.pixelpioneer.moneymaster.data.services

import android.content.Context
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.pixelpioneer.moneymaster.BuildConfig
import com.pixelpioneer.moneymaster.R
import kotlinx.coroutines.tasks.await

class RemoteConfigManager(private val context: Context) {
    
    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
    
    companion object {
        private const val TAG = "RemoteConfigManager"
        private const val CACHE_EXPIRATION = 3600L // 1 Stunde
        
        // Keys für Remote Config
        const val OCR_SPACE_API_KEY = "ocr_space_api_key"
        const val COINCAP_API_KEY = "coincap_api_key"
    }
    
    init {
        initializeRemoteConfig()
    }
    
    private fun initializeRemoteConfig() {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0 else CACHE_EXPIRATION
        }
        
        remoteConfig.setConfigSettingsAsync(configSettings)
        
        // Setze Default-Werte (Fallback)
        remoteConfig.setDefaultsAsync(
            mapOf(
                OCR_SPACE_API_KEY to BuildConfig.OCR_SPACE_API_KEY,
                COINCAP_API_KEY to BuildConfig.COINCAP_API_KEY
            )
        )
    }
    
    /**
     * Lädt die Remote Config Werte
     */
    suspend fun fetchAndActivate(): Boolean {
        return try {
            val fetchResult = remoteConfig.fetchAndActivate().await()
            Log.d(TAG, "Remote config fetch successful: $fetchResult")
            fetchResult
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching remote config", e)
            false
        }
    }
    
    /**
     * Holt den OCR Space API Key
     */
    fun getOcrSpaceApiKey(): String {
        return remoteConfig.getString(OCR_SPACE_API_KEY).takeIf { it.isNotEmpty() }
            ?: BuildConfig.OCR_SPACE_API_KEY
    }
    
    /**
     * Holt den CoinCap API Key
     */
    fun getCoinCapApiKey(): String {
        return remoteConfig.getString(COINCAP_API_KEY).takeIf { it.isNotEmpty() }
            ?: BuildConfig.COINCAP_API_KEY
    }
    
    /**
     * Prüft ob ein spezifischer Key verfügbar ist
     */
    fun hasKey(key: String): Boolean {
        return remoteConfig.getString(key).isNotEmpty()
    }
    
    /**
     * Lädt alle verfügbaren Keys
     */
    fun getAllKeys(): Set<String> {
        return remoteConfig.all.keys
    }
    
    /**
     * Gibt Debug-Informationen aus
     */
    fun getDebugInfo(): Map<String, Any> {
        return mapOf(
            "lastFetchTime" to remoteConfig.info.fetchTimeMillis,
            "lastFetchStatus" to remoteConfig.info.lastFetchStatus,
            "configSettings" to remoteConfig.info.configSettings.minimumFetchIntervalInSeconds,
            "availableKeys" to getAllKeys()
        )
    }
}