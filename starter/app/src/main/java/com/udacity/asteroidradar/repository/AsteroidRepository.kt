package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.getNextSevenDaysFormattedDates
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.PictureOfDay
import com.udacity.asteroidradar.domain.asDatabaseModel
import org.json.JSONObject

enum class FeedStatus {
    LOADING,
    LOADED,
    ERROR,
}

/**
 * AsteroidRepository handles refreshing the cache and fetching all the asteroids from cache
 * And also, presenting a unified front for the UI.
 **/
class AsteroidRepository(private val database: AsteroidDatabase) {
    /**
     * A list of asteroids that can be shown on the screen
     * This list contains all the asteroids saved in the database
     */
    val savedAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getSavedAsteroidFeed()) {
            it.asDomainModel()
        }

    /**
     * A list of asteroids that can be shown on the screen
     * This list contains only today's asteroids
     */
    val todayAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getTodayAsteroidFeed(date = getNextSevenDaysFormattedDates().first())) {
            it.asDomainModel()
        }

    /**
     * A list of asteroids that can be shown on the screen
     * This list contains the whole week asteroids, starting from today
     */
    val weekAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(
            database.asteroidDao.getWeekAsteroidFeed(
                startDate = getNextSevenDaysFormattedDates().first(),
                endDate = getNextSevenDaysFormattedDates().last()
            ),
        ) {
            it.asDomainModel()
        }

    suspend fun getImageOfTheDay(): PictureOfDay {
        return AsteroidApi.service.getAsteroidImageOfTheDay(apiKey = "DEMO_KEY")
    }

    /**
     * Refresh the asteroids stored in the offline cache.
     *
     * This function doesn't use the IO dispatcher because Room handles the switching of threads
     * for us
     *
     * To actually load the asteroids for use, observe [asteroids]
     */
    suspend fun refreshCache() {
        val formattedDateList = getNextSevenDaysFormattedDates()
        val asteroidsAsString = AsteroidApi.service.getAsteroidFeed(
            startDate = formattedDateList.first(),
            endDate = formattedDateList.last(),
            apiKey = "DEMO_KEY",
        )
        val asteroids = parseAsteroidsJsonResult(JSONObject(asteroidsAsString))
        database.asteroidDao.insertAll(*asteroids.asDatabaseModel())
    }

    /**
     * Refresh the asteroids stored in the offline cache.
     *
     * This function doesn't use the IO dispatcher because Room handles the switching of threads
     * for us
     *
     * To actually load the asteroids for use, observe [asteroids]
     */
    suspend fun refreshCacheForToday() {
        val formattedDateList = getNextSevenDaysFormattedDates()
        val asteroidsAsString = AsteroidApi.service.getAsteroidFeed(
            startDate = formattedDateList.first(),
            endDate = formattedDateList.first(),
            apiKey = "DEMO_KEY",
        )
        val asteroids = parseAsteroidsJsonResult(JSONObject(asteroidsAsString))
        database.asteroidDao.insertAll(*asteroids.asDatabaseModel())
    }

    /**
     * Delete the old asteroids from the offline cache
     *
     * This function doesn't use the IO dispatcher because Room handles the switching of threads
     * for us
     *
     */
    suspend fun deleteOldAsteroids() {
        val formattedDateList = getNextSevenDaysFormattedDates()
        database.asteroidDao.deleteOldAsteroids(formattedDateList.first())
    }

}