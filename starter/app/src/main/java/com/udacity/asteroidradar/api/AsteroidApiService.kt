package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants.BASE_URL
import com.udacity.asteroidradar.domain.PictureOfDay
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 *retrofit instance to make REST calls to the internet
 */
private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()

/**
 * API interface that retrofit will implement and our app will use
 * This interface defines all the calls our app will make using a particular [BASE_URL]
 */
interface AsteroidApiService {
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroidFeed(@Query("start_date") startDate: String, @Query("end_date") endDate: String, @Query("api_key") apiKey: String): String

    @GET("planetary/apod")
    suspend fun getAsteroidImageOfTheDay(@Query("api_key") apiKey: String): PictureOfDay
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