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
import com.example.algoviz.utils.ErrorSanitizer

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
                    userRepository.getUserProfileFlow(currentUser.id).collect { profileResult ->
                        profileResult.onSuccess { userProfile ->
                            _uiState.update { ProfileUiState.Success(userProfile) }
                        }.onFailure { exception ->
                            _uiState.update { ProfileUiState.Error(ErrorSanitizer.sanitize(exception as? Exception)) }
                        }
                    }
                } else {
                    _uiState.update { ProfileUiState.Error("User not logged in") }
                }
            } catch (e: Exception) {
                _uiState.update { ProfileUiState.Error(ErrorSanitizer.sanitize(e)) }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

    fun updateProfile(updatedUser: User) {
        viewModelScope.launch {
            _uiState.update { ProfileUiState.Loading }
            try {
                val result = userRepository.updateUserProfile(updatedUser)
                result.onSuccess { user ->
                    _uiState.update { ProfileUiState.Success(user) }
                }.onFailure { e ->
                    _uiState.update { ProfileUiState.Error(ErrorSanitizer.sanitize(e as? Exception)) }
                }
            } catch (e: Exception) {
               _uiState.update { ProfileUiState.Error(ErrorSanitizer.sanitize(e)) }
            }
        }
    }
}
