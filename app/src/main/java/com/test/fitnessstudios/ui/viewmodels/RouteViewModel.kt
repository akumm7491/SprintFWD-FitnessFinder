package com.test.fitnessstudios.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.test.fitnessstudios.data.models.routes.Route
import com.test.fitnessstudios.data.repositories.routes.RoutesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

data class RoutesUiState(
    var isLoading: Boolean = false,
    var errorFetching: String? = null,
    var route: Route? = null
)

@HiltViewModel
class RouteViewModel @Inject constructor(
    private val routesRepository: RoutesRepository
) : ViewModel() {

    private val _routeUiState: MutableStateFlow<RoutesUiState> = MutableStateFlow(RoutesUiState())

    private fun setIsLoading(isLoading: Boolean) {
        _routeUiState.update {it.copy(isLoading = isLoading) }
    }

    private fun setError(error: String?) {
        _routeUiState.update { it.copy(errorFetching = error,  isLoading = false) }
    }

    private fun setRoute(route: Route?) {
        _routeUiState.update { it.copy(route = route, isLoading = false) }
    }

    // Expose screen UI state and helper functions
    val routeUiState: StateFlow<RoutesUiState> = _routeUiState.asStateFlow()

    fun clearError() {
        setError(null)
    }

    // Fetch route from a lat/lng pair to a lat/lng pair
    private var fetchJob: Job? = null
    fun getRoute(from: LatLng, to: LatLng): StateFlow<RoutesUiState> {
        // Update loading state
        setIsLoading(true)

        run {
            // Cancel and pending requests then request studios
            fetchJob?.cancel()
            fetchJob = viewModelScope.launch {
                try {
                    // Listen for route updates
                    routesRepository.getRoute(from, to).collect {
                        setRoute(it)
                    }
                } catch (ioe: IOException) {
                    // Handle the error and notify the UI when appropriate.
                    setError(ioe.localizedMessage)
                }
            }
        }

        return routeUiState
    }

    override fun onCleared() {
        super.onCleared()
        fetchJob?.cancel()
    }

}