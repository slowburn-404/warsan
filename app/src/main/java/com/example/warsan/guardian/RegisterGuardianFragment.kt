package com.example.warsan.guardian

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.warsan.R
import com.example.warsan.databinding.FragmentRegisterGuardianBinding
import com.example.warsan.models.AddGuardianRequest
import com.example.warsan.models.ErrorResponse
import com.example.warsan.models.Location
import com.example.warsan.models.SuccessResponse
import com.example.warsan.network.RetrofitClient
import com.example.warsan.network.WarsanAPI
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterGuardianFragment : Fragment(), LocationSelectedCallback {

    private var _binding: FragmentRegisterGuardianBinding? = null
    private val binding get() = _binding!!

    //UI elements
    private lateinit var navController: NavController
    private lateinit var firstNameLayout: TextInputLayout
    private lateinit var etFirstName: EditText
    private lateinit var lastNameLayout: TextInputLayout
    private lateinit var etLastName: EditText
    private lateinit var locationLayout: TextInputLayout
    private lateinit var atLocation: AutoCompleteTextView
    private lateinit var btSaveGuardian: MaterialButton
    private lateinit var progressIndicator: CircularProgressIndicator
    private lateinit var etPhone: EditText
    private lateinit var phoneLayout: TextInputLayout


    private val locationNames = mutableListOf<String>()
    private var locationsList: MutableList<Location> = mutableListOf()
    private lateinit var locationsAdapter: ArrayAdapter<String>
    private var locationID = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterGuardianBinding.inflate(inflater, container, false)

        navController = findNavController()
        firstNameLayout = binding.layoutFirstName
        lastNameLayout = binding.layoutLastName
        etFirstName = binding.tfFirstName
        etLastName = binding.etLastName
        locationLayout = binding.layoutLocation
        atLocation = binding.atLocation
        btSaveGuardian = binding.btSaveGuardian
        progressIndicator = binding.saveGuardianCircularIndicator
        progressIndicator.hide()
        etPhone = binding.etPhone
        phoneLayout = binding.layoutPhone


        binding.tabRegisterGuardian.setNavigationOnClickListener {
            navController.popBackStack()
        }

        atLocation.setOnItemClickListener { _, _, _, _ ->
            locationLayout.error = null
        }

        binding.btSaveGuardian.setOnClickListener {
            val firstName = etFirstName.text?.trim().toString()
            val lastName = etLastName.text?.trim().toString()
            val phoneNumber = etPhone.text?.trim().toString()

            Log.d("LOCATION ID", "$locationID")

            sendGuardianDetailsToAPI(firstName, lastName, phoneNumber, locationID)
        }


        return binding.root
    }

    private fun textFieldNullCheck(): Boolean {
        val firstName = etFirstName.text?.trim().toString()
        val lastName = etLastName.text?.trim().toString()
        val phoneNumber = etPhone.text?.trim().toString()

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
        if (isPhoneNumberEmpty(phoneNumber)) {
            phoneLayout.error = "Phone number is required"
            isValid = false
        } else {
            phoneLayout.error = null
        }
        if (isLocationSelected()) {
            locationLayout.error = "Location is required"
            isValid = false
        } else {
            locationLayout.error = null
        }
        return isValid

    }

    private fun isLocationSelected(): Boolean {
        return atLocation.text.isNullOrBlank()
    }

    private fun isFirstNameEmpty(text: String?): Boolean {
        return text.isNullOrBlank()
    }

    private fun isLastNameEmpty(text: String?): Boolean {
        return text.isNullOrBlank()
    }

    private fun isPhoneNumberEmpty(text: String?): Boolean {
        return text.isNullOrBlank()
    }

    private fun setAutoCompleteTextViewAdapter() {
        locationsAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            locationsList.map { it.region })
        atLocation.setAdapter(locationsAdapter)
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

        etPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                phoneLayout.error = null // Clear the error when text changes
            }
        })

        atLocation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                locationLayout.error = null // Clear the error when text changes
            }
        })

    }

    private fun getLocations() {
        //Create api service
        val warsanAPI = RetrofitClient.instance.create(WarsanAPI::class.java)

        //Make a get request
        val call: Call<List<Location>> = warsanAPI.getLocations()

        call.enqueue(object : Callback<List<Location>> {
            override fun onResponse(
                call: Call<List<Location>>, response: Response<List<Location>>
            ) {
                if (response.isSuccessful) {
                    val data: List<Location>? = response.body()
                    Log.d("WARSANAPIRESPONSE", "$data")
                    Toast.makeText(
                        requireContext(),
                        "Locations have been added",
                        Toast.LENGTH_SHORT
                    ).show()

                    data?.let {
                        locationsList.clear()
                        locationsList.addAll(data)
                        locationsAdapter.notifyDataSetChanged()

                        setAutoCompleteTextViewAdapter()
                        getSelectedLocation(this@RegisterGuardianFragment)

                    }

                    data?.forEach {
                        locationNames.add(it.region)
                    }

                } else {
                    Toast.makeText(
                        requireContext(), R.string.location_nonexistent, Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Location>>, t: Throwable) {
                // Handle network or other errors
                //Snackbar.makeText(requireContext(), "${t.message}", Snackbar.LENGTH_SHORT).show()
                Log.e("WARSANAPIERROR", "Failed because of: ${t.message}")
            }
        })
    }

    private fun getSelectedLocation(callback: LocationSelectedCallback) {
        atLocation.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedLocationName = locationsList[position]
            locationID = selectedLocationName.id
            callback.onLocationSelected(locationID)
        }
    }

    private fun sendGuardianDetailsToAPI(
        firstName: String,
        lastName: String,
        phoneNumber: String,
        locationID: Int
    ) {
        clearErrorOnTextChange()
        if (textFieldNullCheck()) {
            progressIndicator.show()
            btSaveGuardian.isEnabled = false


            //Create api service
            val warsanAPI = RetrofitClient.instance.create(WarsanAPI::class.java)

            val guardianRequestBody =
                AddGuardianRequest(firstName, lastName, phoneNumber, locationID)

            //Make post request
            val call: Call<SuccessResponse> = warsanAPI.registerGuardian(guardianRequestBody)

            call.enqueue(object : Callback<SuccessResponse> {
                override fun onResponse(
                    call: Call<SuccessResponse>, response: Response<SuccessResponse>
                ) {
                    if (response.isSuccessful) {
                        val data: SuccessResponse? = response.body()
                        Log.d("REGISTER RESPONSE", "$data")
                        Snackbar.make(
                            binding.root,
                            "Guardian has been added",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        //Pass guardian phone number while navigating  to children list fragment
                        val guardiansPhoneNumber = data?.phoneNumber

                        if (guardiansPhoneNumber != null) {
                            Log.d("Phone Number", guardiansPhoneNumber)
                        }
                        val navigateToChildrenListFragment =
                            RegisterGuardianFragmentDirections.actionRegisterGuardianFragmentToChildrenListFragment(
                                guardiansPhoneNumber
                            )
                        navController.navigate(
                            navigateToChildrenListFragment
                        )


                    } else {

                        val errorResponseBody = response.errorBody()?.toString()

                        val errorResponse =
                            Gson().fromJson(errorResponseBody, ErrorResponse::class.java)

                        if (errorResponse.phoneNumber.isNotEmpty()) {
                            val errorMessage = errorResponse.phoneNumber[0]
                            Snackbar.make(
                                binding.root,
                                errorMessage,
                                Snackbar.LENGTH_SHORT
                            ).show()
                            progressIndicator.hide()
                            btSaveGuardian.isEnabled = true
                            Log.d("WARSAN API ERROR", response.body().toString())

                        } else {
                            // Handle the error
                            Snackbar.make(
                                binding.root,
                                "Something went wrong, please try again",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            progressIndicator.hide()
                            btSaveGuardian.isEnabled = true

                        }
                    }

                }

                override fun onFailure(call: Call<SuccessResponse>, t: Throwable) {
                    // Handle network or other errors
                    Snackbar.make(
                        binding.root, "${t.message}", Snackbar.LENGTH_SHORT
                    ).show()

                    Log.e("WARSAN API ERROR", "Failed because of: ${t.message}")
                    progressIndicator.hide()
                    btSaveGuardian.isEnabled = true

                }
            })

        }
    }

    override fun onLocationSelected(locationID: Int) {
        this.locationID = locationID
    }

    override fun onResume() {
        super.onResume()
        getLocations()
        setAutoCompleteTextViewAdapter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}