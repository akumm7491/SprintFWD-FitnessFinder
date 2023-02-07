package com.test.fitnessstudios.data.sources.remote.studio.api

import android.location.Location
import com.test.fitnessstudios.data.models.studio.StudioResponse
import retrofit2.Call

// Makes studio-related network requests.
interface StudioApi {
    fun getStudiosNearby(
        location: Location,
        radius: Int = 1000,
    ): Call<StudioResponse>
}