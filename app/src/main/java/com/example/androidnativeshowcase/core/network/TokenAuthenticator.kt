package com.example.androidnativeshowcase.core.network

import com.example.androidnativeshowcase.core.network.model.RefreshRequest
import com.example.androidnativeshowcase.core.security.TokenStore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenStore: TokenStore,
    private val lazyRetrofit: dagger.Lazy<Retrofit>,
) : Authenticator {

    private val mutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.url.encodedPath.endsWith("/auth/refresh")) {
            tokenStore.clear()
            return null
        }

        if (response.retryCount() > 2) {
            return null
        }

        return runBlocking {
            mutex.withLock {
                val currentToken = tokenStore.getAccessToken()
                val requestToken = response.request.header("Authorization")?.removePrefix("Bearer ")

                if (currentToken != null && currentToken != requestToken) {
                    return@runBlocking response.request.newBuilder()
                        .header("Authorization", "Bearer $currentToken")
                        .build()
                }

                val refreshToken = tokenStore.getRefreshToken()
                if (refreshToken == null) {
                    tokenStore.clear()
                    return@runBlocking null
                }

                val apiService = lazyRetrofit.get().create(ShowcaseApi::class.java)
                val refreshCall = apiService.refreshSync(RefreshRequest(refreshToken))
                val refreshResponse = try {
                    refreshCall.execute()
                } catch (e: Exception) {
                    tokenStore.clear()
                    return@runBlocking null
                }

                if (refreshResponse.isSuccessful) {
                    val body = refreshResponse.body()
                    if (body != null) {
                        tokenStore.saveTokens(body.accessToken, body.refreshToken)
                        return@runBlocking response.request.newBuilder()
                            .header("Authorization", "Bearer ${body.accessToken}")
                            .build()
                    }
                }

                tokenStore.clear()
                null
            }
        }
    }

    private fun Response.retryCount(): Int {
        var currentResponse = priorResponse
        var result = 0
        while (currentResponse != null) {
            result++
            currentResponse = currentResponse.priorResponse
        }
        return result
    }
}
