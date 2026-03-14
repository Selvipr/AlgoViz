package com.example.algoviz.ui.screens.arena

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algoviz.domain.model.User
import com.example.algoviz.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LeaderboardUiState {
    object Loading : LeaderboardUiState()
    data class Success(val topUsers: List<User>) : LeaderboardUiState()
    data class Error(val message: String) : LeaderboardUiState()
}

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LeaderboardUiState>(LeaderboardUiState.Loading)
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    init {
        fetchTopUsers()
    }

    private fun fetchTopUsers() {
        viewModelScope.launch {
            _uiState.value = LeaderboardUiState.Loading
            userRepository.getTopUsersFlow(limit = 100)
                .catch { e ->
                    _uiState.value = LeaderboardUiState.Error(e.message ?: "An unknown error occurred")
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { users ->
                            _uiState.value = LeaderboardUiState.Success(users)
                        },
                        onFailure = { e ->
                            _uiState.value = LeaderboardUiState.Error(e.message ?: "Failed to load leaderboard")
                        }
                    )
                }
        }
    }
}
