package com.test.fitnessstudios.ui

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.test.fitnessstudios.R
import com.test.fitnessstudios.databinding.ActivityMainBinding
import com.test.fitnessstudios.helpers.Constants.DEFAULT_LATITUDE
import com.test.fitnessstudios.helpers.Constants.DEFAULT_LONGITUDE
import com.test.fitnessstudios.ui.adapters.PagerAdapter
import com.test.fitnessstudios.ui.fragments.MapFragment
import com.test.fitnessstudios.ui.fragments.StudioListFragment
import com.test.fitnessstudios.ui.viewmodels.StudioViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private val studioViewModel: StudioViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    private val defaultLocation = Location("Default").apply {
        this.latitude = DEFAULT_LATITUDE
        this.longitude = DEFAULT_LONGITUDE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializePager()
        binding.tvError.setOnClickListener {
            studioViewModel.clearError()
            getNearbyStudios()
        }

        // Get studios nearby the default location
        getNearbyStudios()
    }

    private fun initializePager(){
        val pagerAdapter = PagerAdapter(supportFragmentManager, lifecycle)
        binding.pager.adapter = pagerAdapter

        // Lets disable swiping on the viewpager so it doesn't interfere with the map
        binding.pager.isUserInputEnabled = false;

        TabLayoutMediator(binding.tabs, binding.pager) { tab, position ->
            when(position){
                0 -> tab.text = "Map"
                1 -> tab.text = "List"
            }
        }.attach()
    }

    private fun getNearbyStudios(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                studioViewModel.getStudiosNearby(defaultLocation).collect { studioUiState ->
                    Log.v(TAG, "New StudioUiState: $studioUiState")
                    binding.pBar.visibility = if (studioUiState.isLoading) View.VISIBLE else View.GONE
                    if(studioUiState.errorFetching != null){
                        binding.tvError.visibility = View.VISIBLE
                        binding.tvError.text = "Failed to fetch nearby studios, tap to try again. \n${studioUiState.errorFetching}"
                    }else {
                        binding.tvError.visibility = View.GONE
                    }

                    if (studioUiState.studios.isNotEmpty()) {
                        binding.tvError.visibility = View.GONE
                        binding.pBar.visibility = View.GONE
                        binding.pagerLayout.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}
