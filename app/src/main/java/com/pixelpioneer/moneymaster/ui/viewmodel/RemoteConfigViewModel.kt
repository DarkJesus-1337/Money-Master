package com.pixelpioneer.moneymaster.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelpioneer.moneymaster.data.services.RemoteConfigManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RemoteConfigState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val debugInfo: Map<String, Any> = emptyMap()
)

class RemoteConfigViewModel(
    private val remoteConfigManager: RemoteConfigManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RemoteConfigState())
    val uiState: StateFlow<RemoteConfigState> = _uiState.asStateFlow()
    
    init {
        loadRemoteConfig()
    }
    
    fun loadRemoteConfig() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val success = remoteConfigManager.fetchAndActivate()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = success,
                    debugInfo = remoteConfigManager.getDebugInfo()
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = false,
                    errorMessage = e.message
                )
            }
        }
    }
    
    fun getOcrApiKey(): String {
        return remoteConfigManager.getOcrSpaceApiKey()
    }
    
    fun getCoinCapApiKey(): String {
        return remoteConfigManager.getCoinCapApiKey()
    }
    
    fun hasKey(key: String): Boolean {
        return remoteConfigManager.hasKey(key)
    }
    
    fun getAllKeys(): Set<String> {
        return remoteConfigManager.getAllKeys()
    }
    
    fun refreshConfig() {
        loadRemoteConfig()
    }
}