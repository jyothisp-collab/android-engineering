package com.example.androidnativeshowcase.core.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ShowcaseItemDao {
    @Query("SELECT * FROM showcase_items ORDER BY updatedAtMillis DESC")
    fun observeItems(): Flow<List<ShowcaseItemEntity>>

    @Query("SELECT * FROM showcase_items WHERE id = :id")
    fun observeItem(id: String): Flow<ShowcaseItemEntity?>

    @Query("SELECT * FROM showcase_items ORDER BY updatedAtMillis DESC")
    suspend fun getItems(): List<ShowcaseItemEntity>

    @Upsert
    suspend fun upsertAll(items: List<ShowcaseItemEntity>)

    @Query("DELETE FROM showcase_items")
    suspend fun clear()
}
