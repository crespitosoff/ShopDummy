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
            val isDarkTheme = prefsManager.isDarkTheme()
            
            ShopDummyTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Agregamos un Modifier.padding a una caja o contenedor principal 
                    // dentro de AppNavGraph si fuese necesario, 
                    // o le pasamos el innerPadding al nav graph, pero para simplificar
                    // y dado que Jetpack Compose maneja los insets bien, 
                    // podríamos dejar que cada pantalla lo maneje o no usar Scaffold aquí.
                    // Para ajustarnos a las recomendaciones:
                    androidx.compose.foundation.layout.Box(modifier = Modifier.padding(innerPadding)) {
                        AppNavGraph(
                            navController = navController,
                            startDestination = initialRoute
                        )
                    }
                }
            }
        }
    }
}
