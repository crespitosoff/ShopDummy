package com.shopdummy.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.shopdummy.app.ui.auth.LoginScreen
import com.shopdummy.app.ui.cart.CartScreen
import com.shopdummy.app.ui.products.ProductDetailScreen
import com.shopdummy.app.ui.products.ProductListScreen
import com.shopdummy.app.ui.settings.SettingsScreen
import com.shopdummy.app.utils.AppViewModelFactory

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String
) {
    val context = LocalContext.current
    val viewModelFactory = AppViewModelFactory(context)

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                viewModel = viewModel(factory = viewModelFactory),
                onLoginSuccess = {
                    navController.navigate("products") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("products") {
            ProductListScreen(
                productViewModel = viewModel(factory = viewModelFactory),
                cartViewModel = viewModel(factory = viewModelFactory),
                onNavigateToDetail = { productId ->
                    navController.navigate("detail/$productId")
                },
                onNavigateToCart = {
                    navController.navigate("cart")
                },
                onNavigateToSettings = {
                    navController.navigate("settings")
                }
            )
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
        composable("cart") {
            CartScreen(
                viewModel = viewModel(factory = viewModelFactory),
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("settings") {
            SettingsScreen(
                settingsViewModel = viewModel(factory = viewModelFactory),
                authViewModel = viewModel(factory = viewModelFactory),
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
