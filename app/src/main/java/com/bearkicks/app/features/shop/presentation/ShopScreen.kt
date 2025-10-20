package com.bearkicks.app.features.shop.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bearkicks.app.features.home.domain.model.ShoeModel
import com.bearkicks.app.ui.components.BKProductListItem
import com.bearkicks.app.ui.components.BKTextField
import com.bearkicks.app.features.auth.domain.usecase.ObserveAuthStateUseCase
import org.koin.compose.koinInject
import org.koin.androidx.compose.koinViewModel

@Composable
fun ShopScreen(
    onProductClick: (ShoeModel) -> Unit
) {
    val vm: ShopViewModel = koinViewModel()
    val ui by vm.state.collectAsState()
    val observeAuth: ObserveAuthStateUseCase = koinInject()
    val user by observeAuth().collectAsState(initial = null)
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        item {
            BKTextField(
                value = ui.query,
                onValueChange = vm::onQueryChange,
                label = "Buscar",
                placeholder = "Nombre o marca",
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
            )
            Spacer(Modifier.height(4.dp))
        }
        if (ui.filtered.isEmpty()) {
            item {
                Text(
                    text = if (ui.query.isBlank()) "Sin productos" else "Sin resultados",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(12.dp)
                )
            }
        } else {
            items(ui.filtered, key = { it.id }) { shoe ->
                BKProductListItem(
                    shoe = shoe,
                    onClick = onProductClick,
                    onLikeClick = { vm.onToggleLike(it) },
                    isLoggedIn = user != null,
                    onRequireLogin = { /* Navigate to login from parent via Nav? show a hint here */ },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}