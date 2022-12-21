package ch.tanchuca.daa_labo4

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.io.IOException

class CacheCleaningWork(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    private val imagesCacheDir = File(appContext.cacheDir, "images")

    override suspend fun doWork(): Result = CoroutineScope(Dispatchers.IO).run {
        println("Cleaning cache")
        // Empty local cache
        try {
            imagesCacheDir.listFiles()?.forEach { it.delete() }
        } catch (e: IOException) {
            return Result.failure()
        }
        return Result.success()
    }
}