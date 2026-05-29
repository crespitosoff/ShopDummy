package com.shopdummy.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shopdummy.app.ui.cart.CartScreen
import com.shopdummy.app.ui.products.ProductListScreen
import com.shopdummy.app.ui.settings.SettingsScreen
import com.shopdummy.app.utils.AppViewModelFactory

@Composable
fun MainScreen(
    parentNavController: NavHostController
) {
    val bottomNavController = rememberNavController()
    val context = LocalContext.current
    val viewModelFactory = AppViewModelFactory(context)

    val items = listOf(
        BottomNavItem("Catálogo", "products", Icons.Filled.Home),
        BottomNavItem("Carrito", "cart", Icons.Filled.ShoppingCart),
        BottomNavItem("Perfil", "settings", Icons.Filled.Person)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            bottomNavController.navigate(item.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = "products",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("products") {
                ProductListScreen(
                    productViewModel = viewModel(factory = viewModelFactory),
                    cartViewModel = viewModel(factory = viewModelFactory),
                    onNavigateToDetail = { productId ->
                        // Navigate to detail using parent controller (hides bottom bar)
                        parentNavController.navigate("detail/$productId")
                    },
                    onNavigateToCart = {
                        bottomNavController.navigate("cart") {
                            popUpTo(bottomNavController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToSettings = {
                        bottomNavController.navigate("settings") {
                            popUpTo(bottomNavController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable("cart") {
                CartScreen(
                    viewModel = viewModel(factory = viewModelFactory),
                    onNavigateBack = { bottomNavController.popBackStack() }
                )
            }
            composable("settings") {
                SettingsScreen(
                    settingsViewModel = viewModel(factory = viewModelFactory),
                    authViewModel = viewModel(factory = viewModelFactory),
                    onNavigateToLogin = {
                        parentNavController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateBack = { bottomNavController.popBackStack() }
                )
            }
        }
    }
}

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
