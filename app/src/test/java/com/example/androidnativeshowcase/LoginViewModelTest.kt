package com.example.androidnativeshowcase

import com.example.androidnativeshowcase.core.data.AuthRepository
import com.example.androidnativeshowcase.core.network.ShowcaseApi
import com.example.androidnativeshowcase.core.network.model.LoginRequest
import com.example.androidnativeshowcase.core.network.model.LoginResponse
import com.example.androidnativeshowcase.core.network.model.RefreshRequest
import com.example.androidnativeshowcase.core.network.model.RefreshResponse
import com.example.androidnativeshowcase.core.network.model.ShowcaseItemResponse
import com.example.androidnativeshowcase.core.security.InMemoryTokenStore
import com.example.androidnativeshowcase.features.auth.LoginViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    @get:Rule val dispatcherRule = MainDispatcherRule()

    @Test
    fun loginWithBlankEmailShowsValidationError() {
        val viewModel = LoginViewModel(AuthRepository(SuccessApi(), InMemoryTokenStore()))

        viewModel.updateEmail("")
        viewModel.login()

        assertEquals("Email and password are required", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun successfulLoginUpdatesSignedInState() = runTest {
        val viewModel = LoginViewModel(AuthRepository(SuccessApi(), InMemoryTokenStore()))

        viewModel.login()

        assertTrue(viewModel.uiState.value.isSignedIn)
    }

    private class SuccessApi : ShowcaseApi {
        override suspend fun login(request: LoginRequest) = LoginResponse(
            displayName = "Reviewer",
            accessToken = "demo-access-token",
            refreshToken = "demo-refresh-token",
        )

        override suspend fun refresh(request: RefreshRequest): RefreshResponse {
            error("Not used")
        }

        override suspend fun getItems(): List<ShowcaseItemResponse> {
            error("Not used")
        }

        override suspend fun getItem(id: String): ShowcaseItemResponse {
            error("Not used")
        }
    }
}
