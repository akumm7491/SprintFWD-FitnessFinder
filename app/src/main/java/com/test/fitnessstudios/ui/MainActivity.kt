package com.test.fitnessstudios.ui

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.test.fitnessstudios.R
import com.test.fitnessstudios.data.models.Studio
import com.test.fitnessstudios.databinding.ActivityMainBinding
import com.test.fitnessstudios.helpers.Constants.DEFAULT_LATITUDE
import com.test.fitnessstudios.helpers.Constants.DEFAULT_LONGITUDE
import com.test.fitnessstudios.helpers.Constants.KEY_SELECTED_STUDIO
import com.test.fitnessstudios.helpers.Constants.TAG_STUDIO_DETAIL_FRAGMENT
import com.test.fitnessstudios.ui.adapters.PagerAdapter
import com.test.fitnessstudios.ui.fragments.StudioDetailFragment
import com.test.fitnessstudios.ui.viewmodels.MainViewModel
import com.test.fitnessstudios.ui.viewmodels.StudioViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private val studioViewModel: StudioViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    private val defaultLocation = Location("Default").apply {
        this.latitude = DEFAULT_LATITUDE
        this.longitude = DEFAULT_LONGITUDE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Force light mode for now.
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)

        // Initialize pager and tabs.
        initializePager()

        // Setup error text click listener so we can retry if we fail
        // to get studios.
        binding.tvError.setOnClickListener {
            studioViewModel.clearError()
            getNearbyStudios()
        }

        // Get studios near the default location
        getNearbyStudios()

        // Start listening for studio detail updates
        listenForStudioDetailUpdates()
    }

    private fun shareStudioDetails() {
        mainViewModel.studioDetail.value?.let { studioDetails ->
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, studioDetails.url)
            startActivity(Intent.createChooser(shareIntent, "Share link using"))
        }
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

    private fun listenForStudioDetailUpdates(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.studioDetail.observe(this@MainActivity) { selectedStudio ->
                    if(selectedStudio != null){
                        addStudioDetailFragment(selectedStudio)
                        hideShareDetails = false
                        invalidateOptionsMenu()
                    }else {
                        removeStudioDetailFragment()
                        hideShareDetails = true
                        invalidateOptionsMenu()
                    }
                }
            }
        }
    }
    private fun addStudioDetailFragment(selectedStudio: Studio) {
        if(supportFragmentManager.findFragmentByTag(TAG_STUDIO_DETAIL_FRAGMENT) == null){
            val studioDetailFragment = StudioDetailFragment()
            studioDetailFragment.arguments = Bundle().apply {
                putString(KEY_SELECTED_STUDIO, Gson().toJson(selectedStudio))
            }
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out)
                .setReorderingAllowed(true)
                .addToBackStack(TAG_STUDIO_DETAIL_FRAGMENT)
                .add(R.id.fragment_container, studioDetailFragment, TAG_STUDIO_DETAIL_FRAGMENT)
                .commit()
            supportFragmentManager.executePendingTransactions()
        }
    }

    private fun removeStudioDetailFragment() {
        supportFragmentManager.findFragmentByTag(TAG_STUDIO_DETAIL_FRAGMENT)?.let { studioDetailFragment ->
            supportFragmentManager.beginTransaction()
                .remove(studioDetailFragment)
                .commitNow()
            supportFragmentManager.popBackStack();
            supportFragmentManager.executePendingTransactions()
            mainViewModel.setStudioDetail(null)
        }
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

    override fun onBackPressed() {
        // If the StudioDetailFragmnet is showing, remove it.
        if(supportFragmentManager.findFragmentByTag(TAG_STUDIO_DETAIL_FRAGMENT) != null){
            removeStudioDetailFragment()
        }else {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
