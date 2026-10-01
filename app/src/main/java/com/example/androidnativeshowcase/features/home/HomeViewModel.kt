package com.example.androidnativeshowcase.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.androidnativeshowcase.services.background.RefreshItemsWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val workManager: WorkManager,
) : ViewModel() {
    fun enqueueRefresh() {
        viewModelScope.launch {
            val request = OneTimeWorkRequestBuilder<RefreshItemsWorker>().build()
            workManager.enqueueUniqueWork(
                RefreshItemsWorker.UNIQUE_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                request,
            )
        }
    }
}
