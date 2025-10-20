package com.bearkicks.app.features.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bearkicks.app.features.home.domain.model.ShoeModel
import com.bearkicks.app.ui.components.BKCarousel
import com.bearkicks.app.features.auth.domain.usecase.ObserveAuthStateUseCase
import org.koin.compose.koinInject
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    onProductClick: (ShoeModel) -> Unit = {}
) {
    val vm: HomeViewModel = koinViewModel()
    val ui by vm.state.collectAsState()
    val observeAuth: ObserveAuthStateUseCase = koinInject()
    val user by observeAuth().collectAsState(initial = null)

    val isLoading = ui is HomeUiState.Loading
    val error = (ui as? HomeUiState.Error)?.message
    val offers = (ui as? HomeUiState.Success)?.offers ?: emptyList()
    val news = (ui as? HomeUiState.Success)?.news ?: emptyList()

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            when {
                isLoading -> {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(24.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                error != null -> {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        if (offers.isNotEmpty()) {
            item {
                BKCarousel(
                    title = "Ofertas",
                    items = offers,
                    onLikeClick = { vm.onToggleLike(it) },
                    isLoggedIn = user != null,
                    onRequireLogin = { /* handled by bottom bar navigation; could open login via Nav lifts later */ },
                    onProductClick = onProductClick
                )
                Spacer(Modifier.height(8.dp))
                HorizontalDivider()
            }
        }
        if (news.isNotEmpty()) {
            item {
                Spacer(Modifier.height(8.dp))
                BKCarousel(
                    title = "Novedades",
                    items = news,
                    onLikeClick = { vm.onToggleLike(it) },
                    isLoggedIn = user != null,
                    onRequireLogin = { },
                    onProductClick = onProductClick
                )
                Spacer(Modifier.height(8.dp))
                HorizontalDivider()
            }
        }
    }
}