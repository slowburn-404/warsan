package com.example.warsan.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.example.warsan.R
import com.example.warsan.databinding.FragmentLogInBinding
import com.google.android.material.textfield.TextInputLayout

class LogInFragment : Fragment() {

    private var _binding: FragmentLogInBinding? = null
    private val binding get() = _binding!!

    private lateinit var phoneNumberLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var phoneNumber: EditText
    private lateinit var password: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLogInBinding.inflate(inflater, container, false)

        phoneNumberLayout = binding.layoutPhoneNumber
        passwordLayout = binding.layoutPassword
        phoneNumber = binding.tfPhoneNumber
        password = binding.tfPassword


        binding.btLogin.setOnClickListener {
            login()
        }
        clearErrorOnTextChange()


        return binding.root
    }

    private fun textFieldNullCheck(): Boolean {
        val etPhoneNumber = phoneNumber.text?.trim().toString()
        val etPassword = password.text?.trim().toString()

        return etPhoneNumber.isNotEmpty() && etPassword.isNotEmpty()

    }

    private fun login() {

        if (textFieldNullCheck()) {
            phoneNumberLayout.error = null
            passwordLayout.error = null

            Toast.makeText(
                requireContext(), "Success", Toast.LENGTH_SHORT
            ).show()
            findNavController().navigate(R.id.action_logInFragment_to_retrieveGuardianFragment)
        } else {
            phoneNumberLayout.error = "Phone number cannot be empty"
            passwordLayout.error = "Password cannot be empty"
        }

    }

    private fun clearErrorOnTextChange() {
        phoneNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                phoneNumberLayout.error = null
            }
        })
        password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                passwordLayout.error = null
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}