package com.example.androidnativeshowcase.core.network

import com.example.androidnativeshowcase.core.network.model.LoginRequest
import com.example.androidnativeshowcase.core.network.model.LoginResponse
import com.example.androidnativeshowcase.core.network.model.RefreshRequest
import com.example.androidnativeshowcase.core.network.model.RefreshResponse
import com.example.androidnativeshowcase.core.network.model.ShowcaseItemResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ShowcaseApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): RefreshResponse

    @GET("items")
    suspend fun getItems(): List<ShowcaseItemResponse>

    @GET("items/{id}")
    suspend fun getItem(@Path("id") id: String): ShowcaseItemResponse
}
