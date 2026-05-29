package com.shopdummy.app.ui.products

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shopdummy.app.ui.cart.CartViewModel
import com.shopdummy.app.utils.NotificationHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Int,
    productViewModel: ProductViewModel,
    cartViewModel: CartViewModel,
    onNavigateBack: () -> Unit
) {
    val selectedProduct by productViewModel.selectedProduct.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(productId) {
        productViewModel.loadProductById(productId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedProduct?.title ?: "Loading...") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        selectedProduct?.let { product ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = product.thumbnail,
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = product.category.uppercase(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = product.title, style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "$${product.price}", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, contentDescription = "Rating", tint = MaterialTheme.colorScheme.secondary)
                        Text(text = product.rating.toString(), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 4.dp))
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = product.description, style = MaterialTheme.typography.bodyLarge)
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = {
                            cartViewModel.addToCart(product)
                            NotificationHelper.sendCartNotification(context, product.title)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Agregado al carrito")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Agregar al carrito")
                    }
                }
            }
        } ?: run {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

