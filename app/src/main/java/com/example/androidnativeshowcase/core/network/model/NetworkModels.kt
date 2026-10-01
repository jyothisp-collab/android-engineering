package com.example.androidnativeshowcase.core.network.model

import com.example.androidnativeshowcase.core.data.ShowcaseItem

data class LoginRequest(val email: String, val password: String)

data class LoginResponse(
    val displayName: String,
    val accessToken: String,
    val refreshToken: String,
)

data class RefreshRequest(val refreshToken: String)

data class RefreshResponse(
    val accessToken: String,
    val refreshToken: String,
)

data class ShowcaseItemResponse(
    val id: String,
    val title: String,
    val summary: String,
    val detail: String,
    val updatedAtMillis: Long,
)

fun ShowcaseItemResponse.asDomain() = ShowcaseItem(
    id = id,
    title = title,
    summary = summary,
    detail = detail,
    updatedAtMillis = updatedAtMillis,
)
