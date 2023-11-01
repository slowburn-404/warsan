package com.example.warsan.children

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.warsan.R
import com.example.warsan.databinding.FragmentRegisterChildBinding


class RegisterChildFragment : Fragment() {

    private var _binding: FragmentRegisterChildBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterChildBinding.inflate(inflater, container, false)

        binding.btSaveChild.setOnClickListener {
            findNavController().navigate(R.id.action_registerChildFragment_to_addRecordsFragment)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding =  null
    }


}