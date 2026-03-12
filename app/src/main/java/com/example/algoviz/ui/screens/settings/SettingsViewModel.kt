package com.example.algoviz.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algoviz.domain.repository.AppTheme
import com.example.algoviz.domain.repository.AuthRepository
import com.example.algoviz.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val theme: AppTheme = AppTheme.SYSTEM,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSignedOut: Boolean = false,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        // Observe theme changes from DataStore
        viewModelScope.launch {
            settingsRepository.theme
                .catch { e ->
                    _uiState.value = _uiState.value.copy(error = e.message)
                }
                .collect { theme ->
                    _uiState.value = _uiState.value.copy(theme = theme)
                }
        }
    }

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            try {
                settingsRepository.setTheme(theme)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun signOut() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                authRepository.signOut()
                _uiState.value = _uiState.value.copy(isLoading = false, isSignedOut = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to sign out"
                )
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = "Account deletion not fully implemented by backend. Please contact support.")
            // Real implementation would call a backend function to securely wipe data + auth
            // authRepository.deleteUser()
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }
}
