package com.shopdummy.app.data.repository

import com.shopdummy.app.data.local.prefs.PreferencesManager
import com.shopdummy.app.data.remote.api.DummyJsonApi
import com.shopdummy.app.data.remote.dto.LoginRequestDto
import com.shopdummy.app.domain.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val api: DummyJsonApi,
    private val prefs: PreferencesManager
) {
    suspend fun login(username: String, password: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val request = LoginRequestDto(username, password)
                val response = api.login(request)
                
                // Guardar sesión localmente
                prefs.saveToken(response.accessToken)
                prefs.saveUserId(response.id)
                prefs.saveUsername(response.username)
                
                // Mapear DTO a modelo de dominio
                val user = User(
                    id = response.id,
                    username = response.username,
                    email = response.email,
                    firstName = response.firstName,
                    lastName = response.lastName,
                    gender = response.gender,
                    image = response.image,
                    token = response.accessToken
                )
                
                Result.success(user)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    fun logout() {
        prefs.clearSession()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getToken() != null
    }
}
