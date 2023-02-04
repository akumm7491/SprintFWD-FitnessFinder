package com.test.fitnessstudios.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.test.fitnessstudios.ui.fragments.MapFragment
import com.test.fitnessstudios.ui.fragments.StudioListFragment

/**
 * A simple pager adapter that represents our list and map fragments
 */
class PagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        lateinit var fragment: Fragment
        when(position){
            0 -> { fragment =  MapFragment() }
            1 -> { fragment = StudioListFragment() }
        }
        return fragment
    }
}