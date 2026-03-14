package com.example.algoviz.ui.screens.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algoviz.domain.model.User
import com.example.algoviz.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PublicProfileUiState {
    object Loading : PublicProfileUiState()
    data class Success(val user: User) : PublicProfileUiState()
    data class Error(val message: String) : PublicProfileUiState()
}

@HiltViewModel
class PublicProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: String = checkNotNull(savedStateHandle["userId"])

    private val _uiState = MutableStateFlow<PublicProfileUiState>(PublicProfileUiState.Loading)
    val uiState: StateFlow<PublicProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = PublicProfileUiState.Loading
            userRepository.getUserProfile(userId)
                .onSuccess { user ->
                    _uiState.value = PublicProfileUiState.Success(user)
                }
                .onFailure { e ->
                    _uiState.value = PublicProfileUiState.Error(e.message ?: "Failed to load profile")
                }
        }
    }
}
