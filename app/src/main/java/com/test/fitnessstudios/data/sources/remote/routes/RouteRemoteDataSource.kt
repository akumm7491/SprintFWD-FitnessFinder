package com.test.fitnessstudios.data.sources.remote.routes

import com.google.android.gms.maps.model.LatLng
import com.test.fitnessstudios.data.models.routes.RouteResponse
import com.test.fitnessstudios.data.sources.remote.routes.service.RouteService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.Call
import javax.inject.Inject

/**
 * Handles getting a route from the network.
 * We move the execution to an IO-optimized thread since the RouteApiService
 * doesn't support coroutines and makes synchronous requests.
 *
 * Calls in this class execute on an IO-optimized thread pool, the class is main-safe.
 */
class RouteRemoteDataSource @Inject constructor(
    private val routeService: RouteService,
    private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun getRoute(fromLocation: LatLng, toLocation: LatLng): Call<RouteResponse> =
        withContext(ioDispatcher) {
            routeService.getRoute(fromLocation, toLocation)
        }
}