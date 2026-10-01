package com.example.androidnativeshowcase.core.data

data class UserSession(val displayName: String)

data class ShowcaseItem(
    val id: String,
    val title: String,
    val summary: String,
    val detail: String,
    val updatedAtMillis: Long,
)
