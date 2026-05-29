package com.shopdummy.app.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shopdummy.app.data.local.db.AppDatabase
import com.shopdummy.app.data.local.prefs.PreferencesManager
import com.shopdummy.app.data.remote.api.RetrofitInstance
import com.shopdummy.app.data.repository.AuthRepository
import com.shopdummy.app.data.repository.CartRepository
import com.shopdummy.app.data.repository.ProductRepository
import com.shopdummy.app.ui.auth.AuthViewModel
import com.shopdummy.app.ui.cart.CartViewModel
import com.shopdummy.app.ui.products.ProductViewModel
import com.shopdummy.app.ui.settings.SettingsViewModel

class AppViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    private val prefs by lazy { PreferencesManager(context) }
    private val api by lazy { RetrofitInstance.api }
    private val db by lazy { AppDatabase.getDatabase(context) }

    private val authRepository by lazy { AuthRepository(api, prefs) }
    private val productRepository by lazy { ProductRepository(api, db.productDao()) }
    private val cartRepository by lazy { CartRepository(db.cartDao()) }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(ProductViewModel::class.java) -> {
                ProductViewModel(productRepository) as T
            }
            modelClass.isAssignableFrom(CartViewModel::class.java) -> {
                CartViewModel(cartRepository) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(prefs, db.productDao()) as T
            }
            // Agregaremos otros ViewModels aquí
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
