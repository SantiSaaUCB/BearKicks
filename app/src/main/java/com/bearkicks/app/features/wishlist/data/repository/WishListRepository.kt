package com.bearkicks.app.features.wishlist.data.repository

import com.bearkicks.app.features.home.domain.model.ShoeModel
import com.bearkicks.app.features.wishlist.data.datasource.FavoritesRemoteDataSource
import com.bearkicks.app.features.wishlist.data.datasource.WishListLocalDataSource
import com.bearkicks.app.features.wishlist.domain.repository.IWishListRepository
import kotlinx.coroutines.flow.Flow

class WishListRepository(
    private val local: WishListLocalDataSource,
    private val remote: FavoritesRemoteDataSource
) : IWishListRepository {
    override fun observeFavorites(): Flow<List<ShoeModel>> = local.observe()
    override fun observeFavoriteIds(): Flow<Set<String>> = local.observeIds()
    override fun observeIsFavorite(id: String): Flow<Boolean> = local.observeIsFavorite(id)
    override suspend fun setFavorite(shoe: ShoeModel, favored: Boolean) {
        if (favored) local.upsert(shoe) else local.delete(shoe.id)
        runCatching { remote.setFavorite(shoe, favored) }
    }
}
