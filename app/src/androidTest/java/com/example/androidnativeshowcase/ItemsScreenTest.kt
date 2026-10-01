package com.example.androidnativeshowcase

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.androidnativeshowcase.core.data.ShowcaseItem
import com.example.androidnativeshowcase.features.items.ItemsScreen
import com.example.androidnativeshowcase.features.items.ItemsUiState
import com.example.androidnativeshowcase.ui.theme.ShowcaseTheme
import org.junit.Rule
import org.junit.Test

class ItemsScreenTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun showsCachedItems() {
        composeRule.setContent {
            ShowcaseTheme {
                ItemsScreen(
                    state = ItemsUiState(
                        isLoading = false,
                        items = listOf(
                            ShowcaseItem(
                                id = "storage",
                                title = "Room cache",
                                summary = "Read-through caching for structured app data.",
                                detail = "Detail",
                                updatedAtMillis = 1L,
                            ),
                        ),
                    ),
                    onBack = {},
                    onRefresh = {},
                    onOpenDetail = {},
                )
            }
        }

        composeRule.onNodeWithText("Room cache").assertIsDisplayed()
    }
}
