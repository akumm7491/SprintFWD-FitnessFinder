package com.test.fitnessstudios.data.sources.remote.studio.api

import com.test.fitnessstudios.data.models.studio.StudioResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface YelpAPI{

    @GET("businesses/search?categories=fitness&sort_by=distance&limit=20")
    fun getStudiosNearby(
        @Query("latitude") latitude: Double?,
        @Query("longitude") longitude: Double?,
        @Query("radius") radius: Int = 1000
    ): Call<StudioResponse>
}