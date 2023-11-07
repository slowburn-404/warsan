package com.example.warsan.children.immunization

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.lifecycle.Lifecycle
import com.example.warsan.models.AddChildResponseParcelable

class ImmunizationRecordViewPagerAdapter(
    private val childObject: AddChildResponseParcelable,
    fragments: ArrayList<Fragment>,
    fm: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {

    private val immunizationRecordsFragments: ArrayList<Fragment> = fragments

    override fun getItemCount(): Int {
        return immunizationRecordsFragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ZeroToSixMonthsFragment.newInstance(childObject)
            1 -> SixToTwelveMonthsFragment.newInstance(childObject)
            2 -> TwelveToEighteenMonthsFragment.newInstance(childObject)
            else -> throw IllegalArgumentException("Invalid position")
        }
    }

}