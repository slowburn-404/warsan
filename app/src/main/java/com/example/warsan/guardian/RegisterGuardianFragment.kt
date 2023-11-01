package com.example.warsan.guardian

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.warsan.R
import com.example.warsan.databinding.FragmentRegisterGuardianBinding

class RegisterGuardianFragment : Fragment() {

    private var _binding: FragmentRegisterGuardianBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterGuardianBinding.inflate(inflater, container, false)

        val navController = findNavController()

        binding.tabRegisterGuardian.setNavigationOnClickListener {
            navController.popBackStack()
        }
        binding.btSaveGuardian.setOnClickListener {
            navController.navigate(R.id.action_registerGuardianFragment_to_childrenListFragment)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    
}