package com.test.fitnessstudios.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.test.fitnessstudios.R
import com.test.fitnessstudios.data.models.Studio
import com.test.fitnessstudios.databinding.FragmentMapBinding
import com.test.fitnessstudios.helpers.Constants.DEFAULT_LATITUDE
import com.test.fitnessstudios.helpers.Constants.DEFAULT_LONGITUDE
import com.test.fitnessstudios.ui.viewmodels.MainViewModel
import com.test.fitnessstudios.ui.viewmodels.StudioViewModel
import kotlinx.coroutines.launch


class MapFragment : Fragment(R.layout.fragment_map) {
    private val TAG = "MapFragment"

    private var _binding: FragmentMapBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var map: GoogleMap
    private lateinit var studioViewModel: StudioViewModel
    private lateinit var mainViewModel: MainViewModel
    private var studioMarkers = ArrayList<Marker>()
    private var currentStudios = emptyList<Studio>()

    // Default Camera Position
    private val defaultCameraPosition = CameraPosition(
        LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE),
        12.0f, // Roughly city level
        0f, // No tilt
        0f // default bearing
    )

    private var locationPermissionGranted = false
    @SuppressLint("MissingPermission")
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            locationPermissionGranted = isGranted
            Log.d(TAG, "Location permission granted: $isGranted")

            // Enable the location layer if the permissions are granted
            map.isMyLocationEnabled = isGranted
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Check initial location permissions
        checkLocationPermissions()

        // Initialize ViewModel to listen for studio updates
        studioViewModel = ViewModelProvider(requireActivity())[StudioViewModel::class.java]
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        // Initialize map fragment and get the map asynchronously
        val supportMapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        supportMapFragment?.getMapAsync { map ->
            this.map = map
            initializeMapConfiguration()

            // Start listening for studio updates now that the map is initialized.
            listenForStudioUpdates()
        }
    }

    private fun checkLocationPermissions() {
        locationPermissionGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun listenForStudioUpdates() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                studioViewModel.studiosUiState.collect { studioUiState ->
                    Log.v(TAG, "New StudioUiState in MapFragment: $studioUiState")
                    currentStudios = studioUiState.studios
                    updateMapWithStudios(currentStudios)
                }
            }
        }
    }

    private fun initializeMapConfiguration() {
        map.animateCamera(CameraUpdateFactory.newCameraPosition(defaultCameraPosition))
        map.uiSettings.isCompassEnabled = true
        map.uiSettings.isRotateGesturesEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        map.setOnMyLocationClickListener {
            if(locationPermissionGranted){
                zoomToLatLng(LatLng(it.latitude, it.longitude), 10f)
            }else {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        map.setOnMarkerClickListener { marker ->
            val studioId = marker.tag as String
            val clickedStudio = currentStudios.first { it.id == studioId }
            Log.d(TAG, "Clicked on studio: $clickedStudio")
            zoomToLatLng(
                LatLng(
                    clickedStudio.coordinates.latitude,
                    clickedStudio.coordinates.longitude
                ),
                14f
            )
            marker.showInfoWindow()
            true
        }

        map.setOnInfoWindowClickListener { marker ->
            val studioId = marker.tag as String
            val clickedStudio = currentStudios.first { it.id == studioId }
            Log.d(TAG, "Clicked on studio info window: $clickedStudio")

            // Update the selected studio which will trigger the studio detail fragment to show
            mainViewModel.setStudioDetail(clickedStudio)
        }
    }

    private fun updateMapWithStudios(studios: List<Studio>){
        // Remove all previous markers
        studioMarkers.clear()
        map.clear()

        for(studio in studios){
            map.addMarker(createMapMarker(studio))?.let { studioMarker ->
                // set the tag for the marker to the studio id
                // so we can reference the studio on marker click.
                studioMarker.tag = studio.id
                studioMarkers.add(studioMarker)
            }
        }

        // Animate camera to include all studios with 100 pixel buffer around the edges
        // of the bounds of studios
        zoomToBounds(getAllStudiosLatLngBounds(), 100)
    }

    private fun zoomToBounds(latLngBounds: LatLngBounds, padding: Int){
        map.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                latLngBounds,
                padding)
        )
    }

    private fun zoomToLatLng(latLng: LatLng, zoom: Float){
        // Animate camera to specified latLng
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLng, zoom
            )
        )
    }

    private fun getAllStudiosLatLngBounds(): LatLngBounds {
        val builder = LatLngBounds.Builder()
        for (marker in studioMarkers) {
            builder.include(marker.position)
        }
        return builder.build()
    }

    private fun createMapMarker(studio: Studio): MarkerOptions{
        // Initialize marker options
        val markerOptions = MarkerOptions()
        // Set position of marker
        markerOptions.position(
            LatLng(
                studio.coordinates.latitude,
                studio.coordinates.longitude
            )
        )
        // Set title of marker
        markerOptions.title(studio.name)
        return markerOptions
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}