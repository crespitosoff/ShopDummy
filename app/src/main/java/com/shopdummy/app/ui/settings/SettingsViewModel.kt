package com.shopdummy.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopdummy.app.data.local.db.ProductDao
import com.shopdummy.app.data.local.prefs.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val prefsManager: PreferencesManager,
    private val productDao: ProductDao
) : ViewModel() {

    private val _isDarkTheme = MutableStateFlow(prefsManager.isDarkTheme())
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun getUsername(): String = prefsManager.getUsername() ?: "Usuario Desconocido"
    fun getUserId(): Int = prefsManager.getUserId()

    fun toggleTheme() {
        val newTheme = !_isDarkTheme.value
        prefsManager.saveTheme(newTheme)
        _isDarkTheme.value = newTheme
    }

    fun clearProductCache() {
        viewModelScope.launch {
            productDao.deleteAll()
        }
    }
}
