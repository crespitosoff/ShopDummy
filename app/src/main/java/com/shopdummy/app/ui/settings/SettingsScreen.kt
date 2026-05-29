package com.shopdummy.app.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shopdummy.app.ui.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    authViewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()
    val username = settingsViewModel.getUsername()
    val userId = settingsViewModel.getUserId()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Usuario info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Person, contentDescription = "User", modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = "Usuario: $username", style = MaterialTheme.typography.titleMedium)
                    Text(text = "ID: $userId", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            // Tema
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Modo Oscuro", style = MaterialTheme.typography.titleMedium)
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = { settingsViewModel.toggleTheme() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            // Caché
            Text("Caché", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { settingsViewModel.clearProductCache() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Limpiar productos guardados")
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            // Logout
            Button(
                onClick = {
                    authViewModel.logout()
                    onNavigateToLogin()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}

