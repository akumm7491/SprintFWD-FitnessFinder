package com.test.fitnessstudios.data.repositories.routes

import com.test.fitnessstudios.data.models.routes.Route
import com.test.fitnessstudios.data.sources.remote.routes.RouteRemoteDataSource
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.test.fitnessstudios.data.models.routes.RouteResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class RoutesRepository @Inject constructor(
    private val routeRemoteDataSource: RouteRemoteDataSource, // network
    private val ioDispatcher: CoroutineDispatcher
) {

    private val TAG = "RouteRepository"

    // Cache of the latest route got from the network.
    val latestRoute: MutableStateFlow<Route?> = MutableStateFlow(null)

    suspend fun getRoute(fromLocation: LatLng, toLocation: LatLng): MutableStateFlow<Route?> {
        withContext(ioDispatcher) {
            routeRemoteDataSource.getRoute(fromLocation, toLocation)
                .enqueue(object: Callback<RouteResponse> {
                    override fun onResponse(
                        call: Call<RouteResponse>,
                        response: Response<RouteResponse>
                    ) {
                        response.body()?.let { routeResponse: RouteResponse ->
                            Log.d(TAG, "Got ${routeResponse.routes.size} routes: $routeResponse")
                            if(routeResponse.routes.isNotEmpty()){
                                // Use the first route by default
                                latestRoute.value = routeResponse.routes.first()
                            }else {
                                Log.d(TAG, "Route response had an empty routes array...")
                            }
                        } ?: {
                            Log.e(TAG, "Received an empty body from call $call")
                        }
                    }

                    override fun onFailure(call: Call<RouteResponse>, t: Throwable) {
                        Log.e(TAG , "Failed to get route: ${t.localizedMessage}")
                    }
                }
            )
        }
        return latestRoute
    }
}