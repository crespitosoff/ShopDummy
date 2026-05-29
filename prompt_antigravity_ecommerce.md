# Prompt para Antigravity — App Android E-commerce con DummyJSON

## Nombre del proyecto
**ShopDummy**

> Crea el proyecto en Android Studio con **Empty Activity**, nombre `ShopDummy`,
> package `com.shopdummy.app`, lenguaje **Kotlin**, mínimo SDK **24**.

---

## Contexto general

Construye una app Android nativa de e-commerce completa usando:
- **Kotlin + Jetpack Compose** para toda la UI
- **Arquitectura MVVM** con Repository pattern
- **Room** para persistencia de productos (caché) y carrito de compras
- **SharedPreferences** para token JWT, tema (oscuro/claro) y user_id
- **Retrofit + Moshi** para consumir la API REST de DummyJSON (`https://dummyjson.com`)
- **Corrutinas + StateFlow** en todos los ViewModels
- **Navigation Compose** para la navegación entre pantallas

---

## Estructura de paquetes

```
com.shopdummy.app/
├── data/
│   ├── local/
│   │   ├── db/          → AppDatabase, ProductDao, CartDao
│   │   └── prefs/       → PreferencesManager (SharedPreferences)
│   ├── remote/
│   │   ├── api/         → DummyJsonApi (Retrofit interface)
│   │   └── dto/         → ProductDto, LoginRequestDto, LoginResponseDto
│   └── repository/
│       ├── ProductRepository
│       ├── AuthRepository
│       └── CartRepository
├── domain/
│   └── model/           → Product, CartItem, User (data classes limpias)
├── ui/
│   ├── auth/            → LoginScreen, AuthViewModel
│   ├── products/        → ProductListScreen, ProductDetailScreen, ProductViewModel
│   ├── cart/            → CartScreen, CartViewModel
│   └── settings/        → SettingsScreen, SettingsViewModel
├── utils/
│   ├── NotificationHelper.kt
│   └── ThemeManager.kt
└── navigation/
    └── AppNavGraph.kt
```

---

## Fase 1 — Base del proyecto (dependencias + estructura vacía)

Configura el proyecto recién creado con todo lo necesario para que las siguientes fases compilen sin errores.

### 1.1 `build.gradle.kts` (app)
Agrega estas dependencias:

```kotlin
// Jetpack Compose BOM
implementation(platform("androidx.compose:compose-bom:2024.02.00"))
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.ui:ui-tooling-preview")
implementation("androidx.activity:activity-compose:1.8.2")

// Navigation Compose
implementation("androidx.navigation:navigation-compose:2.7.6")

// ViewModel + Lifecycle
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

// Room
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
kapt("androidx.room:room-compiler:2.6.1")

// Retrofit + Moshi
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
kapt("com.squareup.moshi:moshi-kotlin-codegen:1.15.0")

// OkHttp logging
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

// Coil (carga de imágenes)
implementation("io.coil-kt:coil-compose:2.5.0")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
```

En `build.gradle.kts` raíz agrega el plugin `kapt`.

### 1.2 `AndroidManifest.xml`
- Permiso `INTERNET`
- Permiso `POST_NOTIFICATIONS`

### 1.3 Crea todos los paquetes vacíos con un archivo `placeholder` (o clase vacía) para que la estructura exista desde el inicio.

### 1.4 `AppDatabase.kt`
Room database con:
- Entidad `ProductEntity` (id, title, description, price, thumbnail, category, rating)
- Entidad `CartEntity` (id, productId, title, price, thumbnail, quantity)
- `ProductDao`: `insertAll`, `getAll`, `getById`, `deleteAll`
- `CartDao`: `insert`, `getAll`, `deleteById`, `deleteAll`, `updateQuantity`

### 1.5 `PreferencesManager.kt`
Clase que envuelve SharedPreferences y expone:
- `saveToken(token: String)` / `getToken(): String?`
- `saveUserId(id: Int)` / `getUserId(): Int`
- `saveTheme(isDark: Boolean)` / `isDarkTheme(): Boolean`
- `clearSession()`

### 1.6 `DummyJsonApi.kt`
Interface Retrofit con estos endpoints:
```kotlin
// Auth
@POST("auth/login")
suspend fun login(@Body body: LoginRequestDto): LoginResponseDto

// Products
@GET("products")
suspend fun getProducts(@Query("limit") limit: Int = 20, @Query("skip") skip: Int = 0): ProductListResponse

@GET("products/{id}")
suspend fun getProductById(@Path("id") id: Int): ProductDto

@GET("products/search")
suspend fun searchProducts(@Query("q") query: String): ProductListResponse

@GET("products/categories")
suspend fun getCategories(): List<String>
```

### 1.7 `RetrofitInstance.kt`
Singleton con base URL `https://dummyjson.com/`, interceptor de logs y Moshi converter.

### 1.8 `AppNavGraph.kt`
NavHost con rutas:
- `login` → LoginScreen
- `products` → ProductListScreen
- `detail/{productId}` → ProductDetailScreen
- `cart` → CartScreen
- `settings` → SettingsScreen

### 1.9 `MainActivity.kt`
- Detecta si hay token guardado en SharedPreferences para decidir la ruta inicial (`login` o `products`)
- Aplica el tema (claro/oscuro) según SharedPreferences usando `MaterialTheme`
- Llama a `AppNavGraph`

---

## Fase 2 — Autenticación (Login + JWT)

