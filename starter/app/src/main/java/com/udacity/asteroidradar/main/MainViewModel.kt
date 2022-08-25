package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.domain.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.database.AsteroidsFilter
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _imageOfTheDay = MutableLiveData<PictureOfDay?>()
    val imageOfTheDay: LiveData<PictureOfDay?> get() = _imageOfTheDay

    private val _filter = MutableLiveData(AsteroidsFilter.SHOW_WEEK_ASTEROIDS)

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
            _imageOfTheDay.value = repository.getImageOfTheDay()
        }
    }

    private fun refreshAsteroidList() {
        viewModelScope.launch {
            repository.refreshCache()
        }
    }

    fun getAsteroids(filter: AsteroidsFilter) {
        _filter.value = filter
    }
}