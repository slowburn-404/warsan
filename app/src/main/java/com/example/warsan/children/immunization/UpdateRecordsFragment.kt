package com.example.warsan.children.immunization

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.warsan.R
import com.example.warsan.databinding.FragmentUpdateRecordsBinding
import com.example.warsan.models.UpdateRecordsRequest
import com.example.warsan.models.UpdateRecordsResponse
import com.example.warsan.models.VaccineAdministrationSet
import com.example.warsan.models.Vaccines
import com.example.warsan.network.RetrofitClient
import com.example.warsan.network.WarsanAPI
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateRecordsFragment : Fragment() {

    private var _binding: FragmentUpdateRecordsBinding? = null
    private val binding get() = _binding!!

    private val args: AddRecordsFragmentArgs by navArgs()

    private lateinit var navController: NavController
    private lateinit var layoutVaccine: TextInputLayout
    private lateinit var atVaccine: AutoCompleteTextView
    private lateinit var dateAdmin: TextView
    private lateinit var nextDueDate: TextView
    private lateinit var btSubmit: MaterialButton
    private lateinit var progressIndicator: CircularProgressIndicator

    private var selectedDateAdmin: String? = null
    private var selectedNextDueDate: String? = null

    private val vaccinesList = mutableListOf<Vaccines>()
    private lateinit var vaccinesAdapter: ArrayAdapter<String>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUpdateRecordsBinding.inflate(inflater, container, false)

        navController = findNavController()
        layoutVaccine = binding.layoutVaccine
        atVaccine = binding.atVaccine
        dateAdmin = binding.tvDateAdministered
        nextDueDate = binding.tvNextDueDate
        btSubmit = binding.btSubmit
        progressIndicator = binding.vaccineProgressIndicator
        progressIndicator.hide()

        binding.tabAddRecords.title = "${args.childObject.firstName} ${args.childObject.lastName}"

        val ageInMonths = calculateAgeInMonths(args.childObject.dateOfBirth)
        binding.tabAddRecords.subtitle = "$ageInMonths months"

        binding.btSubmit.setOnClickListener {
            sendImmunizationRecordToAPI()
        }

        dateAdmin.setOnClickListener {
            dateAdmin.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            getDateAdmin(dateAdmin)
        }
        nextDueDate.setOnClickListener {
            nextDueDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            getNextDueDate(nextDueDate)
        }
        atVaccine.setOnItemClickListener { _, _, _, _ ->
            layoutVaccine.error = null
        }
        binding.tabAddRecords.setNavigationOnClickListener {
            navController.popBackStack()
        }


        return binding.root
    }

    private fun getDateAdmin(textView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                textView.text = selectedDate

                selectedDateAdmin = selectedDate


            },
            year, month, day
        )

        datePickerDialog.show()
    }

    private fun getNextDueDate(textView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                textView.text = selectedDate

                selectedNextDueDate = selectedDate


            },
            year, month, day
        )

        datePickerDialog.show()
    }

    private fun getVaccines() {
        val warsanAPI = RetrofitClient.instance.create(WarsanAPI::class.java)
        val call: Call<List<Vaccines>> = warsanAPI.getVaccines()

        call.enqueue(object : Callback<List<Vaccines>> {
            override fun onResponse(
                call: Call<List<Vaccines>>, response: Response<List<Vaccines>>
            ) {
                if (response.isSuccessful && isAdded) {
                    val data: List<Vaccines>? = response.body()
                    Log.d("WARSANAPIRESPONSE", "$data")
                    context.let {
                        Toast.makeText(
                            requireContext(),
                            "Vaccines have been added",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }


                    data?.let {
                        vaccinesList.clear()
                        vaccinesList.addAll(data)
                        vaccinesAdapter.notifyDataSetChanged()
                        setAutoCompleteTextViewAdapter()
                    }
                } else if(isAdded){
                    context?.let {
                        Toast.makeText(
                            requireContext(), "Cannot retrieve vaccines", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<List<Vaccines>>, t: Throwable) {
                Log.e("WARSANAPIERROR", "Failed because of: ${t.message}")
            }
        })
    }

    private fun setAutoCompleteTextViewAdapter() {
        vaccinesAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            vaccinesList.map { it.vaccineChoice }
        )
        atVaccine.setAdapter(vaccinesAdapter)
    }

    private fun sendImmunizationRecordToAPI() {


        if (inputValidation()) {
            val vaccineData = prepareVaccineData()

            val warsanAPI = RetrofitClient.instance.create(WarsanAPI::class.java)
            val call: Call<UpdateRecordsResponse> = warsanAPI.updateImmunizationRecord(vaccineData)

            progressIndicator.show() // Show a progress indicator while the request is being sent
            btSubmit.isEnabled = false

            call.enqueue(object : Callback<UpdateRecordsResponse> {
                override fun onResponse(
                    call: Call<UpdateRecordsResponse>,
                    response: Response<UpdateRecordsResponse>
                ) {
                    progressIndicator.hide() // Hide the progress indicator

                    if (response.isSuccessful) {
                        val responseData = response.body()
                        // Handle a successful response, if needed
                        Log.d("WARSANAPIRESPONSE", responseData.toString())
                        Toast.makeText(
                            requireContext(),
                            "Immunization Record Updated",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.navigate(
                            UpdateRecordsFragmentDirections.actionUpdateRecordsFragmentToChildrenListFragment(
                                responseData?.childPhoneNumber
                            )
                        )


                    } else {
                        // Handle errors or failed response, if needed
                        Snackbar.make(
                            binding.root,
                            "Something went wrong, check the details and try again.",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        Log.e(
                            "WARSANAPIERROR",
                            "Something went wrong, check the details and try again"
                        )
                        btSubmit.isEnabled = true
                    }
                }

                override fun onFailure(call: Call<UpdateRecordsResponse>, t: Throwable) {
                    progressIndicator.hide()
                    btSubmit.isEnabled = true

                    // Handle network or other errors
                    Log.e("WARSANAPIERROR", "Failed because of: ${t.message}")
                }
            })
        }
    }

    private fun prepareVaccineData(): UpdateRecordsRequest {
        // Create a VaccineData object with the required data
        val childId = args.childObject.id
        val status = "Taken"
        val nextDateOfAdministration = selectedNextDueDate
        val selectedVaccine = getSelectedVaccine()

        Log.d("Selected Vaccine", "${selectedVaccine?.id}")

        val administrationSetList = listOf(
            VaccineAdministrationSet(
                vaccine = selectedVaccine!!.id,
                dateOfAdministration = selectedDateAdmin!!
            )
        )

        return UpdateRecordsRequest(
            id = childId,
            vaccineAdministrationSet = administrationSetList,
            status = status,
            nextDateOfAdministration = nextDateOfAdministration!!,
            child = childId
        )
    }

    private fun getSelectedVaccine(): Vaccines? {
        val selectedVaccineName = atVaccine.text.toString()

        return vaccinesList.find { it.vaccineChoice == selectedVaccineName }
    }

    private fun inputValidation(): Boolean {
        var isValid = true

        if (isVaccineEmpty()) {
            layoutVaccine.error = "Vaccine is required"
            isValid = false
        } else {
            layoutVaccine.error = null
        }

        if (isDateAdministeredEmpty(selectedDateAdmin)) {
            dateAdmin.text = "Please select date of administration"
            dateAdmin.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.error_color_material_light
                )
            )
            isValid = false
        } else {
            dateAdmin.text = selectedDateAdmin
            dateAdmin.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }

        if (isNextDueDateEmpty(selectedNextDueDate)) {
            nextDueDate.text = "Please select next due date"
            nextDueDate.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.error_color_material_light
                )
            )
            isValid = false
        } else {
            nextDueDate.text = selectedNextDueDate
            nextDueDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }

        return isValid
    }

    private fun isVaccineEmpty(): Boolean {
        return atVaccine.text.isNullOrBlank()

    }

    private fun isDateAdministeredEmpty(text: String?): Boolean {
        return text.isNullOrEmpty()
    }

    private fun isNextDueDateEmpty(text: String?): Boolean {
        return text.isNullOrEmpty()

    }
    private fun calculateAgeInMonths(dateOfBirth: String): Int {
        // Parse the date of birth
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val dob = Calendar.getInstance()
        dob.time = dateFormat.parse(dateOfBirth)!!

        // Get the current date
        val currentDate = Calendar.getInstance()

        // Calculate the age in months

        return (currentDate[Calendar.YEAR] - dob[Calendar.YEAR]) * 12 +
                currentDate[Calendar.MONTH] - dob[Calendar.MONTH]
    }


    override fun onResume() {
        super.onResume()
        getVaccines()
        setAutoCompleteTextViewAdapter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}