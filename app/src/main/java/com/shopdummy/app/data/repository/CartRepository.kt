package com.shopdummy.app.data.repository

import com.shopdummy.app.data.local.db.CartDao
import com.shopdummy.app.data.local.db.CartEntity
import com.shopdummy.app.domain.model.CartItem
import com.shopdummy.app.domain.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CartRepository(private val cartDao: CartDao) {

    fun getCartItems(): Flow<List<CartItem>> {
        return cartDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun addToCart(product: Product) {
        withContext(Dispatchers.IO) {
            val existingItem = cartDao.getByProductId(product.id)
            if (existingItem != null) {
                cartDao.updateQuantity(existingItem.id, existingItem.quantity + 1)
            } else {
                cartDao.insert(
                    CartEntity(
                        productId = product.id,
                        title = product.title,
                        price = product.price,
                        thumbnail = product.thumbnail,
                        quantity = 1
                    )
                )
            }
        }
    }

    suspend fun removeFromCart(cartItemId: Int) {
        withContext(Dispatchers.IO) {
            cartDao.deleteById(cartItemId)
        }
    }
    
    suspend fun updateQuantity(cartItemId: Int, quantity: Int) {
        withContext(Dispatchers.IO) {
            if (quantity <= 0) {
                cartDao.deleteById(cartItemId)
            } else {
                cartDao.updateQuantity(cartItemId, quantity)
            }
        }
    }

    suspend fun clearCart() {
        withContext(Dispatchers.IO) {
            cartDao.deleteAll()
        }
    }

    private fun CartEntity.toDomain() = CartItem(
        id = id,
        productId = productId,
        title = title,
        price = price,
        thumbnail = thumbnail,
        quantity = quantity
    )
}
