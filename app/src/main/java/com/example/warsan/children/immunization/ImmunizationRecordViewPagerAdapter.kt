package com.example.warsan.children.immunization

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.lifecycle.Lifecycle

class ImmunizationRecordViewPagerAdapter(
    fragments: ArrayList<Fragment>,
    fm: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {
    
    private val immunizationRecordsFragments : ArrayList<Fragment> = fragments

    override fun getItemCount(): Int {
        return  immunizationRecordsFragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return immunizationRecordsFragments[position]
    }
    
}