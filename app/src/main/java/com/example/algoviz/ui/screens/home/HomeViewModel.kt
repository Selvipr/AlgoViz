package com.example.algoviz.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algoviz.domain.model.User
import com.example.algoviz.domain.repository.AuthRepository
import com.example.algoviz.domain.repository.UserRepository
import com.example.algoviz.utils.ErrorSanitizer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val user: User) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { HomeUiState.Loading }
            
            try {
                val currentUser = authRepository.getCurrentUser()
                if (currentUser != null) {
                    userRepository.getUserProfileFlow(currentUser.id).collect { profileResult ->
                        profileResult.onSuccess { userProfile ->
                            _uiState.update { HomeUiState.Success(userProfile) }
                        }.onFailure { exception ->
                            _uiState.update { HomeUiState.Error(ErrorSanitizer.sanitize(exception as? Exception)) }
                        }
                    }
                } else {
                    _uiState.update { HomeUiState.Error("User not logged in") }
                }
            } catch (e: Exception) {
                _uiState.update { HomeUiState.Error(ErrorSanitizer.sanitize(e)) }
            }
        }
    }
}
