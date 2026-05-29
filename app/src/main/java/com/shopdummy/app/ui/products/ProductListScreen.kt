package com.shopdummy.app.ui.products

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shopdummy.app.domain.model.Product
import com.shopdummy.app.ui.cart.CartViewModel
import com.shopdummy.app.utils.NotificationHelper
import kotlinx.coroutines.launch

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
    val sortOption by productViewModel.sortOption.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar Area
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 0.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { productViewModel.onSearchQueryChange(it) },
                        placeholder = { Text("Buscar productos...") },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Chips de filtrado/ordenamiento
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = sortOption == SortOption.DEFAULT,
                                onClick = { productViewModel.updateSortOption(SortOption.DEFAULT) },
                                label = { Text("Relevancia") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = sortOption == SortOption.PRICE_ASC,
                                onClick = { productViewModel.updateSortOption(SortOption.PRICE_ASC) },
                                label = { Text("Menor Precio") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = sortOption == SortOption.PRICE_DESC,
                                onClick = { productViewModel.updateSortOption(SortOption.PRICE_DESC) },
                                label = { Text("Mayor Precio") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = sortOption == SortOption.RATING_DESC,
                                onClick = { productViewModel.updateSortOption(SortOption.RATING_DESC) },
                                label = { Text("Mejor Valorados") }
                            )
                        }
                    }
                }
            }

            // Products Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(products) { product ->
                    ProductCard(
                        product = product,
                        onClick = { onNavigateToDetail(product.id) },
                        onAddToCart = {
                            cartViewModel.addToCart(product)
                            NotificationHelper.sendCartNotification(context, product.title)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Agregado al carrito: ${product.title}")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, onClick: () -> Unit, onAddToCart: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onClick)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model = product.thumbnail,
                contentDescription = product.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(140.dp)
                    .fillMaxWidth()
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = product.title, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = "Rating", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                    Text(text = product.rating.toString(), style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 4.dp))
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "$${product.price}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    IconButton(
                        onClick = onAddToCart,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            Icons.Filled.AddShoppingCart, 
                            contentDescription = "Agregar", 
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }
    }
}

