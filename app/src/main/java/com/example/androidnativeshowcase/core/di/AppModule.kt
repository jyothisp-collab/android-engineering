package com.example.androidnativeshowcase.core.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.example.androidnativeshowcase.BuildConfig
import com.example.androidnativeshowcase.core.database.ShowcaseDatabase
import com.example.androidnativeshowcase.core.database.ShowcaseItemDao
import com.example.androidnativeshowcase.core.network.AuthInterceptor
import com.example.androidnativeshowcase.core.network.DemoBackendInterceptor
import com.example.androidnativeshowcase.core.network.ShowcaseApi
import com.example.androidnativeshowcase.core.network.TokenAuthenticator
import com.example.androidnativeshowcase.core.security.SecureTokenStore
import com.example.androidnativeshowcase.core.security.TokenStore
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TokenModule {
    @Binds
    @Singleton
    abstract fun bindTokenStore(store: SecureTokenStore): TokenStore
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ShowcaseDatabase {
        return Room.databaseBuilder(context, ShowcaseDatabase::class.java, "showcase.db")
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    fun provideItemDao(database: ShowcaseDatabase): ShowcaseItemDao = database.itemDao()

    @Provides
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        authenticator: TokenAuthenticator,
        demoBackendInterceptor: DemoBackendInterceptor,
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .authenticator(authenticator)
            .addInterceptor(logging)
            .addInterceptor(demoBackendInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideShowcaseApi(retrofit: Retrofit): ShowcaseApi {
        return retrofit.create(ShowcaseApi::class.java)
    }
}
