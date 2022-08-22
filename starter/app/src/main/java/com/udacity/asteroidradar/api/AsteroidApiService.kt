package com.udacity.asteroidradar.api

import com.udacity.asteroidradar.Constants.BASE_URL
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET


/**
 *retrofit instance to make REST calls to the internet
 */
private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(ScalarsConverterFactory.create())
    .build()

/**
 * API interface that retrofit will implement and our app will use
 * This interface defines all the calls our app will make using a particular [BASE_URL]
 */
interface AsteroidApiService {
    @GET("neo/rest/v1/feed?start_date=2015-09-07&end_date=2015-09-08&api_key=DEMO_KEY")
    suspend fun getAsteroidFeed(): JSONObject
}

/**
 * Singleton AsteroidApi which make use of the retrofit object and the
 * AsteroidApiService to make calls.
 * This object is used by view-model to make fetch the data
 */
object AsteroidApi {
    val service: AsteroidApiService by lazy {
        retrofit.create(AsteroidApiService::class.java)
    }
}