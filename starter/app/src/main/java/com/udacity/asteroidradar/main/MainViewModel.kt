package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.domain.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.database.AsteroidsFilter
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.repository.AsteroidRepository
import com.udacity.asteroidradar.repository.FeedStatus
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _imageOfTheDay = MutableLiveData<PictureOfDay?>()
    val imageOfTheDay: LiveData<PictureOfDay?> get() = _imageOfTheDay

    private val _filter = MutableLiveData(AsteroidsFilter.SHOW_WEEK_ASTEROIDS)

    private val _feedStatus = MutableLiveData(FeedStatus.LOADING)
    val feedStatus: LiveData<FeedStatus> get() = _feedStatus

    private val database = getDatabase(application.applicationContext)
    private val repository = AsteroidRepository(database)

    val asteroids = Transformations.switchMap(_filter) {
        when (it) {
            AsteroidsFilter.SHOW_WEEK_ASTEROIDS -> repository.weekAsteroids
            AsteroidsFilter.SHOW_TODAY_ASTEROIDS -> repository.todayAsteroids
            AsteroidsFilter.SHOW_SAVED_ASTEROIDS -> repository.savedAsteroids
        }
    }

    init {
        refreshAsteroidList()
        fetchImageOfTheDay()
    }

    private fun fetchImageOfTheDay() {
        viewModelScope.launch {
            try {
                _imageOfTheDay.value = repository.getImageOfTheDay()
            } catch (ex: Exception) {
                // do nothing
            }
        }
    }

    private fun refreshAsteroidList() {
        viewModelScope.launch {
            _feedStatus.value = FeedStatus.LOADING
            try {
                repository.refreshCache()
                _feedStatus.value = FeedStatus.LOADED
            } catch (ex: Exception) {
                _feedStatus.value = FeedStatus.ERROR
            }
        }
    }

    fun getAsteroids(filter: AsteroidsFilter) {
        _filter.value = filter
    }

    fun updateFeedStatus(feedStatus: FeedStatus) {
        _feedStatus.value = feedStatus
    }
}