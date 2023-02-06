package com.test.fitnessstudios.data.sources.remote.routes.api

import com.test.fitnessstudios.BuildConfig
import com.test.fitnessstudios.data.models.routes.LatLngQuery
import com.test.fitnessstudios.data.models.routes.RouteResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleAPI{

    @GET("directions/json")
    fun getRoute(
        @Query("origin") from: LatLngQuery,
        @Query("destination") to: LatLngQuery,
        @Query("key") key: String = BuildConfig.MAPS_API_KEY
    ): Call<RouteResponse>
}