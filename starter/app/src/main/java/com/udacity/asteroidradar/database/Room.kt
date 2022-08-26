package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.Constants.DATABASE_FILE_NAME
import com.udacity.asteroidradar.Constants.DATABASE_NAME


enum class AsteroidsFilter {
    SHOW_WEEK_ASTEROIDS,
    SHOW_TODAY_ASTEROIDS,
    SHOW_SAVED_ASTEROIDS,
}

@Dao
interface AsteroidDao {
    @Query("SELECT * FROM $DATABASE_NAME ORDER BY closeApproachDate ASC")
    fun getSavedAsteroidFeed(): LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM $DATABASE_NAME WHERE closeApproachDate = :date ORDER BY closeApproachDate ASC")
    fun getTodayAsteroidFeed(date: String): LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM $DATABASE_NAME WHERE closeApproachDate BETWEEN :startDate AND :endDate ORDER BY closeApproachDate ASC")
    fun getWeekAsteroidFeed(startDate: String, endDate: String): LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg asteroid: DatabaseAsteroid)

    @Query("DELETE FROM $DATABASE_NAME WHERE closeApproachDate < :date")
    suspend fun deleteOldAsteroids(date: String)
}

@Database(entities = [DatabaseAsteroid::class], version = 1)
abstract class AsteroidDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}

private lateinit var INSTANCE: AsteroidDatabase

fun getDatabase(context: Context): AsteroidDatabase {
    synchronized(AsteroidDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE =
                Room.databaseBuilder(context, AsteroidDatabase::class.java, DATABASE_FILE_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
        }
        return INSTANCE
    }
}