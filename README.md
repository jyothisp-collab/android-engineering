# android-engineering-showcase

A small Android application built as a technical showcase for client review. It demonstrates common Android implementation concerns in a compact codebase rather than presenting a production app or tutorial.

## What It Demonstrates

- Kotlin, Jetpack Compose, Material 3, Navigation Compose, ViewModels, and immutable UI state.
- Hilt dependency injection across UI, repositories, networking, Room, WorkManager, and Firebase Messaging service entry points.
- Retrofit and OkHttp request flow with bearer tokens, refresh tokens, 401 handling, and one retry of the original request.
- Room-backed local caching for structured data.
- Keystore-backed encrypted storage for access and refresh tokens through AndroidX Security.
- WorkManager background refresh of cached data.
- Firebase Cloud Messaging service structure and notification handling without committed Firebase project credentials.
- Unit, ViewModel, network retry, and Compose UI tests.

## Architecture Overview

The app uses a practical MVVM structure:

- `features/` contains screen UI and ViewModels.
- `core/data/` contains repositories and app-level domain models.
- `core/network/` contains Retrofit models, API definitions, OkHttp auth behavior, and the demo backend interceptor.
- `core/database/` contains Room entities, DAO, and database setup.
- `core/security/` stores sensitive token values separately from Room.
- `services/` contains Android platform integrations for WorkManager and FCM.

The project avoids a large Clean Architecture layout because the app has one small authenticated data flow. A separate use-case layer would mostly duplicate repository methods here.

## Main Data Flow

1. The user signs in with demo values.
2. `AuthRepository` calls the Retrofit login endpoint.
3. The demo backend returns an intentionally expired access token and a refresh token.
4. The data list requests protected data.
5. OkHttp receives a 401, refreshes the token, stores the new token, and retries the original request once.
6. `ItemsRepository` writes the returned items to Room.
7. Compose screens observe Room through repository flows.

## Authentication And Networking

`DemoBackendInterceptor` acts as a local in-process backend. It keeps Retrofit and OkHttp in the real request path while avoiding real endpoints, API keys, or private infrastructure.

`AuthInterceptor` attaches the current access token to protected requests. `TokenAuthenticator` owns refresh behavior so token retry logic stays in the networking layer instead of leaking into ViewModels. Retry count is capped to avoid loops.

The token strings are fake demo values and are never displayed in UI or logged.

## Local Storage

Room stores normal structured application data in `showcase_items`. Access and refresh tokens are stored through `SecureTokenStore`, which uses AndroidX Security and the Android Keystore-backed `MasterKey`. This keeps sensitive values separate from the Room database.

## Firebase And FCM

`ShowcaseMessagingService` handles incoming FCM messages and delegates notification creation to `NotificationHelper`.

No `google-services.json` is committed. To test real FCM delivery, create a Firebase project, add an Android app for `com.example.androidnativeshowcase`, place `google-services.json` in `app/`, and apply the Google Services Gradle plugin in the app module. The repository remains buildable without those credentials.

## WorkManager

`RefreshItemsWorker` is a Hilt-injected `CoroutineWorker` that refreshes the same Room cache used by the UI. The Home screen can enqueue it as unique work.

## Testing

Included tests cover:

- Login ViewModel validation and success state.
- OkHttp 401 refresh and retry behavior.
- Compose rendering for the cached item list.

The test set is intentionally small and focused on behavior that matters to this showcase.

## Build And Run

Requirements:

- Android Studio with an installed Android SDK.
- JDK 17 or newer supported by the selected Android Gradle Plugin.

Commands:

```bash
./gradlew test
./gradlew lintDebug
./gradlew assembleDebug
```

Compose instrumentation tests require an emulator or device:

```bash
./gradlew connectedDebugAndroidTest
```

## Project Structure

```text
app/src/main/java/com/example/androidnativeshowcase/
  core/
    common/
    data/
    database/
    di/
    network/
    security/
  features/
    auth/
    home/
    items/
  services/
    background/
    notifications/
  ui/
```

## Key Decisions

- Hilt is used because the project has Android framework entry points that benefit from standard constructor injection.
- Room is used for structured cache data; tokens are not stored in Room.
- The network layer owns token refresh and retry behavior because every protected request should behave consistently.
- The fake backend is implemented as an OkHttp interceptor so Retrofit, converters, interceptors, and authenticators are still exercised.
- FCM configuration is documented but not committed because Firebase app files contain project-specific identifiers.

## Out Of Scope

- Real backend integration.
- Real Firebase project setup.
- Production account management.
- Offline conflict resolution.
- Analytics, crash reporting, and release signing.
