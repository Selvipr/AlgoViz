package com.example.algoviz.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algoviz.domain.model.User
import com.example.algoviz.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.algoviz.data.local.SecureStorage
import io.github.jan.supabase.SupabaseClient
import com.example.algoviz.utils.ErrorSanitizer

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    val supabaseClient: SupabaseClient,
    val secureStorage: SecureStorage
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.sessionStatus.collect { isAuthenticated ->
                if (isAuthenticated) {
                    val user = authRepository.getCurrentUser()
                    if (user != null) {
                        _authState.value = AuthState.Authenticated(user)
                    }
                } else if (_authState.value !is AuthState.Loading) {
                    _authState.value = AuthState.Idle
                }
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    fun checkAuthStatus() {
        viewModelScope.launch {
            if (authRepository.isLoggedIn()) {
                val user = authRepository.getCurrentUser()
                if (user != null) {
                    _authState.value = AuthState.Authenticated(user)
                    return@launch
                }
            }
            _authState.value = AuthState.Idle
        }
    }

    fun attemptBiometricLogin() {
        if (secureStorage.hasCredentials()) {
            val email = secureStorage.getEmail() ?: return
            val password = secureStorage.getPassword() ?: return
            signIn(email, password)
        }
    }

    fun signIn(email: String, password: String, enableBiometrics: Boolean = false) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.signIn(email, password)
            result.onSuccess { user ->
                if (enableBiometrics) {
                    secureStorage.saveBiometricCredentials(email, password)
                }
                _authState.value = AuthState.Authenticated(user)
            }.onFailure { e ->
                _authState.value = AuthState.Error(ErrorSanitizer.sanitize(e as? Exception))
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.signInWithGoogle(idToken)
            result.onFailure { e ->
                _authState.value = AuthState.Error(ErrorSanitizer.sanitize(e as? Exception))
            }
        }
    }

    fun signUp(email: String, password: String, username: String, enableBiometrics: Boolean = false) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.signUp(email, password, username)
            result.onSuccess { user ->
                if (enableBiometrics) {
                    secureStorage.saveBiometricCredentials(email, password)
                }
                _authState.value = AuthState.Authenticated(user)
            }.onFailure { e ->
                _authState.value = AuthState.Error(ErrorSanitizer.sanitize(e as? Exception))
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.signOut()
            secureStorage.clearCredentials()
            _authState.value = AuthState.Idle
        }
    }

    fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }
}
