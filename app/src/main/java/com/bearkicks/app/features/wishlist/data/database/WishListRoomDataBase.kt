package com.bearkicks.app.features.wishlist.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bearkicks.app.features.wishlist.data.database.dao.FavoriteDao
import com.bearkicks.app.features.wishlist.data.database.entity.FavoriteEntity

@Database(
    entities = [FavoriteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WishListRoomDataBase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}