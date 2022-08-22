package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {
    private val _response = MutableLiveData<String>()
    val response: LiveData<String>
        get() = _response

    init {
        fetchAllAsteroids()
    }

    private fun fetchAllAsteroids() {
        viewModelScope.launch {
            AsteroidApi.service.getAsteroidFeed().enqueue(object: Callback<JSONObject> {
                override fun onResponse(call: Call<JSONObject>, response: Response<JSONObject>) {
                    _response.value = parseAsteroidsJsonResult(response.body()).first().codename
                }

                override fun onFailure(call: Call<JSONObject>, t: Throwable) {
                    _response.value = "error: ${t.message}"
                }
            })

        }
    }
}