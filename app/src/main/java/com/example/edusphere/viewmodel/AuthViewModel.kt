package com.example.edusphere.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusphere.data.repository.AuthRepository
import com.example.edusphere.navigation.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val isLoading: Boolean = false,
    val role: UserRole? = null,
    val error: String? = null,
    val isAuthenticated: Boolean = false
)

class AuthViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)

            repository.login(email, password)
                .onSuccess { role ->
                    _authState.value = AuthState(
                        isLoading = false,
                        role = role,
                        isAuthenticated = true
                    )
                }
                .onFailure { exception ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Login Failed"
                    )
                }
        }
    }

    fun register(email: String, password: String, name: String, role: UserRole) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)

            repository.register(email, password, name, role)
                .onSuccess {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isAuthenticated = true
                    )
                }
                .onFailure { exception ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Registration Failed"
                    )
                }
        }
    }

    fun logout() {
        _authState.value = AuthState()
        repository.logout()
    }
}