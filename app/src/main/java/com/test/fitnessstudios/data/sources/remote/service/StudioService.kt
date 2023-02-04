package com.test.fitnessstudios.data.sources.remote.service

import android.location.Location
import com.test.fitnessstudios.data.sources.remote.api.StudioApi
import com.test.fitnessstudios.data.sources.remote.api.StudioResponse
import com.test.fitnessstudios.data.sources.remote.api.YelpNetwork
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject


class StudioService @Inject constructor(): StudioApi {

    // Implement the studio API and call the YelpNetwork to get the studios from there.
    override fun getStudiosNearby(location: Location, radius: Int): Call<StudioResponse> {
        return YelpNetwork.retrofit.getStudiosNearby(location.latitude, location.longitude)
    }
}