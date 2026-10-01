package com.example.androidnativeshowcase.core.network

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DemoBackendInterceptor @Inject constructor(
    private val gson: Gson,
) : Interceptor {
    private val forceFirstItemsRequestToRefresh = AtomicBoolean(true)
    private val json = "application/json".toMediaType()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        val authHeader = request.header("Authorization")

        val response = when {
            path == "/auth/login" -> ok(
                mapOf(
                    "displayName" to "Demo Reviewer",
                    "accessToken" to EXPIRED_ACCESS_TOKEN,
                    "refreshToken" to VALID_REFRESH_TOKEN,
                ),
            )

            path == "/auth/refresh" -> ok(
                mapOf(
                    "accessToken" to VALID_ACCESS_TOKEN,
                    "refreshToken" to VALID_REFRESH_TOKEN,
                ),
            )

            path == "/items" && shouldReject(authHeader) -> unauthorized()
            path == "/items" -> ok(items)
            path.startsWith("/items/") && shouldReject(authHeader) -> unauthorized()
            path.startsWith("/items/") -> {
                val id = path.substringAfterLast("/")
                items.firstOrNull { it["id"] == id }?.let(::ok) ?: notFound()
            }

            else -> notFound()
        }

        return response
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .build()
    }

    private fun shouldReject(authHeader: String?): Boolean {
        val force = forceFirstItemsRequestToRefresh.getAndSet(false)
        if (authHeader != "Bearer $VALID_ACCESS_TOKEN") return true
        return force
    }

    private fun ok(body: Any) = baseResponse(200, "OK", gson.toJson(body))

    private fun unauthorized() = baseResponse(401, "Unauthorized", """{"message":"expired"}""")

    private fun notFound() = baseResponse(404, "Not Found", """{"message":"missing"}""")

    private fun baseResponse(code: Int, message: String, body: String) = Response.Builder()
        .code(code)
        .message(message)
        .body(body.toResponseBody(json))

    private companion object {
        const val EXPIRED_ACCESS_TOKEN = "demo-expired-access-token"
        const val VALID_ACCESS_TOKEN = "demo-valid-access-token"
        const val VALID_REFRESH_TOKEN = "demo-refresh-token"

        val items = listOf(
            mapOf(
                "id" to "networking",
                "title" to "Authenticated networking",
                "summary" to "Retrofit and OkHttp with token refresh on 401.",
                "detail" to "The first protected request receives a 401, refreshes the token, and retries once through OkHttp.",
                "updatedAtMillis" to 1_725_800_000_000L,
            ),
            mapOf(
                "id" to "storage",
                "title" to "Room cache",
                "summary" to "Read-through caching for structured app data.",
                "detail" to "Repository reads cached data from Room and refreshes from the demo API when requested.",
                "updatedAtMillis" to 1_725_700_000_000L,
            ),
            mapOf(
                "id" to "background",
                "title" to "Background refresh",
                "summary" to "WorkManager updates the local cache off the main thread.",
                "detail" to "A Hilt-injected CoroutineWorker performs the same repository refresh used by the UI.",
                "updatedAtMillis" to 1_725_600_000_000L,
            ),
        )
    }
}
