package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

const val DATABASE_NAME = "databaseAsteroid"

enum class AsteroidsFilter {
    SHOW_WEEK_ASTEROIDS,
    SHOW_TODAY_ASTEROIDS,
    SHOW_SAVED_ASTEROIDS,
}

@Dao
interface AsteroidDao {
    @Query("SELECT * FROM $DATABASE_NAME ORDER BY closeApproachDate ASC")
    fun getAsteroidFeed(): LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg asteroid: DatabaseAsteroid)
}

@Database(entities = [DatabaseAsteroid::class], version = 1)
abstract class AsteroidDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}

private lateinit var INSTANCE: AsteroidDatabase

fun getDatabase(context: Context): AsteroidDatabase {
    synchronized(AsteroidDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context, AsteroidDatabase::class.java, "asteroids")
                .fallbackToDestructiveMigration()
                .build()
        }
        return INSTANCE
    }
}