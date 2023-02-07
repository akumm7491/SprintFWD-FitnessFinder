package com.test.fitnessstudios.data.sources.remote.studio

import android.location.Location
import com.test.fitnessstudios.data.models.studio.StudioResponse
import com.test.fitnessstudios.data.sources.remote.studio.service.StudioService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.Call
import javax.inject.Inject


/**
 * Handles getting nearby studios from the network.
 * We move the execution to an IO-optimized thread since the YelpApiService
 * doesn't support coroutines and makes synchronous requests.
 *
 * Calls in this class execute on an IO-optimized thread pool, the class is main-safe.
 */
class StudioRemoteDataSource @Inject constructor(
    private val studioService: StudioService,
    private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun getStudiosNearby(location: Location, radius: Int = 1000): Call<StudioResponse> =
        withContext(ioDispatcher) {
            studioService.getStudiosNearby(location, radius)
        }
}