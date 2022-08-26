package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import retrofit2.HttpException
import java.lang.Exception
import java.util.concurrent.TimeUnit

class RefreshDataWorker(appContext: Context, params: WorkerParameters): CoroutineWorker(appContext, params) {
    companion object {
        const val WORK_REPEAT_INTERVAL: Long = 1
        const val TAG = "REFRESH_DATA_WORKER"
        val TIME_UNIT = TimeUnit.DAYS
    }
    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = AsteroidRepository(database)

        return try {
            // cache today's asteroids
            repository.refreshCacheForToday()
            // delete old asteroids from the cache
            repository.deleteOldAsteroids()
            // return success
            Result.success()
        } catch (ex: HttpException) {
            // retry the work
            Result.retry()
        } catch (ex: Exception) {
            // fail the owrk
            Result.failure()
        }
    }
}