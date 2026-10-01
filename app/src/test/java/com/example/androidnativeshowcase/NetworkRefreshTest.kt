package com.example.androidnativeshowcase

import com.example.androidnativeshowcase.core.network.AuthInterceptor
import com.example.androidnativeshowcase.core.network.DemoBackendInterceptor
import com.example.androidnativeshowcase.core.network.ShowcaseApi
import com.example.androidnativeshowcase.core.network.TokenAuthenticator
import com.example.androidnativeshowcase.core.network.model.LoginRequest
import com.example.androidnativeshowcase.core.security.InMemoryTokenStore
import com.google.gson.Gson
import dagger.Lazy
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkRefreshTest {
    @Test
    fun protectedRequestRefreshesTokenAndRetriesAfterUnauthorized() = runTest {
        val tokenStore = InMemoryTokenStore()
        lateinit var api: ShowcaseApi
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenStore))
            .addInterceptor(
                TokenAuthenticator(
                    tokenStore,
                    object : Lazy<ShowcaseApi> {
                        override fun get(): ShowcaseApi = api
                    },
                ),
            )
            .addInterceptor(DemoBackendInterceptor(Gson()))
            .build()
        api = Retrofit.Builder()
            .baseUrl("https://demo.local/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()
            .create(ShowcaseApi::class.java)

        api.login(LoginRequest("reviewer@example.com", "demo-password")).also {
            tokenStore.saveTokens(it.accessToken, it.refreshToken)
        }

        val items = api.getItems()

        assertEquals(3, items.size)
        assertEquals("demo-valid-access-token", tokenStore.getAccessToken())
    }
}
