package com.example.androidnativeshowcase.features.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.androidnativeshowcase.core.data.ShowcaseItem

@Composable
fun ItemsRoute(
    onBack: () -> Unit,
    onOpenDetail: (String) -> Unit,
    viewModel: ItemsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    ItemsScreen(
        state = state,
        onBack = onBack,
        onRefresh = viewModel::refresh,
        onOpenDetail = onOpenDetail,
    )
}

@Composable
fun ItemsScreen(
    state: ItemsUiState,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onOpenDetail: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onBack) { Text("Back") }
            Button(onClick = onRefresh, enabled = !state.isLoading) { Text("Refresh") }
        }
        Spacer(Modifier.height(12.dp))
        Text("Data list", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        when {
            state.isLoading -> LoadingMessage()
            state.errorMessage != null && state.items.isEmpty() -> StateMessage(state.errorMessage)
            state.isEmpty -> StateMessage("No cached items")
            else -> ItemList(state.items, onOpenDetail)
        }

        if (state.errorMessage != null && state.items.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Text(state.errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun LoadingMessage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Loading items" },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun StateMessage(message: String) {
    Text(
        modifier = Modifier.semantics { contentDescription = "State message" },
        text = message,
    )
}

@Composable
private fun ItemList(
    items: List<ShowcaseItem>,
    onOpenDetail: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.semantics { contentDescription = "Showcase items" },
    ) {
        items(items, key = { it.id }) { item ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenDetail(item.id) }
                    .padding(vertical = 14.dp),
            ) {
                Text(item.title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(item.summary, style = MaterialTheme.typography.bodyMedium)
            }
            Divider()
        }
    }
}
