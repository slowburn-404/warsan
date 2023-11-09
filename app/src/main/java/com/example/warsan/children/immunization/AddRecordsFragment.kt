package com.example.warsan.children.immunization

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.warsan.children.ChildrenListFragmentDirections
import com.example.warsan.databinding.FragmentAddRecordsBinding
import com.example.warsan.models.AddChildResponseParcelable
import com.example.warsan.models.AdministrationSet
import com.example.warsan.models.VaccineData
import com.example.warsan.models.Vaccines
import com.example.warsan.network.RetrofitClient
import com.example.warsan.network.WarsanAPI
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddRecordsFragment : Fragment() {

    private var _binding: FragmentAddRecordsBinding? = null
    private val binding get() = _binding!!

    private val args: AddRecordsFragmentArgs by navArgs()

    private lateinit var navController: NavController
    private lateinit var layoutVaccine: TextInputLayout
    private lateinit var atVaccine: AutoCompleteTextView
    private lateinit var dateAdmin: TextView
    private lateinit var nextDueDate: TextView
    private lateinit var btSubmit: MaterialButton
    private lateinit var progressIndicator: CircularProgressIndicator

    private lateinit var selectedDateAdmin: String
    private lateinit var selectedNextDueDate: String

    private val vaccinesList = mutableListOf<Vaccines>()
    private lateinit var vaccinesAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddRecordsBinding.inflate(inflater, container, false)

        navController = findNavController()
        layoutVaccine = binding.layoutVaccine
        atVaccine = binding.atVaccine
        dateAdmin = binding.tvDateAdministered
        nextDueDate = binding.tvNextDueDate
        btSubmit = binding.btSubmit
        progressIndicator = binding.vaccineProgressIndicator
        progressIndicator.hide()

        binding.tabAddRecords.title = "${args.childObject.firstName} ${args.childObject.lastName}"
        binding.tabAddRecords.subtitle = args.childObject.dateOfBirth

        binding.btSubmit.setOnClickListener {
            sendImmunizationRecordToAPI()
        }

        dateAdmin.setOnClickListener {
            getDateOfBirth(dateAdmin)
        }
        nextDueDate.setOnClickListener {
            getDateOfBirth(nextDueDate)
        }

        return binding.root
    }


    private fun getDateOfBirth(textView: TextView) {
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
                if (response.isSuccessful) {
                    val data: List<Vaccines>? = response.body()
                    Log.d("WARSANAPIRESPONSE", "$data")
                    Toast.makeText(requireContext(), "Vaccines have been added", Toast.LENGTH_SHORT)
                        .show()

                    data?.let {
                        vaccinesList.clear()
                        vaccinesList.addAll(data)
                        vaccinesAdapter.notifyDataSetChanged()
                        setAutoCompleteTextViewAdapter()
                    }
                } else if (response.body() == null) {
                    Toast.makeText(
                        requireContext(),
                        "Cannot retrieve vaccines",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(), "Cannot retrieve vaccines", Toast.LENGTH_SHORT
                    ).show()
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
        val selectedVaccine = getSelectedVaccine()


        if (selectedVaccine != null) {
            val vaccineData = prepareVaccineData()

            val warsanAPI = RetrofitClient.instance.create(WarsanAPI::class.java)
            val call: Call<VaccineData> = warsanAPI.addRecords(vaccineData)

            progressIndicator.show() // Show a progress indicator while the request is being sent
            btSubmit.isEnabled = false

            call.enqueue(object : Callback<VaccineData> {
                override fun onResponse(call: Call<VaccineData>, response: Response<VaccineData>) {
                    progressIndicator.hide() // Hide the progress indicator

                    if (response.isSuccessful) {
                        val responseData = response.body()
                        // Handle a successful response, if needed
                        Log.d("WARSANAPIRESPONSE", responseData.toString())
                        Toast.makeText(
                            requireContext(),
                            "Immunization Record Added",
                            Toast.LENGTH_SHORT
                        ).show()

                        val childObject = AddChildResponseParcelable(args.childObject.id, args.childObject.firstName, args.childObject.lastName, args.childObject.dateOfBirth)

                        val action =
                            AddRecordsFragmentDirections.actionAddRecordsFragmentToImmunizationRecordsFragment3(
                                childObject = childObject
                            )
                        navController.navigate(action)
                    } else {
                        // Handle errors or failed response, if needed
                        Toast.makeText(
                            requireContext(),
                            "Immunization record with this child already exists.",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("WARSANAPIERROR", "Failed to send immunization record")
                        btSubmit.isEnabled = true
                    }
                }

                override fun onFailure(call: Call<VaccineData>, t: Throwable) {
                    progressIndicator.hide()
                    btSubmit.isEnabled = true

                    // Handle network or other errors
                    Log.e("WARSANAPIERROR", "Failed because of: ${t.message}")
                }
            })
        } else {
            Toast.makeText(requireContext(), "Please select a valid vaccine", Toast.LENGTH_SHORT)
                .show()

        }
    }

    private fun prepareVaccineData(): VaccineData {
        // Create a VaccineData object with the required data
        val childId = args.childObject.id
        val status = "Taken"
        val nextDateOfAdministration = selectedNextDueDate
        val selectedVaccine = getSelectedVaccine()

        // Create a list of AdministrationSet objects with vaccine data
        val administrationSetList = listOf(
            AdministrationSet(
                vaccine = selectedVaccine!!.id,
                dateOfAdministration = selectedDateAdmin
            )
        )

        return VaccineData(
            id = childId,
            administrationSet = administrationSetList,
            status = status,
            nextDateOfAdministration = nextDateOfAdministration,
            child = childId
        )
    }

    private fun getSelectedVaccine(): Vaccines? {
        val selectedVaccineName = atVaccine.text.toString()

        return vaccinesList.find { it.vaccineChoice == selectedVaccineName }
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
