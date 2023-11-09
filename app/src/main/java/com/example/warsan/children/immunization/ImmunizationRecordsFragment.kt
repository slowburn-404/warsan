package com.example.warsan.children.immunization

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.example.warsan.R
import com.example.warsan.databinding.FragmentImmunizationRecordsBinding
import com.example.warsan.models.AddChildResponseParcelable

class ImmunizationRecordsFragment : Fragment() {

    private var _binding: FragmentImmunizationRecordsBinding? = null
    private val binding get() = _binding!!

    private val args: ImmunizationRecordsFragmentArgs by navArgs()

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

        val childObject = args.childObject
        val ageInMonths = calculateAgeInMonths(childObject.dateOfBirth)

        binding.tabImmunizationRecords.title =
            "${childObject.firstName} ${childObject.lastName}"
        if (ageInMonths == 1) {
            binding.tabImmunizationRecords.subtitle = "$ageInMonths month"
        } else {
            binding.tabImmunizationRecords.subtitle = "$ageInMonths months"
        }

          // Use a Handler to introduce a delay before setting the current item
    Handler(Looper.getMainLooper()).postDelayed({
        // Set the current ViewPager item based on the age
        immunizationRecordViewPager.currentItem = when (ageInMonths) {
            in 0..5 -> 0 // 0 to 5 months, inclusive
            in 6..11 -> 1 // 6 to 11 months, inclusive
            else -> 2 // 12 months and above
        }
    }, 100) // Delay for 100 milliseconds


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
            val childObject = AddChildResponseParcelable(
                args.childObject.id,
                args.childObject.firstName,
                args.childObject.firstName,
                args.childObject.firstName
            )
            val action =
                ImmunizationRecordsFragmentDirections.actionImmunizationRecordsFragmentToUpdateRecordsFragment(
                    childObject
                )

            findNavController().navigate(action)
        }


        return binding.root
    }

    private fun setUpImmunizationRecordViewPagerAdapter() {
        val chilObject = args.childObject
        val bundle = Bundle()
        bundle.putParcelable("childObject", chilObject)
        val adapter = ImmunizationRecordViewPagerAdapter(
            chilObject,
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

    private fun calculateAgeInMonths(dateOfBirth: String): Int {
        // Parse the date of birth
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val dob = Calendar.getInstance()
        dob.time = dateFormat.parse(dateOfBirth)!!

        // Get the current date
        val currentDate = Calendar.getInstance()

        // Calculate the age in months

        return (currentDate[Calendar.YEAR] - dob[Calendar.YEAR]) * 12 +
                currentDate[Calendar.MONTH] - dob[Calendar.MONTH]
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}