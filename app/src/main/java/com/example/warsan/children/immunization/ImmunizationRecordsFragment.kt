package com.example.warsan.children.immunization

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.warsan.R
import com.example.warsan.databinding.FragmentImmunizationRecordsBinding

class ImmunizationRecordsFragment : Fragment() {

    private var _binding: FragmentImmunizationRecordsBinding? = null
    private val binding get() = _binding!!

    private lateinit var immunizationRecordsFragments: ArrayList<Fragment>
    private lateinit var immunizationRecordViewPager: ViewPager2

    private lateinit var firstDot: View
    private lateinit var secondDot: View
    private lateinit var thirdDot: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentImmunizationRecordsBinding.inflate(inflater, container, false)

        firstDot = binding.activeDot
        secondDot = binding.inactiveDot
        thirdDot = binding.inactiveDot2
        immunizationRecordViewPager = binding.vpImmunizationRecords


        immunizationRecordsFragments = arrayListOf(
            ZeroToSixMonthsFragment(), SixToTwelveMonthsFragment(), TwelveToEighteenMonthsFragment()
        )


        immunizationRecordViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setActiveDot()
            }
        })

        setUpImmunizationRecordViewPagerAdapter()


        firstDot.setOnClickListener {
            immunizationRecordViewPager.currentItem = 0
        }
        secondDot.setOnClickListener {
            immunizationRecordViewPager.currentItem = 1
        }
        thirdDot.setOnClickListener {
            immunizationRecordViewPager.currentItem = 2
        }
        binding.btLogin.setOnClickListener {
            findNavController().navigate(R.id.action_immunizationRecordsFragment_to_addRecordsFragment2)
        }


        return binding.root
    }

    private fun setUpImmunizationRecordViewPagerAdapter() {
        val adapter = ImmunizationRecordViewPagerAdapter(
            immunizationRecordsFragments, requireActivity().supportFragmentManager, lifecycle
        )
        immunizationRecordViewPager.adapter = adapter
    }

    private fun setActiveDot() {

        when (immunizationRecordViewPager.currentItem) {
            0 -> {
                firstDot.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.active_dot_bg)
                secondDot.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.inactive_dot_bg)
                thirdDot.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.inactive_dot_bg)

            }

            1 -> {
                firstDot.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.inactive_dot_bg)
                secondDot.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.active_dot_bg)
                thirdDot.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.inactive_dot_bg)

            }

            2 -> {
                firstDot.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.inactive_dot_bg)
                secondDot.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.inactive_dot_bg)
                thirdDot.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.active_dot_bg)

            }

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}