package com.shopdummy.app.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cartEntity: CartEntity)

    @Query("SELECT * FROM cart_items")
    fun getAll(): Flow<List<CartEntity>>

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM cart_items")
    suspend fun deleteAll()

    @Query("UPDATE cart_items SET quantity = :quantity WHERE id = :id")
    suspend fun updateQuantity(id: Int, quantity: Int)
    
    @Query("SELECT * FROM cart_items WHERE productId = :productId LIMIT 1")
    suspend fun getByProductId(productId: Int): CartEntity?
}
