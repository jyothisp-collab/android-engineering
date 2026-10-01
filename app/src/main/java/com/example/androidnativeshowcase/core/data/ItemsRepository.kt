package com.example.androidnativeshowcase.core.data

import com.example.androidnativeshowcase.core.common.Result
import com.example.androidnativeshowcase.core.database.ShowcaseItemDao
import com.example.androidnativeshowcase.core.database.asDomain
import com.example.androidnativeshowcase.core.database.asEntity
import com.example.androidnativeshowcase.core.network.ShowcaseApi
import com.example.androidnativeshowcase.core.network.model.asDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemsRepository @Inject constructor(
    private val api: ShowcaseApi,
    private val dao: ShowcaseItemDao,
) {
    fun observeItems(): Flow<List<ShowcaseItem>> = dao.observeItems()
        .map { entities -> entities.map { it.asDomain() } }

    fun observeItem(id: String): Flow<ShowcaseItem?> = dao.observeItem(id)
        .map { it?.asDomain() }

    suspend fun refreshItems(): Result<List<ShowcaseItem>> {
        return runCatching {
            val items = api.getItems().map { it.asDomain() }
            dao.upsertAll(items.map { it.asEntity() })
            items
        }.fold(
            onSuccess = { Result.Success(it) },
            onFailure = { Result.Error("Could not refresh items", it) },
        )
    }

    suspend fun refreshItem(id: String): Result<ShowcaseItem> {
        return runCatching {
            val item = api.getItem(id).asDomain()
            dao.upsertAll(listOf(item.asEntity()))
            item
        }.fold(
            onSuccess = { Result.Success(it) },
            onFailure = { Result.Error("Could not refresh item", it) },
        )
    }
}
