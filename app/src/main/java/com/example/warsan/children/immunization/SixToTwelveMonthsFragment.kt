package com.example.warsan.children.immunization

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.warsan.R
import com.example.warsan.databinding.FragmentSixToTwelveMonthsBinding

class SixToTwelveMonthsFragment : Fragment() {

    private var _binding: FragmentSixToTwelveMonthsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentSixToTwelveMonthsBinding.inflate(inflater, container, false)



        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}