package com.test.fitnessstudios.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.test.fitnessstudios.R
import com.test.fitnessstudios.data.models.studio.Studio
import com.test.fitnessstudios.databinding.FragmentStudioDetailBinding
import com.test.fitnessstudios.helpers.Constants
import com.test.fitnessstudios.ui.viewmodels.MainViewModel
import kotlinx.coroutines.launch

class StudioDetailFragment: Fragment(R.layout.fragment_studio_detail) {

    private val TAG = "StudioDetailFragment"

    private var _binding: FragmentStudioDetailBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var map: GoogleMap
    private lateinit var selectedStudio: Studio

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
        _binding = FragmentStudioDetailBinding.inflate(inflater, container, false)

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