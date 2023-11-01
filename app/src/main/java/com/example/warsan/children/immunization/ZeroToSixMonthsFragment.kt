package com.example.warsan.children.immunization

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.warsan.R
import com.example.warsan.children.ChildrenListAdapter
import com.example.warsan.databinding.FragmentSixToTwelveMonthsBinding
import com.example.warsan.databinding.FragmentZeroToSixMonthsBinding
import com.example.warsan.models.Child
import com.example.warsan.models.ImmunizationDetails

class ZeroToSixMonthsFragment : Fragment() {
    private var _binding: FragmentZeroToSixMonthsBinding? = null
    private val binding get() = _binding!!

    private lateinit var immunizationDetailsAdapter: ImmunizationDetailsAdapter
    private var immunizationDetailsList = ArrayList<ImmunizationDetails>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentZeroToSixMonthsBinding.inflate(inflater, container, false)

        immunizationDetailsList.clear()
        addImmunizationDetails()




        return binding.root
    }

    private fun addImmunizationDetails() {
        for (i in 1..1){
            immunizationDetailsList.add(ImmunizationDetails("Polio3", R.drawable.tick, "01/01/23"))
            immunizationDetailsList.add(ImmunizationDetails("BCG", R.drawable.tick, "20/02/23"))
        }


        immunizationDetailsAdapter = ImmunizationDetailsAdapter(immunizationDetailsList)
        binding.rvZeroToSixMonths.layoutManager = LinearLayoutManager(requireContext())
        binding.rvZeroToSixMonths.setHasFixedSize(true)
        binding.rvZeroToSixMonths.adapter = immunizationDetailsAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}