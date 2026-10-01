package com.example.androidnativeshowcase.core.security

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryTokenStore : TokenStore {
    private val mutex = Mutex()
    private var accessToken: String? = null
    private var refreshToken: String? = null

    override suspend fun getAccessToken(): String? = mutex.withLock { accessToken }

    override suspend fun getRefreshToken(): String? = mutex.withLock { refreshToken }

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        mutex.withLock {
            this.accessToken = accessToken
            this.refreshToken = refreshToken
        }
    }

    override suspend fun clear() {
        mutex.withLock {
            accessToken = null
            refreshToken = null
        }
    }
}
