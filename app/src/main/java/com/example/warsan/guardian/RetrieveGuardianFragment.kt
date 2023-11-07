package com.example.warsan.guardian

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
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.warsan.R
import com.example.warsan.databinding.FragmentRetrieveGuardianBinding
import com.example.warsan.models.GuardianResponse
import com.example.warsan.network.RetrofitClient
import com.example.warsan.network.WarsanAPI
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RetrieveGuardianFragment : Fragment() {
    private var _binding: FragmentRetrieveGuardianBinding? = null
    private val binding get() = _binding!!

    //UI elements
    private lateinit var guardianPhoneLayout: TextInputLayout
    private lateinit var etGuardianPhone: EditText
    private lateinit var btRetrieveGuardian: MaterialButton
    private lateinit var progressIndicator: CircularProgressIndicator
    private lateinit var navController: NavController


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentRetrieveGuardianBinding.inflate(inflater, container, false)

        guardianPhoneLayout = binding.layoutGuardianPhoneNumber
        etGuardianPhone = binding.tfGuardianPhoneNumber
        btRetrieveGuardian = binding.btRetrieveGuardian
        progressIndicator = binding.retrieveGuardianCircularIndicator
        progressIndicator.hide()
        navController = findNavController()


        binding.txtRegisterNewGuardian.setOnClickListener {
            binding.txtRegisterNewGuardian.setTextColor(ContextCompat.getColor(requireContext(),R.color.main_color))
            navController.navigate(R.id.action_retrieveGuardianFragment_to_registerGuardianFragment)
        }
        binding.btRetrieveGuardian.setOnClickListener {
            retrieveGuardian()
        }
        clearErrorOnTextChange()


        return binding.root
    }

    private fun retrieveGuardian() {
        if (textFieldNullCheck()) {
            guardianPhoneLayout.error = null

            btRetrieveGuardian.isEnabled = false
            progressIndicator.show()
            retrieveGuardianFromAPI()


        } else {
            guardianPhoneLayout.error = getString(R.string.phone_number_is_required)
        }
    }

    private fun textFieldNullCheck(): Boolean {
        val guardianPhoneNumber = etGuardianPhone.text?.trim().toString()

        return guardianPhoneNumber.isNotEmpty()

    }

    private fun clearErrorOnTextChange() {
        etGuardianPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                p0: CharSequence?, p1: Int, p2: Int, p3: Int
            ) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                guardianPhoneLayout.error = null
            }
        })
    }

    private fun retrieveGuardianFromAPI() {
        val guardianPhoneNumber = etGuardianPhone.text?.trim().toString()
        //Create api service
        val warsanAPI = RetrofitClient.instance.create(WarsanAPI::class.java)

        //Make a get request
        val call: Call<GuardianResponse> = warsanAPI.retrieveGuardian(guardianPhoneNumber)

        call.enqueue(object : Callback<GuardianResponse> {
            override fun onResponse(
                call: Call<GuardianResponse>, response: Response<GuardianResponse>
            ) {
                if (response.isSuccessful) {
                    progressIndicator.hide()
                    val data: GuardianResponse? = response.body()
                    Log.d("WARSANAPIRESPONSE", "$data")

                    // Pass guardian phone number while navigating  to children list fragment
                    val guardianPhoneNumber = data?.guardian?.phoneNumber.toString()
                    val navigateToChildrenListFragment =
                        RetrieveGuardianFragmentDirections.actionRetrieveGuardianFragmentToChildrenListFragment(
                            guardianPhoneNumber
                        )
                    navController.navigate(
                        navigateToChildrenListFragment
                    )
                } else if (response.body() == null) {
                    progressIndicator.hide()
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.guardian_non_existent),
                        Toast.LENGTH_SHORT
                    ).show()
                    btRetrieveGuardian.isEnabled = true

                } else {
                    progressIndicator.hide()
                    Toast.makeText(requireContext(), R.string.login_failed, Toast.LENGTH_SHORT)
                        .show()
                    btRetrieveGuardian.isEnabled = true
                }
            }

            override fun onFailure(call: Call<GuardianResponse>, t: Throwable) {
                // Handle network or other errors
                progressIndicator.hide()
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