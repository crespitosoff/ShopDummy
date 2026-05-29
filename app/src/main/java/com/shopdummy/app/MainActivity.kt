package com.shopdummy.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.shopdummy.app.data.local.prefs.PreferencesManager
import com.shopdummy.app.navigation.AppNavGraph
import com.shopdummy.app.ui.theme.ShopDummyTheme
import com.shopdummy.app.utils.NotificationHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Crear canal de notificaciones
        NotificationHelper.createNotificationChannel(this)

        val prefsManager = PreferencesManager(this)
        val initialRoute = if (prefsManager.getToken() != null) "products" else "login"

        setContent {
            val isDarkTheme by prefsManager.themeFlow.collectAsState(initial = prefsManager.isDarkTheme())
            
            ShopDummyTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                AppNavGraph(
                    navController = navController,
                    startDestination = initialRoute
                )
            }
        }
    }
}
