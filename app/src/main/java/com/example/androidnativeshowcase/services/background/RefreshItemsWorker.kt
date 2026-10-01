package com.example.androidnativeshowcase.services.background

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.example.androidnativeshowcase.core.data.ItemsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RefreshItemsWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: ItemsRepository,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): ListenableWorker.Result {
        return when (repository.refreshItems()) {
            is com.example.androidnativeshowcase.core.common.Result.Success -> ListenableWorker.Result.success()
            is com.example.androidnativeshowcase.core.common.Result.Error -> {
                if (runAttemptCount < 2) {
                    ListenableWorker.Result.retry()
                } else {
                    ListenableWorker.Result.failure()
                }
            }
        }
    }

    companion object {
        const val UNIQUE_WORK_NAME = "refresh_showcase_items"
    }
}
