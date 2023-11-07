package com.example.warsan.children

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.warsan.R
import com.example.warsan.databinding.FragmentRegisterChildBinding
import com.example.warsan.models.AddChildRequest
import com.example.warsan.models.AddChildResponse
import com.example.warsan.models.AddChildResponseParcelable
import com.example.warsan.models.SuccessResponse
import com.example.warsan.network.RetrofitClient
import com.example.warsan.network.WarsanAPI
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterChildFragment : Fragment() {

    private var _binding: FragmentRegisterChildBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private lateinit var firstNameLayout: TextInputLayout
    private lateinit var etFirstName: EditText
    private lateinit var lastNameLayout: TextInputLayout
    private lateinit var etLastName: EditText
    private lateinit var layoutGender: TextInputLayout
    private lateinit var atGender: AutoCompleteTextView
    private lateinit var btSaveChild: MaterialButton
    private lateinit var progressIndicator: CircularProgressIndicator
    private lateinit var layoutDOB: MaterialCardView
    private lateinit var tVDOB: TextView
    private lateinit var selectedDOB: String

    private lateinit var guardianID: String

    private val args: RegisterChildFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterChildBinding.inflate(inflater, container, false)

        navController = findNavController()
        firstNameLayout = binding.layoutFirstName
        etFirstName = binding.tfFirstName
        lastNameLayout = binding.layoutLastName
        etLastName = binding.etLastName
        layoutGender = binding.layoutGender
        atGender = binding.atGender
        progressIndicator = binding.genderProgressIndicator
        progressIndicator.hide()
        btSaveChild = binding.btSaveChild
        layoutDOB = binding.layoutDOB
        tVDOB = binding.tVDOB
        guardianID = args.guardianID.toString()


        layoutDOB.setOnClickListener {
            getDateOfBirth()
        }

        btSaveChild.setOnClickListener {
            var dOBPlaceHolder = ContextCompat.getString(requireContext(), R.string.date_of_birth)
            val doBError = ContextCompat.getString(requireContext(), R.string.date_of_birth_error)
            val firstName = etFirstName.text?.trim().toString()
            val lastName = etLastName.text?.trim().toString()
            var dOB: String? = null

            if(tVDOB.text != doBError || tVDOB.text != dOBPlaceHolder) {
                dOB = tVDOB.text.toString()
            }
            val gender  = when (atGender.text.toString()) {
                "Male" -> "M"

                else -> {
                    "F"
                }
            }
            if (dOB != null) {
                sendChildDetailsToAPI(firstName, lastName, gender, dOB, guardianID.toInt())
            }


        }



        return binding.root
    }

    private fun isGenderSelected(): Boolean {
        return atGender.text.isNullOrBlank()
    }

    private fun isFirstNameEmpty(text: String?): Boolean {
        return text.isNullOrBlank()
    }

    private fun isLastNameEmpty(text: String?): Boolean {
        return text.isNullOrBlank()
    }

    private fun isDOBEmpty(text: TextView): String {
        return text.text.trim().toString()
    }

    private fun textFieldNullCheck(): Boolean {
        val firstName = etFirstName.text?.trim().toString()
        val lastName = etLastName.text?.trim().toString()
        var dOBPlaceHolder = ContextCompat.getString(requireContext(), R.string.date_of_birth)

        var isValid = true


        if (isFirstNameEmpty(firstName)) {
            firstNameLayout.error = "First name is required"
            isValid = false

        } else {
            firstNameLayout.error = null
        }
        if (isLastNameEmpty(lastName)) {
            lastNameLayout.error = "Last name is required"
            isValid = false

        } else {
            lastNameLayout.error = null
        }
        if (isDOBEmpty(tVDOB) == dOBPlaceHolder) {
            tVDOB.text = ContextCompat.getString(requireContext(), R.string.date_of_birth_error)
            tVDOB.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.error_color_material_light
                )
            )
            isValid = false
        } else {
            tVDOB.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
        if (isGenderSelected()) {
            layoutGender.error = "Gender is required"
            isValid = false
        } else {
            layoutGender.error = null
        }
        return isValid

    }

    private fun clearErrorOnTextChange() {
        etFirstName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                firstNameLayout.error = null // Clear the error when text changes
            }
        })

        etLastName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                lastNameLayout.error = null // Clear the error when text changes
            }
        })

        atGender.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                layoutGender.error = null // Clear the error when text changes
            }
        })

    }

    private fun sendChildDetailsToAPI(
        firstName: String,
        lastName: String,
        gender: String,
        dOB: String,
        guardianID: Int
    ) {
        clearErrorOnTextChange()
        if (textFieldNullCheck()) {
            progressIndicator.show()
            btSaveChild.isEnabled = false

            //Create api service
            val warsanAPI = RetrofitClient.instance.create(WarsanAPI::class.java)

            val childRequestBody = AddChildRequest(firstName, lastName, dOB, gender, guardianID)

            //Make post request
            val call: Call<AddChildResponse> = warsanAPI.addChild(childRequestBody)

            call.enqueue(object : Callback<AddChildResponse> {
                override fun onResponse(
                    call: Call<AddChildResponse>, response: Response<AddChildResponse>
                ) {
                    if (response.isSuccessful) {
                        val data: AddChildResponse? = response.body()
                        Log.d("ADDCHILD RESPONSE", "$data")
                        Toast.makeText(requireContext(), "Child has been added", Toast.LENGTH_SHORT)
                            .show()
                        //pass child ID to add records fragment

                        val navigateTAddRecordsFragment = data?.let {
                            val childObject = AddChildResponseParcelable(data.id, data.firstName, data.lastName, data.dateOfBirth)
                            RegisterChildFragmentDirections.actionRegisterChildFragmentToAddRecordsFragment(
                                childObject = childObject
                            )
                        }
                        navigateTAddRecordsFragment?.let {
                            navController.navigate(navigateTAddRecordsFragment)
                        }
                    } else {

                        Toast.makeText(
                            requireContext(),
                            "Something went wrong, please try again",
                            Toast.LENGTH_SHORT
                        ).show()
                        progressIndicator.hide()
                        btSaveChild.isEnabled = true

                    }


                }

                override fun onFailure(call: Call<AddChildResponse>, t: Throwable) {
                    val errorMessage: String? = t.message
                    if (errorMessage != null) {
                        Log.d("ADD CHILD ERROR", errorMessage)
                        Toast.makeText(
                            requireContext(), errorMessage, Toast.LENGTH_SHORT
                        ).show()
                        progressIndicator.hide()
                        btSaveChild.isEnabled = true
                    }


                }
            })
        }


    }
    private fun getDateOfBirth() {

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
        requireContext(),
        { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
            tVDOB.text = selectedDate

            selectedDOB = selectedDate

        },
        year, month, day
    )

    datePickerDialog.show()

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}