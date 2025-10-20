package com.bearkicks.app.features.profile.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import com.bearkicks.app.features.cart.domain.usecase.ObserveOrdersUseCase
import com.bearkicks.app.navigation.Screen
import com.bearkicks.app.features.cart.domain.usecase.ClearOrdersUseCase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(onLoggedOut: () -> Unit, onSeeAllOrders: () -> Unit) {
    val vm: ProfileViewModel = koinViewModel()
    val ui by vm.state.collectAsState()

    when (val s = ui) {
        is ProfileUiState.Authenticated -> ProfileContent(
            state = s,
            onChangePhoto = { /* TODO: photo picker; for now expect URL change */ },
            onLogout = { vm.onLogout(); onLoggedOut() },
            onClearHistory = { vm.onClearOrders() },
            onSeeAllOrders = onSeeAllOrders,
            onSaveProfile = { f, l, p, a -> vm.onSaveProfile(f, l, p, a) },
            onUpdatePhotoUrl = { url -> vm.onChangePhotoUrl(url) }
        )
        ProfileUiState.LoggedOut -> Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
            Text("No has iniciado sesión", style = MaterialTheme.typography.titleMedium)
        }
        is ProfileUiState.Error -> Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
            Text(s.message, color = MaterialTheme.colorScheme.error)
        }
        ProfileUiState.Loading -> Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
            Text("Cargando…")
        }
    }
}

@Composable
private fun ProfileContent(
    state: ProfileUiState.Authenticated,
    onChangePhoto: () -> Unit,
    onLogout: () -> Unit,
    onClearHistory: () -> Unit,
    onSeeAllOrders: () -> Unit,
    onSaveProfile: (String?, String?, String?, String?) -> Unit,
    onUpdatePhotoUrl: (String) -> Unit
) {
    var editOpen = rememberSaveable { mutableStateOf(false) }
    var photoDialogOpen = rememberSaveable { mutableStateOf(false) }

    val observeOrders: ObserveOrdersUseCase = koinInject()
    val orders by observeOrders().collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header card
        ElevatedCard(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Avatar(url = state.user.photoUrl, name = "${state.user.firstName} ${state.user.lastName}")
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "${state.user.firstName} ${state.user.lastName}".trim(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Email, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    Spacer(Modifier.width(8.dp))
                    Text(state.user.email, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = { photoDialogOpen.value = true }) {
                        Icon(Icons.Filled.PhotoCamera, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Cambiar foto")
                    }
                    OutlinedButton(onClick = { editOpen.value = true }) {
                        Icon(Icons.Filled.Edit, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Editar perfil")
                    }
                }
            }
        }

        // Info card
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                state.user.phone?.takeIf { it.isNotBlank() }?.let {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Phone, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(it, style = MaterialTheme.typography.bodyLarge)
                    }
                    Divider()
                }
                state.user.address?.takeIf { it.isNotBlank() }?.let {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Home, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(it, style = MaterialTheme.typography.bodyLarge)
                    }
                } ?: Text("Agrega tu dirección para envíos", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        // Orders preview
        ElevatedCard(shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.ShoppingBag, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Historial de compras", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = onSeeAllOrders) { Text("Ver todo") }
                }
                if (orders.isEmpty()) {
                    Text("Aún no tienes compras", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    val fmt = rememberDateFormatter()
                    orders.take(5).forEach { o ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Pedido ${o.orderId.takeLast(6).uppercase()}", style = MaterialTheme.typography.bodyLarge)
                                Text(fmt(o.createdAt), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text("Bs ${"%.2f".format(o.total)}", style = MaterialTheme.typography.bodyLarge)
                        }
                        Divider()
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = onClearHistory) {
                        Text("Limpiar historial")
                    }
                }
            }
        }

        // Logout
        OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.ExitToApp, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Cerrar sesión")
        }

        if (editOpen.value) {
            EditProfileDialog(
                firstNameInit = state.user.firstName,
                lastNameInit = state.user.lastName,
                phoneInit = state.user.phone.orEmpty(),
                addressInit = state.user.address.orEmpty(),
                onDismiss = { editOpen.value = false },
                onSave = { f, l, p, a ->
                    onSaveProfile(f, l, p, a)
                    editOpen.value = false
                }
            )
        }

        if (photoDialogOpen.value) {
            UpdatePhotoUrlDialog(
                current = state.user.photoUrl.orEmpty(),
                onDismiss = { photoDialogOpen.value = false },
                onSave = { url -> onUpdatePhotoUrl(url); photoDialogOpen.value = false }
            )
        }
    }
}

@Composable
private fun EditProfileDialog(
    firstNameInit: String,
    lastNameInit: String,
    phoneInit: String,
    addressInit: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    val firstName = rememberSaveable { mutableStateOf(firstNameInit) }
    val lastName = rememberSaveable { mutableStateOf(lastNameInit) }
    val phone = rememberSaveable { mutableStateOf(phoneInit) }
    val address = rememberSaveable { mutableStateOf(addressInit) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar perfil") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = firstName.value, onValueChange = { firstName.value = it }, label = { Text("Nombre") })
                OutlinedTextField(value = lastName.value, onValueChange = { lastName.value = it }, label = { Text("Apellido") })
                OutlinedTextField(value = phone.value, onValueChange = { phone.value = it }, label = { Text("Teléfono") })
                OutlinedTextField(value = address.value, onValueChange = { address.value = it }, label = { Text("Dirección") })
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(firstName.value, lastName.value, phone.value, address.value) }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
private fun UpdatePhotoUrlDialog(
    current: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    val url = rememberSaveable { mutableStateOf(current) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Actualizar foto (URL)") },
        text = { OutlinedTextField(value = url.value, onValueChange = { url.value = it }, label = { Text("URL de la foto") }) },
        confirmButton = { TextButton(onClick = { onSave(url.value) }) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun Avatar(url: String?, name: String) {
    val initials = name.trim().split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .height(96.dp)
            .fillMaxWidth(0.3f),
        contentAlignment = Alignment.Center
    ) {
        if (url.isNullOrBlank()) {
            Text(initials, style = MaterialTheme.typography.titleLarge)
        } else {
            AsyncImage(
                model = url,
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(96.dp).clip(CircleShape)
            )
        }
    }
}

@Composable
private fun rememberDateFormatter(): (Long) -> String {
    val sdf = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }
    return { millis -> sdf.format(Date(millis)) }
}
