package com.bearkicks.app.features.wishlist.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bearkicks.app.features.wishlist.data.database.dao.FavoriteDao
import com.bearkicks.app.features.wishlist.data.database.entity.FavoriteEntity
import com.bearkicks.app.features.cart.data.database.dao.CartDao
import com.bearkicks.app.features.cart.data.database.dao.OrdersDao
import com.bearkicks.app.features.cart.data.database.entity.CartItemEntity
import com.bearkicks.app.features.cart.data.database.entity.OrderEntity
import com.bearkicks.app.features.cart.data.database.entity.OrderItemEntity

@Database(
    entities = [
        FavoriteEntity::class,
        CartItemEntity::class,
        OrderEntity::class,
        OrderItemEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class WishListRoomDataBase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun cartDao(): CartDao
    abstract fun ordersDao(): OrdersDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Add userId with default 'guest'
        db.execSQL("ALTER TABLE favorites ADD COLUMN userId TEXT NOT NULL DEFAULT 'guest'")
        // Optional indexes could be added for (userId, id)
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Create cart table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS cart (
                id TEXT NOT NULL PRIMARY KEY,
                userId TEXT NOT NULL,
                shoeId TEXT NOT NULL,
                name TEXT NOT NULL,
                brand TEXT,
                price REAL NOT NULL,
                imageUrl TEXT,
                size INTEGER,
                qty INTEGER NOT NULL
            )
            """.trimIndent()
        )
        // Create orders table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS orders (
                orderId TEXT NOT NULL PRIMARY KEY,
                userId TEXT NOT NULL,
                createdAt INTEGER NOT NULL,
                total REAL NOT NULL
            )
            """.trimIndent()
        )
        // Create order_items table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS order_items (
                id TEXT NOT NULL PRIMARY KEY,
                orderId TEXT NOT NULL,
                shoeId TEXT NOT NULL,
                name TEXT NOT NULL,
                brand TEXT,
                price REAL NOT NULL,
                qty INTEGER NOT NULL,
                imageUrl TEXT,
                size INTEGER
            )
            """.trimIndent()
        )
    }
}