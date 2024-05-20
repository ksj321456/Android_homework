package com.example.week12_practice

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineScope

class MyWorker(private val context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result{
        val repo = MyRepository(context)
        repo.valueInternal = "New value"
        return Result.success()
    }
}