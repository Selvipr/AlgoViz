package com.example.algoviz.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algoviz.domain.model.User
import com.example.algoviz.domain.repository.AuthRepository
import com.example.algoviz.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val user: User) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { ProfileUiState.Loading }
            
            try {
                val currentUser = authRepository.getCurrentUser()
                if (currentUser != null) {
                    val profileResult = userRepository.getUserProfile(currentUser.id)
                    profileResult.onSuccess { userProfile ->
                        _uiState.update { ProfileUiState.Success(userProfile) }
                    }.onFailure { exception ->
                        _uiState.update { ProfileUiState.Error(exception.message ?: "Failed to load profile") }
                    }
                } else {
                    _uiState.update { ProfileUiState.Error("User not logged in") }
                }
            } catch (e: Exception) {
                _uiState.update { ProfileUiState.Error(e.message ?: "Unknown error occurred") }
            }
        }
    }
}
