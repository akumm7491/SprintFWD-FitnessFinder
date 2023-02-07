package com.test.fitnessstudios.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.google.maps.android.PolyUtil
import com.test.fitnessstudios.R
import com.test.fitnessstudios.data.models.routes.Route
import com.test.fitnessstudios.data.models.studio.Studio
import com.test.fitnessstudios.databinding.FragmentStudioDetailBinding
import com.test.fitnessstudios.helpers.Constants
import com.test.fitnessstudios.ui.viewmodels.RouteViewModel
import kotlinx.coroutines.launch

class StudioDetailFragment: Fragment(R.layout.fragment_studio_detail) {

    private val TAG = "StudioDetailFragment"

    private var _binding: FragmentStudioDetailBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var map: GoogleMap
    private lateinit var selectedStudio: Studio
    private lateinit var routeViewModel: RouteViewModel
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var locationPermissionGranted = false
    private var currentRoute: Route? = null
    private var currentRoutePolyline: Polyline? = null
    private var lastLocation: Location? = null

    @SuppressLint("MissingPermission")
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            locationPermissionGranted = isGranted
            Log.d(TAG, "Location permission granted: $isGranted")

            // Enable the location layer if the permissions are granted and the
            // map has been initialized already
            if(::map.isInitialized){
                map.isMyLocationEnabled = isGranted
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudioDetailBinding.inflate(inflater, container, false)

        routeViewModel = ViewModelProvider(requireActivity())[RouteViewModel::class.java]
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Get the selected studio from arguments.
        arguments?.getString(Constants.KEY_SELECTED_STUDIO)?.let { selectedStudioString ->
            selectedStudio = Gson().fromJson(
                selectedStudioString,
                Studio::class.java
            )
        } ?: run {
            Log.e(TAG, "Selected studio argument was empty...")
        }

        // Get the last known location so we can request a route.
        getLastLocation()

        return binding.root

    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {location ->
            if(location != null){
                lastLocation = location

                // Now that we have the last location, get a route to the studio.
                getRouteToStudio(
                    LatLng(
                        selectedStudio.coordinates.latitude,
                        selectedStudio.coordinates.longitude
                    ),
                    LatLng(
                        location.latitude,
                        location.longitude
                    )
                )
            }else {
                Log.e(TAG, "Received a new location, but it was null")
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize map fragment and get the map asynchronously
        val supportMapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        supportMapFragment?.getMapAsync { map ->
            this.map = map
            initializeMapConfiguration()
            updateStudioDetails()
            showStudioOnMap()
        }

        binding.btnCallBusiness.setOnClickListener {
            startActivity(Intent(
                Intent.ACTION_DIAL,
                Uri.fromParts(
                    "tel",
                    selectedStudio.phone,
                    null)
            ))
        }
    }

    @SuppressLint("MissingPermission")
    private fun getRouteToStudio(studioLatLng: LatLng, currentLatLng: LatLng) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                routeViewModel.getRoute(studioLatLng, currentLatLng).collect { routeUiState ->
                    Log.v(TAG, "New RouteUiState in StudioDetailFragment: $routeUiState")
                    currentRoute = routeUiState.route
                    updateMapWithRoute()
                }
            }
        }
    }

    private fun updateMapWithRoute() {
        currentRoute?.let {route ->
            if(route.legs.isEmpty()){
                Log.d(TAG, "Current route has no legs so not drawing a route")
                return
            }

            // Remove any previous points in the polyline
            currentRoutePolyline?.remove()


            val routePolylineBuilder = PolylineOptions().apply {
                width(12f)
                color(Color.BLUE)
                geodesic(true)
            }
            // Add all the new route points to the polyline
            for(leg in route.legs) {
                for(step in leg.steps){
                    routePolylineBuilder.addAll(PolyUtil.decode(step.polyline.points))
                }
            }

            // Draw the route on the map and animate zoom to include the whole route.
            currentRoutePolyline = map.addPolyline(routePolylineBuilder)
            zoomToBounds(getRouteBounds(routePolylineBuilder.points), 200)

        } ?: run {
            // Current route is null so remove any previous route polyline still on the map.
            Log.e(TAG, "Route is null so removing any previously drawn routes")
            // Remove any previous points in the polyline
            currentRoutePolyline?.remove()
        }
    }

    private fun getRouteBounds(points: List<LatLng>): LatLngBounds {
        // Create a new instance of LatLngBounds that includes all the route points
        // so we can zoom to fit the whole route
        val builder = LatLngBounds.Builder()
        for(point in points){
            builder.include(point)
        }
        return builder.build()

    }

    private fun showStudioOnMap() {
        val studioCameraPosition = CameraPosition(
            LatLng(
                selectedStudio.coordinates.latitude,
                selectedStudio.coordinates.longitude
            ),
            16.0f, // Roughly street level
            0f, // No tilt
            0f // default bearing
        )

        // Set the camera position to the
        map.moveCamera(CameraUpdateFactory.newCameraPosition(studioCameraPosition))
        map.clear()
        map.addMarker(createMapMarker(selectedStudio))
    }

    private fun updateStudioDetails(){
        binding.tvStudioName.text = selectedStudio.name
        Glide.with(requireContext())
            .load(selectedStudio.image_url)
            .fitCenter()
            .placeholder(R.drawable.download_icon)
            .error(R.drawable.broken_image)
            .into(binding.ivStudioImages)
    }

    @SuppressLint("MissingPermission")
    private fun initializeMapConfiguration() {
        map.uiSettings.isCompassEnabled = true
        map.uiSettings.isRotateGesturesEnabled = true

        // The location layer requires permissions to be shown
        if(locationPermissionGranted){
            map.uiSettings.isMyLocationButtonEnabled = true
            map.isMyLocationEnabled = true
        }else {
            map.uiSettings.isMyLocationButtonEnabled = false
            map.isMyLocationEnabled = false
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        map.setOnMyLocationClickListener {
            if(locationPermissionGranted){
                zoomToLatLng(LatLng(it.latitude, it.longitude), 10f)
            }else {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun zoomToLatLng(latLng: LatLng, zoom: Float){
        // Animate camera to specified latLng
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLng, zoom
            )
        )
    }

    private fun zoomToBounds(latLngBounds: LatLngBounds, padding: Int){
        map.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                latLngBounds,
                padding)
        )
    }

    private fun createMapMarker(studio: Studio): MarkerOptions {
        return MarkerOptions().apply {
            position(LatLng(
                studio.coordinates.latitude,
                studio.coordinates.longitude
            ))
            title(studio.name)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}