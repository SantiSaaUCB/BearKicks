package com.bearkicks.app.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bearkicks.app.features.auth.domain.model.UserModel
import com.bearkicks.app.features.auth.domain.usecase.LoginUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    data object Idle : LoginUiState()
    data object Loading : LoginUiState()
    data class Success(val user: UserModel) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel(private val login: LoginUseCase) : ViewModel() {
    private val _state = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onLogin(email: String, password: String) {
        _state.value = LoginUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val res = login(email, password)
            _state.value = res.fold(
                onSuccess = { LoginUiState.Success(it) },
                onFailure = { LoginUiState.Error(it.message ?: "Error al iniciar sesión") }
            )
        }
    }
}