### 2.1 `AuthRepository.kt`
- `login(username, password)`: llama a `DummyJsonApi.login`, guarda el token y userId en SharedPreferences, retorna `Result<User>`
- `logout()`: llama a `PreferencesManager.clearSession()`
- `isLoggedIn(): Boolean`

### 2.2 `AuthViewModel.kt`
- Estado: `uiState: StateFlow<AuthUiState>` (Loading, Success, Error, Idle)
- `login(username: String, password: String)` lanza corrutina
- `logout()`

### 2.3 `LoginScreen.kt`
Pantalla Compose con:
- Logo o título "ShopDummy" centrado
- Campo `username` y campo `password` (con ícono de ocultar/mostrar)
- Botón "Ingresar" que dispara `AuthViewModel.login()`
- Indicador de carga mientras `uiState == Loading`
- Mensaje de error si falla
- Al éxito navega a `products` y limpia el backstack

> Credenciales de prueba DummyJSON: `emilys` / `emilyspass`

---

## Fase 3 — Catálogo de productos

### 3.1 `ProductRepository.kt`
- `getProducts()`: llama a la API, guarda en Room, retorna `Flow<List<Product>>`
- `getProductById(id)`: primero busca en Room, si no está llama a la API
- `searchProducts(query)`: solo API, sin caché
- Estrategia **offline-first**: si no hay red, retorna lo que hay en Room

### 3.2 `ProductViewModel.kt`
- `products: StateFlow<List<Product>>`
- `selectedProduct: StateFlow<Product?>`
- `searchQuery: StateFlow<String>`
- `loadProducts()`
- `loadProductById(id: Int)`
- `onSearchQueryChange(query: String)` con debounce de 400ms

### 3.3 `ProductListScreen.kt`
- `TopAppBar` con campo de búsqueda y botón de carrito (con badge de cantidad)
- `LazyVerticalGrid` de 2 columnas con cards de productos
- Cada card muestra: imagen (Coil), nombre, precio y rating con estrella
- Pull-to-refresh
- Botón flotante de Settings
- Al tocar una card navega a `detail/{productId}`

### 3.4 `ProductDetailScreen.kt`
- Imagen grande con scroll
- Nombre, precio, categoría, descripción completa, rating
- Botón "Agregar al carrito" que llama a `CartViewModel.addToCart(product)`
- Al agregar muestra un Snackbar y dispara una notificación local
- Botón de volver

---

## Fase 4 — Carrito y notificaciones

### 4.1 `CartRepository.kt`
- `addToCart(product: Product)`: inserta en Room o incrementa quantity si ya existe
- `removeFromCart(cartItemId: Int)`
- `getCartItems(): Flow<List<CartItem>>`
- `clearCart()`
- `getTotalPrice(): Flow<Double>`

### 4.2 `CartViewModel.kt`
- `cartItems: StateFlow<List<CartItem>>`
- `totalPrice: StateFlow<Double>`
- `itemCount: StateFlow<Int>` (para el badge)
- `addToCart(product)`, `removeItem(id)`, `clearCart()`

### 4.3 `CartScreen.kt`
- Lista de items con imagen, nombre, precio unitario y controles +/- de cantidad
- Total al pie con botón "Vaciar carrito"
- Si está vacío muestra ilustración con texto "Tu carrito está vacío"

### 4.4 `NotificationHelper.kt`
- Canal de notificaciones `CART_CHANNEL`
- `sendCartNotification(productName: String)` que muestra:
  - Título: "¡Agregado al carrito!"
  - Texto: "$productName fue agregado a tu carrito"
- Se llama desde `ProductDetailScreen` al agregar un producto

---

## Fase 5 — Ajustes y pulido final

### 5.1 `SettingsScreen.kt`
- Toggle modo oscuro/claro → guarda en SharedPreferences, aplica de inmediato
- Muestra usuario logueado (nombre e id desde SharedPreferences)
- Botón "Cerrar sesión" que llama a `AuthViewModel.logout()` y navega a `login`
- Sección "Caché" con botón "Limpiar productos guardados" (borra Room `ProductEntity`)

### 5.2 `SettingsViewModel.kt`
- `isDarkTheme: StateFlow<Boolean>`
- `toggleTheme()`
- `clearProductCache()`

### 5.3 Pulido general
- Asegúrate de que **todos los ViewModels** usen `viewModelScope` y manejen errores con `try/catch`
- Agrega comentarios en KDoc en todas las clases de `data/` y `domain/`
- Verifica que la navegación no permita volver al Login si ya hay sesión activa
- Asegúrate de que el tema oscuro/claro se aplique correctamente a todas las pantallas
- Revisa que Room no haga operaciones en el hilo principal (usar `Dispatchers.IO`)

---

## Resumen de criterios cubiertos

| Criterio rúbrica | Implementación |
|---|---|
| Kotlin + Compose (20 pts) | 100% Kotlin, todas las pantallas en Compose, StateFlow, corrutinas |
| Persistencia (20 pts) | Room (productos + carrito) + SharedPreferences (JWT + tema) |
| API REST + JSON (20 pts) | Retrofit + Moshi, DummyJSON, parsing automático de DTOs |
| UI Compose (20 pts) | 5 pantallas, NavHost, Material3, modo oscuro |
| Buenas prácticas (10 pts) | MVVM, Repository pattern, KDoc, Dispatchers.IO |
| Creatividad y extras (10 pts) | JWT auth, notificaciones locales, modo oscuro, offline-first |

**Total esperado: 100/100**
