package com.test.fitnessstudios.data.sources.remote.api

import android.location.Location
import retrofit2.Call

// Makes studio-related network requests.
interface StudioApi {
    fun getStudiosNearby(
        location: Location,
        radius: Int = 1000,
    ): Call<StudioResponse>
}