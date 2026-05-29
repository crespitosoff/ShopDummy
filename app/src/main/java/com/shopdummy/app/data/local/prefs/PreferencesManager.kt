package com.shopdummy.app.data.local.prefs

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun saveUserId(id: Int) {
        prefs.edit().putInt(KEY_USER_ID, id).apply()
    }

    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, -1)
    }

    fun saveUsername(username: String) {
        prefs.edit().putString(KEY_USERNAME, username).apply()
    }

    fun getUsername(): String? {
        return prefs.getString(KEY_USERNAME, null)
    }

    fun saveTheme(isDark: Boolean) {
        prefs.edit().putBoolean(KEY_THEME_DARK, isDark).apply()
    }

    fun isDarkTheme(): Boolean {
        // Por defecto false, o se puede cambiar a true
        return prefs.getBoolean(KEY_THEME_DARK, false)
    }

    fun clearSession() {
        prefs.edit().remove(KEY_TOKEN).remove(KEY_USER_ID).apply()
    }

    companion object {
        private const val PREFS_NAME = "shop_dummy_prefs"
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_THEME_DARK = "theme_dark"
    }
}
