package com.bearkicks.app.features.wishlist.data.datasource

import com.bearkicks.app.features.home.domain.model.ShoeModel
import com.bearkicks.app.features.wishlist.data.database.dao.FavoriteDao
import com.bearkicks.app.features.wishlist.data.database.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WishListLocalDataSource(private val dao: FavoriteDao) {
    fun observe(): Flow<List<ShoeModel>> =
        dao.observeAll().map { list ->
            list.map {
                ShoeModel(
                    id = it.id,
                    name = it.name,
                    brand = it.brand,
                    price = it.price,
                    discountPrice = it.discountPrice,
                    imageUrl = it.imageUrl,
                    isFeatured = false,
                    isNew = false,
                    isLiked = true
                )
            }
        }

    fun observeIds(): Flow<Set<String>> = dao.observeAll().map { list -> list.map { it.id }.toSet() }
    fun observeIsFavorite(id: String): Flow<Boolean> = dao.observeIsFavorite(id)

    suspend fun upsert(shoe: ShoeModel) =
        dao.upsert(
            FavoriteEntity(
                id = shoe.id,
                name = shoe.name,
                brand = shoe.brand,
                price = shoe.price,
                discountPrice = shoe.discountPrice,
                imageUrl = shoe.imageUrl
            )
        )

    suspend fun delete(id: String) = dao.delete(id)
}
