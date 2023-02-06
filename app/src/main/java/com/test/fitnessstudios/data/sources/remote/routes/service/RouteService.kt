package com.test.fitnessstudios.data.sources.remote.routes.service

import com.google.android.gms.maps.model.LatLng
import com.test.fitnessstudios.data.models.routes.LatLngQuery
import com.test.fitnessstudios.data.models.routes.RouteResponse
import com.test.fitnessstudios.data.sources.remote.routes.api.GoogleNetwork
import com.test.fitnessstudios.data.sources.remote.routes.api.RouteApi
import retrofit2.Call
import javax.inject.Inject

class RouteService @Inject constructor(): RouteApi {

    // Implement the route API and call the GoogleNetwork to get the route
    override fun getRoute(fromLocation: LatLng, toLocation: LatLng): Call<RouteResponse> {
        val fromLatLng = LatLngQuery(fromLocation.latitude, fromLocation.longitude)
        val toLatLng = LatLngQuery(toLocation.latitude, toLocation.longitude)
        return GoogleNetwork.retrofit.getRoute(fromLatLng, toLatLng)
    }
}