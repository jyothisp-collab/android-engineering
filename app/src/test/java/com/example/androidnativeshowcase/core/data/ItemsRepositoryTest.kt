package com.example.androidnativeshowcase.core.data

import com.example.androidnativeshowcase.core.common.Result
import com.example.androidnativeshowcase.core.database.ShowcaseItemDao
import com.example.androidnativeshowcase.core.database.ShowcaseItemEntity
import com.example.androidnativeshowcase.core.network.ShowcaseApi
import com.example.androidnativeshowcase.core.network.model.ShowcaseItemResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ItemsRepositoryTest {
    @Test
    fun refreshItemsSavesToDaoAndReturnsSuccess() = runTest {
        val api = FakeApi()
        val dao = FakeDao()
        val repository = ItemsRepository(api, dao)

        val result = repository.refreshItems()

        assertTrue(result is Result.Success)
        val items = (result as Result.Success).data
        assertEquals(1, items.size)
        assertEquals("test-id", items[0].id)
        assertEquals(1, dao.savedEntities.size)
        assertEquals("test-id", dao.savedEntities[0].id)
    }

    private class FakeApi : ShowcaseApi {
        override suspend fun login(request: com.example.androidnativeshowcase.core.network.model.LoginRequest) = error("Not used")
        override suspend fun refresh(request: com.example.androidnativeshowcase.core.network.model.RefreshRequest) = error("Not used")
        
        override suspend fun getItems(): List<ShowcaseItemResponse> {
            return listOf(
                ShowcaseItemResponse("test-id", "Test Title", "Test Summary", "Detail", 123L)
            )
        }

        override suspend fun getItem(id: String): ShowcaseItemResponse {
            return ShowcaseItemResponse(id, "Test Title", "Test Summary", "Detail", 123L)
        }
    }

    private class FakeDao : ShowcaseItemDao {
        val savedEntities = mutableListOf<ShowcaseItemEntity>()
        
        override fun observeItems(): Flow<List<ShowcaseItemEntity>> = flowOf(savedEntities)
        override fun observeItem(id: String): Flow<ShowcaseItemEntity?> = flowOf(savedEntities.find { it.id == id })
        override suspend fun getItems(): List<ShowcaseItemEntity> = savedEntities
        
        override suspend fun upsertAll(items: List<ShowcaseItemEntity>) {
            savedEntities.clear()
            savedEntities.addAll(items)
        }
        
        override suspend fun clear() {
            savedEntities.clear()
        }
    }
}
