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

/**
 * AsteroidRepository handles refreshing the cache and fetching all the asteroids from cache
 * And also, presenting a unified front for the UI.
 **/
class AsteroidRepository(private val database: AsteroidDatabase) {
    /**
     * A list of asteroids that can be shown on the screen.
     */
    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroidFeed()) {
            it.asDomainModel()
        }

    suspend fun getImageOfTheDay(): PictureOfDay {
        return AsteroidApi.service.getAsteroidImageOfTheDay(apiKey = "DEMO_KEY",)
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

}