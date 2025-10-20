package com.bearkicks.app.features.cart.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bearkicks.app.features.cart.data.database.entity.OrderEntity
import com.bearkicks.app.features.cart.data.database.entity.OrderItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrdersDao {
    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeOrders(userId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun observeOrderItems(orderId: String): Flow<List<OrderItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<OrderItemEntity>)

    @Query("DELETE FROM order_items WHERE orderId IN (SELECT orderId FROM orders WHERE userId = :userId)")
    suspend fun deleteItemsForUser(userId: String)

    @Query("DELETE FROM orders WHERE userId = :userId")
    suspend fun deleteOrdersForUser(userId: String)

    @androidx.room.Transaction
    suspend fun clearUserOrders(userId: String) {
        deleteItemsForUser(userId)
        deleteOrdersForUser(userId)
    }
}
