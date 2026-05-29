package com.shopdummy.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.shopdummy.app.ui.MainScreen
import com.shopdummy.app.ui.auth.LoginScreen
import com.shopdummy.app.ui.products.ProductDetailScreen
import com.shopdummy.app.utils.AppViewModelFactory

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String
) {
    val context = LocalContext.current
    val viewModelFactory = AppViewModelFactory(context)

    // Si el destino inicial era products, cart o settings, ahora debe ser main
    val actualStartDestination = if (startDestination in listOf("products", "cart", "settings")) "main" else startDestination

    NavHost(navController = navController, startDestination = actualStartDestination) {
        composable("login") {
            LoginScreen(
                viewModel = viewModel(factory = viewModelFactory),
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("main") {
            MainScreen(parentNavController = navController)
        }
        composable(
            route = "detail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: 0
            ProductDetailScreen(
                productId = productId,
                productViewModel = viewModel(factory = viewModelFactory),
                cartViewModel = viewModel(factory = viewModelFactory),
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
