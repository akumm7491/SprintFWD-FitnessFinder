package com.test.fitnessstudios.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.test.fitnessstudios.R
import com.test.fitnessstudios.databinding.FragmentStudioListBinding
import com.test.fitnessstudios.ui.adapters.StudioAdapter
import com.test.fitnessstudios.ui.viewmodels.MainViewModel
import com.test.fitnessstudios.ui.viewmodels.StudioViewModel
import kotlinx.coroutines.launch

class StudioListFragment : Fragment(R.layout.fragment_studio_list) {

    private val TAG = "StudioListFragment"

    private var _binding: FragmentStudioListBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private lateinit var mainViewModel: MainViewModel
    private lateinit var studioViewModel: StudioViewModel
    lateinit var studioAdapter: StudioAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudioListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        studioViewModel = ViewModelProvider(requireActivity())[StudioViewModel::class.java]
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        studioAdapter = StudioAdapter()
        studioAdapter.setOnItemClickListener { studio ->
            Log.d(TAG, "Clicked on studio: $studio")
            // Update the selected studio which will trigger the studio detail fragment to show
            mainViewModel.setStudioDetail(studio)
        }

        setupRecyclerView()
        listenForStudioUpdates()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() = binding.rvAllStudios.apply {
        adapter = studioAdapter
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL).apply {
            setPadding(
                resources.getDimensionPixelSize(R.dimen.rv_divider_start_and_end_padding),
                0,
                resources.getDimensionPixelSize(R.dimen.rv_divider_start_and_end_padding),
                0)
        })
    }

    private fun listenForStudioUpdates() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                studioViewModel.studiosUiState.collect { studioUiState ->
                    Log.v(TAG, "New StudioUiState in StudioListFragment: $studioUiState")
                    studioAdapter.studios = studioUiState.studios
                }
            }
        }
    }

}