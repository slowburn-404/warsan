package com.example.warsan.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.warsan.R
import com.example.warsan.databinding.FragmentLogInBinding

class LogInFragment : Fragment() {

    private var _binding: FragmentLogInBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLogInBinding.inflate(inflater, container, false)

        binding.btLogin.setOnClickListener {
            login()
        }


        return binding.root
    }

    private fun textFieldNullCheck(): Boolean {
        val phoneNumber = binding.tfPhoneNumber.text?.trim().toString()
        val password = binding.tfPassword.text?.trim().toString()

        return phoneNumber.isNotEmpty() || password.isNotEmpty()

    }

    private fun login() {
        val phoneNumberLayout = binding.layoutPhoneNumber
        val passwordLayout = binding.layoutPassword
        val phoneNumber = binding.tfPhoneNumber.text?.trim().toString()
        val password = binding.tfPassword.text?.trim().toString()
        if (textFieldNullCheck()) {
            phoneNumberLayout.error = null
            passwordLayout.error = null
            Toast.makeText(
                requireContext(), "Success", Toast.LENGTH_SHORT
            ).show()
        } else {
            phoneNumberLayout.error = "Phone number cannot be empty"
            passwordLayout.error = "Password cannot be empty"
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}