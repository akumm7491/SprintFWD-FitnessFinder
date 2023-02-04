package com.test.fitnessstudios.ui.viewmodels

import android.location.Location
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.fitnessstudios.data.models.Studio
import com.test.fitnessstudios.data.repositories.studio.StudioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

data class StudiosUiState(
    var isLoading: Boolean = false,
    var errorFetching: String? = null,
    var studios: List<Studio> = emptyList()
)

@HiltViewModel
class StudioViewModel @Inject constructor(
    private val studioRepository: StudioRepository
) : ViewModel() {

    private val _studiosUiState: MutableStateFlow<StudiosUiState> = MutableStateFlow(StudiosUiState())

    private fun setIsLoading(isLoading: Boolean) {
        _studiosUiState.update {it.copy(isLoading = isLoading) }
    }

    private fun setError(error: String?) {
        _studiosUiState.update { it.copy(errorFetching = error,  isLoading = false) }
    }

    private fun setStudios(studios: List<Studio>) {
        _studiosUiState.update { it.copy(studios = studios, isLoading = false) }
    }

    // Expose screen UI state and helper functions
    val studiosUiState: StateFlow<StudiosUiState> = _studiosUiState.asStateFlow()

    fun clearError() {
        setError(null)
    }

    // Fetch studios within a radius of a certain location.
    private var fetchJob: Job? = null
    fun getStudiosNearby(location: Location, radius: Int = 1000): StateFlow<StudiosUiState> {
        // Update loading state
        setIsLoading(true)

        // This handler is just for demo purposes so we can see the
        // progress bar during loading.
        Handler(Looper.getMainLooper()).postDelayed({
            run {
                // Cancel and pending requests then request studios
                fetchJob?.cancel()
                fetchJob = viewModelScope.launch {
                    try {
                        // Listen for changes to nearby studios
                        studioRepository.getStudiosNearby(location, radius).collect {
                            setStudios(it)
                        }
                    } catch (ioe: IOException) {
                        // Handle the error and notify the UI when appropriate.
                        setError(ioe.localizedMessage)
                    }
                }
            }
        }, 5000); // Wait for 5 seconds to show progress bar

        return studiosUiState
    }

    override fun onCleared() {
        super.onCleared()
        fetchJob?.cancel()
    }


}