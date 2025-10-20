package com.bearkicks.app.features.auth.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bearkicks.app.ui.components.BKButton
import com.bearkicks.app.ui.components.BKTextField
import com.bearkicks.app.ui.components.BKTextFieldSize
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreen(
    onRegistered: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val vm: RegisterViewModel = koinViewModel()
    val ui by vm.state.collectAsState()

    val firstName = remember { mutableStateOf("") }
    val lastName = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val phone = remember { mutableStateOf("") }
    val address = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Crear cuenta", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(24.dp))

        BKTextField(value = firstName.value, onValueChange = { firstName.value = it }, label = "Nombre", size = BKTextFieldSize.Medium, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        BKTextField(value = lastName.value, onValueChange = { lastName.value = it }, label = "Apellido", size = BKTextFieldSize.Medium, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        BKTextField(value = email.value, onValueChange = { email.value = it }, label = "Email", size = BKTextFieldSize.Medium, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        BKTextField(value = phone.value, onValueChange = { phone.value = it }, label = "Teléfono (8 dígitos)", size = BKTextFieldSize.Medium, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        BKTextField(value = address.value, onValueChange = { address.value = it }, label = "Dirección", size = BKTextFieldSize.Medium, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        BKTextField(value = password.value, onValueChange = { password.value = it }, label = "Contraseña", isPassword = true, size = BKTextFieldSize.Medium, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(16.dp))
        when (ui) {
            is RegisterUiState.Loading -> CircularProgressIndicator()
            is RegisterUiState.Error -> Text((ui as RegisterUiState.Error).message, color = MaterialTheme.colorScheme.error)
            is RegisterUiState.Success -> onRegistered()
            else -> {}
        }
        Spacer(Modifier.height(16.dp))
        BKButton(text = "Registrar", modifier = Modifier.fillMaxWidth()) {
            vm.onRegister(
                firstName.value,
                lastName.value,
                email.value,
                phone.value.ifBlank { null },
                address.value.ifBlank { null },
                password.value,
                null
            )
        }
        Spacer(Modifier.height(8.dp))
        BKButton(text = "Volver a Iniciar sesión", modifier = Modifier.fillMaxWidth()) { onBackToLogin() }
    }
}
