package com.shopdummy.app.ui.products

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shopdummy.app.domain.model.Product
import com.shopdummy.app.ui.cart.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    productViewModel: ProductViewModel,
    cartViewModel: CartViewModel,
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val products by productViewModel.products.collectAsState()
    val searchQuery by productViewModel.searchQuery.collectAsState()
    val cartItemCount by cartViewModel.itemCount.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { productViewModel.onSearchQueryChange(it) },
                        placeholder = { Text("Search products...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(end = 8.dp),
                        singleLine = true
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToCart) {
                        BadgedBox(
                            badge = {
                                if (cartItemCount > 0) {
                                    Badge { Text(cartItemCount.toString()) }
                                }
                            }
                        ) {
                            Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToSettings) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings")
            }
        }
    ) { padding ->
        // Pull to refresh would typically need an external library like Accompanist or the new Material 3 implementation
        // For simplicity and to use core libraries, we omit the swipe to refresh layout unless explicitly adding the library
        // We can just show the list
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = padding,
            modifier = Modifier.fillMaxSize()
        ) {
            items(products) { product ->
                ProductCard(product, onClick = { onNavigateToDetail(product.id) })
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onClick)
            .fillMaxWidth()
    ) {
        Column {
            AsyncImage(
                model = product.thumbnail,
                contentDescription = product.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = product.title, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "$${product.price}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = "Rating", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                    Text(text = product.rating.toString(), style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 4.dp))
                }
            }
        }
    }
}

