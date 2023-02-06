package com.test.fitnessstudios.data.repositories.studio

import android.location.Location
import android.util.Log
import com.test.fitnessstudios.data.models.studio.Studio
import com.test.fitnessstudios.data.models.studio.StudioResponse
import com.test.fitnessstudios.data.sources.remote.studio.StudioRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class StudioRepository @Inject constructor(
    private val studioRemoteDataSource: StudioRemoteDataSource, // network
    private val ioDispatcher: CoroutineDispatcher
) {

    private val TAG = "StudioRepository"

    // Cache of the latest posts got from the network.
    val latestStudios: MutableStateFlow<List<Studio>> = MutableStateFlow(emptyList())

    private suspend fun refreshStudiosFromNetwork(location: Location, radius: Int): MutableStateFlow<List<Studio>> {
        withContext(ioDispatcher) {
            studioRemoteDataSource.getStudiosNearby(location, radius)
                .enqueue(object: Callback<StudioResponse> {
                    override fun onResponse(
                        call: Call<StudioResponse>,
                        response: Response<StudioResponse>
                    ) {
                        response.body()?.let { studioResponse: StudioResponse ->
                            Log.d(TAG, "Got ${studioResponse.studios.size} studios!")
                            latestStudios.value = studioResponse.studios
                        } ?: {
                            Log.e(TAG, "Received an empty body from call $call")
                        }
                    }

                    override fun onFailure(call: Call<StudioResponse>, t: Throwable) {
                        Log.e(TAG , "Failed to get nearby studios: ${t.localizedMessage}")
                    }
                }
            )
        }
        return latestStudios
    }

    suspend fun getStudiosNearby(location: Location, radius: Int = 1000, refresh: Boolean = true): Flow<List<Studio>> {
        return if(latestStudios.value.isEmpty() || refresh){
            refreshStudiosFromNetwork(location, radius)
        }else {
            latestStudios
        }
    }
}