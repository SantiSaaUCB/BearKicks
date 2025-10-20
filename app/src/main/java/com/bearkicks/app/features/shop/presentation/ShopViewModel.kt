package com.bearkicks.app.features.shop.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bearkicks.app.features.home.domain.model.ShoeModel
import com.bearkicks.app.features.shop.domain.usecase.GetAllShoesUseCase
import com.bearkicks.app.features.wishlist.domain.repository.IWishListRepository
import com.bearkicks.app.features.wishlist.domain.usecase.ToggleWishListUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth

data class ShopUiState(
    val query: String = "",
    val items: List<ShoeModel> = emptyList(),
    val filtered: List<ShoeModel> = emptyList()
)

class ShopViewModel(
    private val getAll: GetAllShoesUseCase,
    private val toggleFavorite: ToggleWishListUseCase,
    private val wishRepo: IWishListRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val state: StateFlow<ShopUiState> =
        combine(
            _query,
            combine(getAll(), wishRepo.observeFavoriteIds()) { list, favIds ->
                list.map { it.copy(isLiked = it.id in favIds) }
            }
        ) { q, list ->
            val filtered = if (q.isBlank()) list else list.filter {
                it.name.contains(q, true) || (it.brand?.contains(q, true) == true)
            }
            ShopUiState(query = q, items = list, filtered = filtered)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, ShopUiState())

    fun onQueryChange(q: String) { _query.value = q }

    fun onToggleLike(item: ShoeModel) {
        if (FirebaseAuth.getInstance().currentUser == null) return
        viewModelScope.launch(Dispatchers.IO) { toggleFavorite(item) }
    }
}
