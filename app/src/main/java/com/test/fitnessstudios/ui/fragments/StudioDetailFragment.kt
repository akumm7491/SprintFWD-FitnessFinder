package com.test.fitnessstudios.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
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

    private var locationPermissionGranted = false
    private var currentRoute: Route? = null
    private var currentRoutePolyline = PolylineOptions().apply {
        width(12f)
        color(Color.BLUE)
        geodesic(true)
    }


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
        _binding = FragmentStudioDetailBinding.inflate(inflater, container, false)

        routeViewModel = ViewModelProvider(requireActivity())[RouteViewModel::class.java]

        // Get the selected studio from arguments.
        arguments?.getString(Constants.KEY_SELECTED_STUDIO)?.let { selectedStudioString ->
            selectedStudio = Gson().fromJson(
                selectedStudioString,
                Studio::class.java
            )
        } ?: run {
            Log.e(TAG, "Selected studio argument was empty...")
        }

        return binding.root

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
            getRouteToStudio()
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
    private fun getRouteToStudio() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                val studioLatLng = LatLng(
                    selectedStudio.coordinates.latitude,
                    selectedStudio.coordinates.longitude
                )

                // Get the users last location
//                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
//                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
//                    val currentLocation = LatLng(
//                        location.latitude,
//                        location.longitude
//                    )
//                }

                val currentLocation = LatLng(
                    33.124155,
                    -111.105792
                )
                routeViewModel.getRoute(studioLatLng, currentLocation).collect { routeUiState ->
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
            currentRoutePolyline.points.clear()


            // Add all the new route points to the polyline
            for(leg in route.legs) {
                for(step in leg.steps){
                    currentRoutePolyline.addAll(PolyUtil.decode(step.polyline.points))
                }
            }

            // Draw the route on the map and animate zoom to include the whole route.
            map.addPolyline(currentRoutePolyline)
            zoomToBounds(getRouteBounds(currentRoutePolyline.points), 100)

        } ?: run {
            // Current route is null so remove any previous route polyline still on the map.
            Log.e(TAG, "Route ")
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

    private fun initializeMapConfiguration() {
        map.uiSettings.isCompassEnabled = true
        map.uiSettings.isRotateGesturesEnabled = true

        // The location layer requires permissions to be shown
        map.uiSettings.isMyLocationButtonEnabled = true
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
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