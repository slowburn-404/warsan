package com.example.warsan.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.example.warsan.models.LogInRequest
import com.example.warsan.models.LogInResponse
import com.example.warsan.network.RetrofitClient
import com.example.warsan.network.WarsanAPI
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LogInFragment : Fragment() {

    private var _binding: FragmentLogInBinding? = null
    private val binding get() = _binding!!

    private lateinit var phoneNumberLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var phoneNumber: EditText
    private lateinit var password: EditText
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var btLogin: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLogInBinding.inflate(inflater, container, false)

        progressBar = binding.loginCircularIndicator
        progressBar.hide()

        phoneNumberLayout = binding.layoutPhoneNumber
        passwordLayout = binding.layoutPassword
        phoneNumber = binding.tfPhoneNumber
        password = binding.tfPassword
        btLogin = binding.btLogin



        btLogin.setOnClickListener {
            findNavController().navigate(R.id.action_logInFragment_to_retrieveGuardianFragment)
            //login()
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

            btLogin.isEnabled = false

            progressBar.show()
            makeAPICall()


        } else {
            phoneNumberLayout.error = getString(R.string.phone_number_is_required)
            passwordLayout.error = getString(R.string.password_is_required)
        }
    }

    private fun clearErrorOnTextChange() {
        phoneNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                p0: CharSequence?, p1: Int, p2: Int, p3: Int
            ) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                phoneNumberLayout.error = null
            }
        })
        password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                p0: CharSequence?, p1: Int, p2: Int, p3: Int
            ) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                passwordLayout.error = null
            }
        })
    }

    private fun makeAPICall() {
        val etPhoneNumber = phoneNumber.text?.trim().toString()
        val etPassword = password.text?.trim().toString()
        //Create api service
        val warsanAPI = RetrofitClient.instance.create(WarsanAPI::class.java)

        //create a request boody
        val requestBody = LogInRequest(etPassword, etPhoneNumber)

        //Make post request
        val call: Call<LogInResponse> = warsanAPI.login(requestBody)

        call.enqueue(object : Callback<LogInResponse> {
            override fun onResponse(
                call: Call<LogInResponse>, response: Response<LogInResponse>
            ) {
                if (response.isSuccessful) {
                    progressBar.hide()
                    val data: LogInResponse? = response.body()
                    Log.d("WARSANAPIRESPONSE", "$data")
                    Toast.makeText(
                        requireContext(), getString(R.string.login_successful), Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigate(R.id.action_logInFragment_to_retrieveGuardianFragment)
                    // Handle the response data here
                } else if (response.body() == null) {
                    // Handle the error
                    progressBar.hide()
                    Toast.makeText(
                        requireContext(), getString(R.string.incorrect_details), Toast.LENGTH_SHORT
                    ).show()
                    btLogin.isEnabled = true

                } else {
                    progressBar.hide()
                    Toast.makeText(requireContext(), R.string.login_failed, Toast.LENGTH_SHORT)
                        .show()
                    btLogin.isEnabled = true
                }
            }

            override fun onFailure(call: Call<LogInResponse>, t: Throwable) {
                // Handle network or other errors
                progressBar.hide()
                Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("WARSANAPIERROR", "Failed because of: ${t.message}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}