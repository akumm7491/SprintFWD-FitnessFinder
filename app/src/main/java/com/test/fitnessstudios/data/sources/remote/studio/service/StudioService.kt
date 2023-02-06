package com.test.fitnessstudios.data.sources.remote.studio.service

import android.location.Location
import com.test.fitnessstudios.data.sources.remote.studio.api.StudioApi
import com.test.fitnessstudios.data.models.studio.StudioResponse
import com.test.fitnessstudios.data.sources.remote.studio.api.YelpNetwork
import retrofit2.Call
import javax.inject.Inject

class StudioService @Inject constructor(): StudioApi {

    // Implement the studio API and call the YelpNetwork to get the studios from there.
    override fun getStudiosNearby(location: Location, radius: Int): Call<StudioResponse> {
        return YelpNetwork.retrofit.getStudiosNearby(location.latitude, location.longitude)
    }
}