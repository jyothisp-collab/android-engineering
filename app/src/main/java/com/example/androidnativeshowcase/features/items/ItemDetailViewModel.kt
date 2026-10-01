package com.example.androidnativeshowcase.features.items

import androidx.lifecycle.SavedStateHandle
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

data class ItemDetailUiState(
    val isLoading: Boolean = true,
    val item: ShowcaseItem? = null,
    val errorMessage: String? = null,
)

@HiltViewModel
class ItemDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ItemsRepository,
) : ViewModel() {
    private val itemId: String = checkNotNull(savedStateHandle["itemId"])
    private val refreshState = MutableStateFlow(RefreshState(isLoading = true))

    val uiState: StateFlow<ItemDetailUiState> = combine(
        repository.observeItem(itemId),
        refreshState,
    ) { item, refresh ->
        ItemDetailUiState(
            isLoading = refresh.isLoading && item == null,
            item = item,
            errorMessage = refresh.errorMessage,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ItemDetailUiState())

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            refreshState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = repository.refreshItem(itemId)) {
                is Result.Success -> refreshState.update { it.copy(isLoading = false) }
                is Result.Error -> refreshState.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    private data class RefreshState(
        val isLoading: Boolean,
        val errorMessage: String? = null,
    )
}
