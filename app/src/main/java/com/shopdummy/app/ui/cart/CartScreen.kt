package com.shopdummy.app.ui.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.shopdummy.app.domain.model.CartItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel,
    onNavigateBack: () -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val totalPrice by viewModel.totalPrice.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrito de Compras") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    tonalElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total:", style = MaterialTheme.typography.titleLarge)
                            Text("$${String.format("%.2f", totalPrice)}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            TextButton(onClick = { viewModel.clearCart() }) {
                                Text("Vaciar carrito")
                            }
                            Button(onClick = { /* TODO Checkout */ }) {
                                Text("Comprar")
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (cartItems.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Filled.ShoppingCart, contentDescription = "Empty", modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Tu carrito está vacío", style = MaterialTheme.typography.headlineSmall)
            }
        } else {
            LazyColumn(
                contentPadding = padding,
                modifier = Modifier.fillMaxSize()
            ) {
                items(cartItems) { item ->
                    CartItemRow(
                        item = item,
                        onIncrease = { viewModel.updateQuantity(item.id, item.quantity + 1) },
                        onDecrease = { viewModel.updateQuantity(item.id, item.quantity - 1) },
                        onRemove = { viewModel.removeItem(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.thumbnail,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                Text("$${item.price}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    IconButton(onClick = onDecrease, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Remove, contentDescription = "Minus")
                    }
                    Text(item.quantity.toString(), modifier = Modifier.padding(horizontal = 8.dp))
                    IconButton(onClick = onIncrease, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Add, contentDescription = "Plus")
                    }
                }
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Filled.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

