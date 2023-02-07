package com.test.fitnessstudios.data.sources.remote.routes.api

import com.google.android.gms.maps.model.LatLng
import com.test.fitnessstudios.data.models.routes.RouteResponse
import retrofit2.Call

interface RouteApi {

    fun getRoute(
        fromLocation: LatLng,
        toLocation: LatLng,
    ): Call<RouteResponse>
}