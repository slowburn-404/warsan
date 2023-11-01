package com.example.warsan.children.immunization

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.warsan.R
import com.example.warsan.databinding.FragmentAddRecordsBinding
import com.google.android.material.datepicker.MaterialDatePicker

class AddRecordsFragment : Fragment() {

    private var _binding: FragmentAddRecordsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddRecordsBinding.inflate(inflater, container, false)

        binding.btSubmit.setOnClickListener {
            findNavController().navigate(R.id.action_addRecordsFragment_to_immunizationRecordsFragment3)
        }


        return binding.root
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select date").build()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}