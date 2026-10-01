package com.example.androidnativeshowcase.core.data

import com.example.androidnativeshowcase.core.common.Result
import com.example.androidnativeshowcase.core.network.ShowcaseApi
import com.example.androidnativeshowcase.core.network.model.LoginRequest
import com.example.androidnativeshowcase.core.security.TokenStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: ShowcaseApi,
    private val tokenStore: TokenStore,
) {
    suspend fun login(email: String, password: String): Result<UserSession> {
        return runCatching {
            val response = api.login(LoginRequest(email, password))
            tokenStore.saveTokens(response.accessToken, response.refreshToken)
            UserSession(response.displayName)
        }.fold(
            onSuccess = { Result.Success(it) },
            onFailure = { Result.Error("Sign in failed", it) },
        )
    }

    suspend fun signOut() {
        tokenStore.clear()
    }
}
