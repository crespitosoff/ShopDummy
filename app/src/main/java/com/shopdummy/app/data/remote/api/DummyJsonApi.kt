package com.shopdummy.app.data.remote.api

import com.shopdummy.app.data.remote.dto.LoginRequestDto
import com.shopdummy.app.data.remote.dto.LoginResponseDto
import com.shopdummy.app.data.remote.dto.ProductDto
import com.shopdummy.app.data.remote.dto.ProductListResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DummyJsonApi {
    // Auth
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequestDto): LoginResponseDto

    // Products
    @GET("products")
    suspend fun getProducts(
        @Query("limit") limit: Int = 20, 
        @Query("skip") skip: Int = 0
    ): ProductListResponse

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): ProductDto

    @GET("products/search")
    suspend fun searchProducts(@Query("q") query: String): ProductListResponse

    @GET("products/categories")
    suspend fun getCategories(): List<String>
}
