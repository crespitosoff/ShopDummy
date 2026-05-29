package com.shopdummy.app.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopdummy.app.data.repository.CartRepository
import com.shopdummy.app.domain.model.CartItem
import com.shopdummy.app.domain.model.Product
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel(
    private val repository: CartRepository
) : ViewModel() {

    val cartItems = repository.getCartItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalPrice = cartItems.map { items ->
        items.sumOf { it.price * it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val itemCount = cartItems.map { items ->
        items.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun addToCart(product: Product) {
        viewModelScope.launch {
            repository.addToCart(product)
        }
    }

    fun removeItem(id: Int) {
        viewModelScope.launch {
            repository.removeFromCart(id)
        }
    }
    
    fun updateQuantity(id: Int, quantity: Int) {
        viewModelScope.launch {
            repository.updateQuantity(id, quantity)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }
}
