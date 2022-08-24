package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.domain.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _imageOfTheDay = MutableLiveData<PictureOfDay?>()
    val imageOfTheDay: LiveData<PictureOfDay?> get() = _imageOfTheDay

    private val database = getDatabase(application.applicationContext)
    private val repository = AsteroidRepository(database)

    val asteroids = repository.asteroids

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
}