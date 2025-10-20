package com.bearkicks.app.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bearkicks.app.features.auth.domain.model.UserModel
import com.bearkicks.app.features.auth.domain.usecase.GetCurrentUserUseCase
import com.bearkicks.app.features.auth.domain.usecase.LogoutUseCase
import com.bearkicks.app.features.auth.domain.usecase.ObserveAuthStateUseCase
import com.bearkicks.app.features.auth.domain.usecase.UpdateProfileUseCase
import com.bearkicks.app.features.auth.domain.usecase.UpdateProfilePhotoUseCase
import com.bearkicks.app.features.cart.domain.usecase.ClearOrdersUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileUiState {
    data object Loading : ProfileUiState()
    data class Authenticated(val user: UserModel) : ProfileUiState()
    data object LoggedOut : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

class ProfileViewModel(
    observeAuth: ObserveAuthStateUseCase,
    private val getCurrent: GetCurrentUserUseCase,
    private val logout: LogoutUseCase,
    private val clearOrders: ClearOrdersUseCase,
    private val updateProfile: UpdateProfileUseCase,
    private val updatePhoto: UpdateProfilePhotoUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            observeAuth().collect { user ->
                _state.value = when (user) {
                    null -> ProfileUiState.LoggedOut
                    else -> ProfileUiState.Authenticated(user)
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = getCurrent()
            _state.value = when (user) {
                null -> ProfileUiState.LoggedOut
                else -> ProfileUiState.Authenticated(user)
            }
        }
    }

    fun onLogout() {
        viewModelScope.launch(Dispatchers.IO) {
            logout()
        }
    }

    fun onClearOrders() {
        viewModelScope.launch(Dispatchers.IO) {
            clearOrders()
        }
    }

    fun onSaveProfile(firstName: String?, lastName: String?, phone: String?, address: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = updateProfile(firstName, lastName, phone, address)
            result.onSuccess { _state.value = ProfileUiState.Authenticated(it) }
                .onFailure { _state.value = ProfileUiState.Error(it.message ?: "Error al actualizar perfil") }
        }
    }

    fun onChangePhotoUrl(photoUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = updatePhoto(photoUrl)
            result.onSuccess { _state.value = ProfileUiState.Authenticated(it) }
                .onFailure { _state.value = ProfileUiState.Error(it.message ?: "Error al actualizar foto") }
        }
    }
}
