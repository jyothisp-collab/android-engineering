package com.example.androidnativeshowcase.features.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidnativeshowcase.core.common.Result
import com.example.androidnativeshowcase.core.data.ItemsRepository
import com.example.androidnativeshowcase.core.data.ShowcaseItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ItemsUiState(
    val isLoading: Boolean = true,
    val items: List<ShowcaseItem> = emptyList(),
    val errorMessage: String? = null,
) {
    val isEmpty: Boolean = !isLoading && items.isEmpty() && errorMessage == null
}

@HiltViewModel
class ItemsViewModel @Inject constructor(
    private val itemsRepository: ItemsRepository,
) : ViewModel() {
    private val refreshState = MutableStateFlow(RefreshState())

    val uiState: StateFlow<ItemsUiState> = combine(
        repository.observeItems(),
        refreshState,
    ) { items, refresh ->
        ItemsUiState(
            isLoading = refresh.isLoading && items.isEmpty(),
            items = items,
            errorMessage = refresh.errorMessage,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ItemsUiState())

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            refreshState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = repository.refreshItems()) {
                is Result.Success -> refreshState.update { it.copy(isLoading = false) }
                is Result.Error -> refreshState.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    private data class RefreshState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
    )
}
