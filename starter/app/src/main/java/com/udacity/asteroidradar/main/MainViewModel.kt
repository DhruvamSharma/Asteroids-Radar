package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception

class MainViewModel : ViewModel() {
    private val _response = MutableLiveData<List<Asteroid>>()
    val response: LiveData<List<Asteroid>>
        get() = _response


    private val _imageOfTheDay = MutableLiveData<PictureOfDay?>()
    val imageOfTheDay: LiveData<PictureOfDay?> get() = _imageOfTheDay

    init {
        fetchAllAsteroids()
        fetchImageOfTheDay()
    }

    private fun fetchImageOfTheDay() {
        viewModelScope.launch {
            try {
                _imageOfTheDay.value = AsteroidApi.service.getAsteroidImageOfTheDay()
            } catch (ex: Exception) {
                _imageOfTheDay.value = null
            }
        }
    }

    private fun fetchAllAsteroids() {
        viewModelScope.launch {
           try {
               val response = AsteroidApi.service.getAsteroidFeed()
               _response.value = parseAsteroidsJsonResult(JSONObject(response))
           } catch(ex: Exception) {
               _response.value = mutableListOf()
           }
        }
    }
}