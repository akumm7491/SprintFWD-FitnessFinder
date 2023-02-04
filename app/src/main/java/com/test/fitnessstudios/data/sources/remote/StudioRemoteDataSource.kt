package com.test.fitnessstudios.data.sources.remote

import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.test.fitnessstudios.data.models.Studio
import com.test.fitnessstudios.data.sources.remote.api.StudioResponse
import com.test.fitnessstudios.data.sources.remote.api.YelpNetwork
import com.test.fitnessstudios.data.sources.remote.service.StudioService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


/**
 * Handles Creating, Reading, Updating and Deleting posts from the network.
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