package com.shopdummy.app.data.repository

import com.shopdummy.app.data.local.db.ProductDao
import com.shopdummy.app.data.local.db.ProductEntity
import com.shopdummy.app.data.remote.api.DummyJsonApi
import com.shopdummy.app.data.remote.dto.ProductDto
import com.shopdummy.app.domain.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ProductRepository(
    private val api: DummyJsonApi,
    private val productDao: ProductDao
) {
    // Retorna Flow desde Room (caché local) y actualiza desde la red
    fun getProducts(): Flow<List<Product>> {
        return productDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun refreshProducts() {
        withContext(Dispatchers.IO) {
            try {
                val response = api.getProducts()
                val entities = response.products.map { it.toEntity() }
                productDao.insertAll(entities)
            } catch (e: Exception) {
                // Si falla la red, mantenemos lo que hay en caché
                e.printStackTrace()
            }
        }
    }

    suspend fun getProductById(id: Int): Product? {
        return withContext(Dispatchers.IO) {
            // Intenta en caché primero
            val cached = productDao.getById(id)
            if (cached != null) {
                return@withContext cached.toDomain()
            }
            
            // Si no está en caché, llama a la API
            try {
                val dto = api.getProductById(id)
                val entity = dto.toEntity()
                // Opcional: guardarlo en caché
                productDao.insertAll(listOf(entity))
                entity.toDomain()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun searchProducts(query: String): List<Product> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.searchProducts(query)
                response.products.map { it.toEntity().toDomain() }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    private fun ProductEntity.toDomain() = Product(
        id = id,
        title = title,
        description = description,
        price = price,
        category = category,
        thumbnail = thumbnail,
        rating = rating
    )

    private fun ProductDto.toEntity() = ProductEntity(
        id = id,
        title = title,
        description = description,
        price = price,
        thumbnail = thumbnail,
        category = category,
        rating = rating ?: 0.0
    )
}
