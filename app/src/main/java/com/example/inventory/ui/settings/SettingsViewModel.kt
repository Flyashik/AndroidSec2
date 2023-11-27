package com.example.inventory.ui.settings

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.inventory.ui.item.ItemUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel: ViewModel() {
    fun initUiState() {
        _uiState.value = SettingsUiState(
            shipperName = "",
            shipperEmail = "",
            shipperPhone = "",
            enableDefaultSettings = false,
            hideSensitiveData = false,
            disableSharing = false,
        )
    }

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(shipperName = value)
    }

    fun onPhoneChange(value: String) {
        _uiState.value = _uiState.value.copy(shipperPhone = value)
    }

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(shipperEmail = value)
    }

    fun onEnableDefaultSettingsChage(value: Boolean) {
        _uiState.value = _uiState.value.copy(enableDefaultSettings = value)
    }

    fun onHideSensitiveDataChange(value: Boolean) {
        _uiState.value = _uiState.value.copy(hideSensitiveData = value)
    }

    fun onDisableSharingChange(value: Boolean) {
        _uiState.value = _uiState.value.copy(disableSharing = value)
    }
}

data class SettingsUiState(
    val shipperName: String = "",
    val shipperPhone: String = "",
    val shipperEmail: String = "",
    val enableDefaultSettings: Boolean = false,
    val hideSensitiveData: Boolean = false,
    val disableSharing: Boolean = false,
)