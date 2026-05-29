# 🛍️ ShopDummy

Aplicación Android de comercio electrónico construida con **Kotlin + Jetpack Compose**, que consume la API pública de [DummyJSON](https://dummyjson.com) como fuente de datos de productos y autenticación.

---

## 📸 Pantallas

| Login | Catálogo | Detalle | Carrito |
|-------|----------|---------|---------|
| Autenticación segura con JWT | Búsqueda en tiempo real | Precio, rating, descripción | Gestión de cantidades y checkout |

---

## 🏗️ Arquitectura

El proyecto sigue el patrón **MVVM (Model-View-ViewModel)** con separación en capas:

```
app/
└── src/main/java/com/shopdummy/app/
    ├── data/
    │   ├── local/
    │   │   ├── db/          ← Room Database (SQLite)
    │   │   └── prefs/       ← SharedPreferences (sesión JWT)
    │   ├── remote/
    │   │   ├── api/         ← Retrofit + Moshi (HTTP)
    │   │   └── dto/         ← Data Transfer Objects
    │   └── repository/      ← Repositorios (fuente única de verdad)
    ├── domain/
    │   └── model/           ← Modelos de dominio (Product, CartItem, User)
    ├── navigation/
    │   └── AppNavGraph.kt   ← Grafo de navegación con NavHost
    ├── ui/
    │   ├── auth/            ← Login screen + AuthViewModel
    │   ├── cart/            ← Carrito screen + CartViewModel
    │   ├── products/        ← Lista y detalle + ProductViewModel
    │   ├── settings/        ← Configuración + SettingsViewModel
    │   └── theme/           ← Material 3 Theme
    └── utils/
        ├── AppViewModelFactory.kt  ← DI manual de ViewModels
        └── NotificationHelper.kt  ← Notificaciones push locales
```

---

## 🛠️ Stack Tecnológico

| Categoría | Tecnología | Versión |
|-----------|-----------|---------|
| Lenguaje | **Kotlin** | 2.3.21 |
| UI | **Jetpack Compose** + Material 3 | BOM 2026.05.01 |
| Build system | AGP | 9.2.1 |
| Anotaciones (ORM) | **KSP** (Kotlin Symbol Processing) | 2.3.9 |
| Base de datos local | **Room** | 2.8.4 |
| HTTP client | **Retrofit 3** + OkHttp | 3.0.0 |
| Serialización JSON | **Moshi** (reflection) | 1.15.2 |
| Carga de imágenes | **Coil** | 2.7.0 |
| Navegación | Navigation Compose | 2.9.8 |
| Corrutinas | Kotlinx Coroutines | 1.11.0 |
| Sesión | SharedPreferences (JWT) | — |

> ⚠️ **¿Por qué KSP y no KAPT?**
> El proyecto usa AGP 9.2.1 con Kotlin 2.3.x. KAPT (el procesador de anotaciones anterior) no es compatible con Kotlin 2.x de forma estable y genera errores como `AppDatabase_Impl does not exist`. KSP es el sucesor oficial de KAPT, con soporte nativo para Kotlin 2.x y compilación hasta 2× más rápida.

> 📝 **¿Por qué no hay Java?**
> El proyecto es 100% Kotlin. Lo que Android Studio puede mostrar como "Java" son archivos generados automáticamente por KSP en tiempo de compilación (como `AppDatabase_Impl.java`), pero **ningún archivo Java fue escrito manualmente** — todo el código de la app está en Kotlin con Jetpack Compose.

---

## 🔐 Autenticación y Usuarios

### ¿Por qué solo un usuario?

La app usa la **API pública de DummyJSON**, que es un servicio de datos ficticios de solo lectura diseñado para pruebas y prototipos. Por esta razón:

- **No permite crear nuevas cuentas** — no existe un endpoint de registro.
- **No almacena datos reales** — cualquier operación de escritura es simulada.
- Los usuarios son **pre-existentes** en la base de datos de DummyJSON.

### Credenciales de prueba disponibles

Puedes usar cualquiera de estos usuarios de [dummyjson.com/users](https://dummyjson.com/users):

| Usuario | Contraseña |
|---------|------------|
| `emilys` | `emilyspass` |
| `michaelw` | `michaelwpass` |
| `sophiab` | `sophiabpass` |
| `jamesd` | `jamesdpass` |
| `emmaj` | `emmajpass` |

> En un proyecto real con backend propio, la pantalla de login incluiría también un botón "Registrarse" conectado a un endpoint `POST /auth/register`.

---

## 🚀 Cómo Ejecutar

### Prerrequisitos
- Android Studio Ladybug / Meerkat o superior
- JDK 11+
- Android SDK 36
- Dispositivo/emulador con Android 7.0+ (API 24)

### Pasos

```bash
# 1. Clonar el repositorio
git clone https://github.com/tu-usuario/ShopDummy.git

# 2. Abrir en Android Studio
# File → Open → selecciona la carpeta ShopDummy

# 3. Sync de Gradle
# Android Studio lo hará automáticamente, o presiona "Sync Now"

# 4. Ejecutar
# Run → Run 'app'  (Shift+F10)
```

---

## 📱 Flujo de la Aplicación (Guía de Prueba)

### 1️⃣ Login
- Al iniciar la app verás la pantalla de login
- Ingresa `emilys` / `emilyspass` (o cualquier otro usuario de la tabla)
- Pulsa **"Ingresar"**
- La sesión se guarda localmente con el JWT de la API

### 2️⃣ Catálogo de Productos
- Verás una grilla de productos cargados desde DummyJSON
- **Busca** productos en tiempo real usando la barra de texto superior
- Toca cualquier tarjeta de producto para ver su detalle
- El ícono del carrito en la esquina superior derecha muestra cuántos items tienes
- El botón ⚙️ (esquina inferior derecha) abre Configuración

### 3️⃣ Detalle de Producto
- Muestra imagen, precio, rating, categoría y descripción
- Pulsa **"Agregar al carrito"**
  - El producto se guarda localmente en Room
  - Aparece un Snackbar de confirmación
  - Recibes una notificación push local en la barra de notificaciones

### 4️⃣ Carrito de Compras
- Accede desde el ícono del carrito en el catálogo
- Puedes **aumentar/disminuir** la cantidad de cada producto con los botones `+` / `-`
- Puedes **eliminar** productos individuales con el ícono de basura
- Puedes **vaciar** el carrito completo
- El **total** se calcula automáticamente en tiempo real
- Pulsa **"Comprar"** para simular el checkout:
  - Aparece un diálogo de confirmación con el total
  - Al aceptar, el carrito se vacía y regresas al catálogo

### 5️⃣ Configuración / Settings
- Accede desde el botón ⚙️ del catálogo
- Muestra información del usuario logueado
- Contiene el botón **"Cerrar sesión"** que limpia el JWT y vuelve al Login

---

## 📡 API - DummyJSON (Consumo con Postman)

La aplicación usa la API de [DummyJSON](https://dummyjson.com). Si necesitas sustentar cómo fue consumida la API, aquí te explico cómo replicar las consultas usando **Postman**:

### 1. Login de Usuario (`POST`)
- **URL**: `https://dummyjson.com/auth/login`
- **Método**: `POST`
- **Headers**:
  - `Content-Type: application/json`
- **Body** (selecciona `raw` y formato `JSON`):
  ```json
  {
    "username": "emilys",
    "password": "emilyspass",
    "expiresInMins": 60
  }
  ```
- **Respuesta**: Recibirás un JSON con los datos del usuario y un `accessToken` (JWT) que la app guarda internamente.

### 2. Obtener Catálogo de Productos (`GET`)
- **URL**: `https://dummyjson.com/products`
- **Método**: `GET`
- **Params** (opcionales): `limit=30` (para paginar)
- **Respuesta**: Un objeto JSON que contiene un arreglo `products` con la lista completa.

### 3. Buscar Productos (`GET`)
- **URL**: `https://dummyjson.com/products/search`
- **Método**: `GET`
- **Params**: `q=laptop` (o cualquier palabra clave a buscar en el título)
- **Respuesta**: El arreglo `products` filtrado por la API.

La app usa Retrofit internamente para realizar exactamente estas mismas peticiones HTTP y luego Moshi convierte las respuestas JSON en objetos Kotlin para mostrarlos en la UI.

---

## 💾 Persistencia Local

### Room Database (`shop_dummy_db`)

| Tabla | Descripción |
|-------|-------------|
| `products` | Caché local de productos consultados |
| `cart_items` | Items del carrito del usuario (persiste entre sesiones) |

### SharedPreferences

| Clave | Valor |
|-------|-------|
| `auth_token` | JWT accessToken de DummyJSON |
| `user_id` | ID del usuario logueado |
| `username` | Username del usuario logueado |

---

## 🔔 Notificaciones

La app usa **notificaciones push locales** (sin servidor) para confirmar cuando un producto se agrega al carrito. Requiere permiso `POST_NOTIFICATIONS` en Android 13+.

---

## 📦 Dependencias Principales

```toml
# gradle/libs.versions.toml

[versions]
kotlin = "2.3.21"
agp = "9.2.1"
ksp = "2.3.9"
composeBom = "2026.05.01"
room = "2.8.4"
retrofit = "3.0.0"
moshi = "1.15.2"
coil = "2.7.0"
navigation = "2.9.8"
```

---

## 🗂️ Decisiones de Diseño

| Decisión | Razón |
|----------|-------|
| **Moshi reflection** en lugar de codegen | `moshi-kotlin-codegen` (KAPT) no es compatible con Kotlin 2.3.x |
| **KSP** para Room en lugar de KAPT | KAPT está deprecado para Kotlin 2.x; KSP es el sucesor oficial |
| **Sin inyección de dependencias** (Hilt/Koin) | Proyecto de aprendizaje; se usa `AppViewModelFactory` manual |
| **DummyJSON** como backend | API REST pública, sin necesidad de configurar servidor propio |
| **SharedPreferences** para sesión | Simplicidad; en producción se usaría EncryptedSharedPreferences |

---

## 📄 Licencia

Este proyecto es de carácter educativo. Los datos son provistos por [DummyJSON](https://dummyjson.com) bajo uso público libre.
