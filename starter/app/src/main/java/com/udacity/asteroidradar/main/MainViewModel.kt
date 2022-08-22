package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel : ViewModel() {
    private val _response = MutableLiveData<String>()
    val response: LiveData<String>
        get() = _response

    init {
        fetchAllAsteroids()
    }

    private fun fetchAllAsteroids() {
        viewModelScope.launch {
           try {
               val response = AsteroidApi.service.getAsteroidFeed()
               _response.value = parseAsteroidsJsonResult(response).first().codename
           } catch(ex: Exception) {
               _response.value = "error: ${ex.message}"
           }
        }
    }
}