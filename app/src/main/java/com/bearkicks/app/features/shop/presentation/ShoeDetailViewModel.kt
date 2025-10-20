package com.bearkicks.app.features.shop.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bearkicks.app.features.home.domain.model.ShoeModel
import com.bearkicks.app.features.shop.domain.usecase.GetShoeDetailUseCase
import com.bearkicks.app.features.wishlist.domain.repository.IWishListRepository
import com.bearkicks.app.features.wishlist.domain.usecase.ToggleWishListUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ShoeDetailViewModel(
    private val id: String,
    private val getDetail: GetShoeDetailUseCase,
    private val toggleFavorite: ToggleWishListUseCase,
    private val wishRepo: IWishListRepository
) : ViewModel() {
    private val _item = MutableStateFlow<ShoeModel?>(null)
    val item: StateFlow<ShoeModel?> = _item.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            combine(getDetail(id), wishRepo.observeIsFavorite(id)) { shoe, favored ->
                shoe?.copy(isLiked = favored)
            }.collect { _item.value = it }
        }
    }

    fun onToggleLike() {
        val cur = _item.value ?: return
        viewModelScope.launch(Dispatchers.IO) { toggleFavorite(cur) }
    }
}
