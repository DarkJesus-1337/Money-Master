package com.pixelpioneer.moneymaster.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelpioneer.moneymaster.data.services.RemoteConfigManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

data class RemoteConfigState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val debugInfo: Map<String, Any> = emptyMap()
)

/**
 * ViewModel for managing remote configuration settings.
 *
 * Handles loading and updating remote config values,
 * and provides UI state for loading, success, and error states.
 *
 * @property remoteConfigManager Manager for remote config operations.
 */
class RemoteConfigViewModel(
    private val remoteConfigManager: RemoteConfigManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RemoteConfigState())

    init {
        loadRemoteConfig()
    }

    private fun loadRemoteConfig() {
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
}