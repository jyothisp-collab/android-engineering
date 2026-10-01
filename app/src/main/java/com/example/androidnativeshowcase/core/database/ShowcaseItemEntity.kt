package com.example.androidnativeshowcase.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.androidnativeshowcase.core.data.ShowcaseItem

@Entity(tableName = "showcase_items")
data class ShowcaseItemEntity(
    @PrimaryKey val id: String,
    val title: String,
    val summary: String,
    val detail: String,
    val updatedAtMillis: Long,
)

fun ShowcaseItemEntity.asDomain() = ShowcaseItem(
    id = id,
    title = title,
    summary = summary,
    detail = detail,
    updatedAtMillis = updatedAtMillis,
)

fun ShowcaseItem.asEntity() = ShowcaseItemEntity(
    id = id,
    title = title,
    summary = summary,
    detail = detail,
    updatedAtMillis = updatedAtMillis,
)
