package ch.tanchuca.daa_labo4

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class CacheCleaningWork(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    private val imageDir = "/img/"

    override fun doWork(): Result {
        // Empty local cache
        val cacheDir = applicationContext.cacheDir
        for (file in cacheDir.listFiles()!!) {
            if (file.isDirectory && file.name.equals(imageDir)) {
                file.listFiles()?.forEach { it.delete() }
            }
        }
        cacheDir.listFiles()?.forEach { it.delete() }
        return Result.success()
    }
}