package com.example.warsan.children.immunization

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.warsan.R
import com.example.warsan.databinding.FragmentTwelveToEighteenMonthsBinding
import com.example.warsan.models.AddChildResponseParcelable
import com.example.warsan.models.ImmunizationDetails
import com.example.warsan.models.VaccinatedChild
import com.example.warsan.models.Vaccines
import com.example.warsan.network.RetrofitClient
import com.example.warsan.network.WarsanAPI
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date
import java.util.Locale


class TwelveToEighteenMonthsFragment : Fragment() {
    private var _binding: FragmentTwelveToEighteenMonthsBinding? = null
    private val binding get() = _binding!!

    private lateinit var immunizationDetailsAdapter: ImmunizationDetailsAdapter
    private var immunizationDetailsListForRecyclerView = mutableListOf<ImmunizationDetails>()
    private val immunizationDetailsListFromAPI: ArrayList<VaccinatedChild> = arrayListOf()
    private val vaccinesList = mutableListOf<Vaccines>()

    private lateinit var progressIndicator: CircularProgressIndicator


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTwelveToEighteenMonthsBinding.inflate(inflater, container, false)

        setUpRecyclerViewAdapter()

        val args: ImmunizationRecordsFragmentArgs by navArgs()
        val childObject = args.childObject
        progressIndicator = binding.twelveToEighteenCircularProgressIndicator
        progressIndicator.hide()

        binding.btLogin.isEnabled = false

        immunizationDetailsListForRecyclerView.clear()
        getVaccines()
        fetchImmunizationRecordsForTwelveToEighteenMonths(childObject.id)


        binding.btLogin.setOnClickListener {
            val childObject = AddChildResponseParcelable(
                args.childObject.id,
                args.childObject.firstName,
                args.childObject.lastName,
                args.childObject.dateOfBirth
            )
            val action =
                ImmunizationRecordsFragmentDirections.actionImmunizationRecordsFragmentToUpdateRecordsFragment(
                    childObject
                )

            findNavController().navigate(action)
        }


        return binding.root
    }

    private fun setUpRecyclerViewAdapter() {
        immunizationDetailsAdapter =
            ImmunizationDetailsAdapter(immunizationDetailsListForRecyclerView)
        binding.rvTwelveToEighteenMonths.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTwelveToEighteenMonths.setHasFixedSize(true)
        binding.rvTwelveToEighteenMonths.adapter = immunizationDetailsAdapter
    }

    private fun fetchImmunizationRecordsForTwelveToEighteenMonths(childID: Int) {
        progressIndicator.show()
        val warsanAPI = RetrofitClient.instance.create(WarsanAPI::class.java)
        val call = warsanAPI.getImmunizationRecords(childID)

        call.enqueue(object : Callback<List<VaccinatedChild>> {
            override fun onResponse(
                call: Call<List<VaccinatedChild>>,
                response: Response<List<VaccinatedChild>>
            ) {
                if (response.isSuccessful) {
                    val vaccinatedChild = response.body()
                    progressIndicator.hide()

                    if (vaccinatedChild != null) {
                        immunizationDetailsListFromAPI.addAll(vaccinatedChild)
                        filterVaccineData(immunizationDetailsListFromAPI)
                    }

                    vaccinatedChild?.forEach { childVaccine ->
                        binding.txtVNextDueDate.text = childVaccine.nextDateOfAdministration
                        val nextDateOfAdministration = childVaccine.nextDateOfAdministration
                        val isUpdateButtonEnabled = isUpdateButtonEnabled(nextDateOfAdministration)
                        binding.btLogin.isEnabled = isUpdateButtonEnabled
                    }
                } else {
                    Snackbar.make(
                        binding.root,
                        "Failed to fetch immunization record, please try again.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    Log.e("WARSANAPIERROR", "Failed to fetch immunization record")
                    progressIndicator.hide()
                }
            }

            override fun onFailure(call: Call<List<VaccinatedChild>>, t: Throwable) {
                Log.e("WARSANAPIERROR", "Failed because of: ${t.message}")
                progressIndicator.hide()
            }
        })
    }

    private fun filterVaccineData(immunizationDetailsListFromAPI: ArrayList<VaccinatedChild>?) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Assuming each child has only one date of birth
        val childDob = immunizationDetailsListFromAPI?.firstOrNull()?.let {
            Calendar.getInstance().apply {
                time = dateFormat.parse(it.childDateOfBirth)!!
            }
        } ?: return

        Log.d("dob", childDob.time.toString())

        // Calculate the start date based on the child's date of birth
        val startDate = Calendar.getInstance().apply {
            time = childDob.time
        }
        Log.d("Start date", startDate.time.toString())

        //Calculate the end date between 12 and 18 months
        val endMonthRange = (12 until 18).random()
        val endDate = Calendar.getInstance().apply {
            time = startDate.time
            add(Calendar.MONTH, endMonthRange)
        }
        Log.d("End date", endDate.time.toString())

        // Filter the data based on the date range
        val filteredData = immunizationDetailsListFromAPI?.filter { vaccinatedChild ->
            vaccinatedChild.vaccineAdministrationSet.any { administration ->
                val vaccineDate = dateFormat.parse(administration.dateOfAdministration)
                val twelveToSixMonthsDifference = calculateMonthsDifference(startDate, vaccineDate)

                twelveToSixMonthsDifference in 12..17
            }
        }

        // Convert filtered data to ImmunizationDetails and display in the UI
        immunizationDetailsListForRecyclerView.clear()
        filteredData?.forEach { vaccinatedChild ->
            convertVaccinatedChildToImmunizationDetails(vaccinatedChild).let {
                immunizationDetailsListForRecyclerView.addAll(it)

                Log.d("Filtered data", it.toString())
                Log.d("rvImmunization", immunizationDetailsListForRecyclerView.toString())
            }
        }
        immunizationDetailsAdapter.updateData(immunizationDetailsListForRecyclerView)
    }

    // Function to calculate the difference in months between two dates
    private fun calculateMonthsDifference(startDate: Calendar, endDate: Date?): Int {
        val endCalendar = Calendar.getInstance().apply {
            time = endDate ?: return 0
        }

        val yearDiff = endCalendar.get(Calendar.YEAR) - startDate.get(Calendar.YEAR)
        val monthDiff = endCalendar.get(Calendar.MONTH) - startDate.get(Calendar.MONTH)

        return yearDiff * 12 + monthDiff
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

                    data?.let {
                        vaccinesList.clear()
                        vaccinesList.addAll(data)
                    }
                } else if (response.body() == null) {
                    Snackbar.make(
                        binding.root,
                        "Cannot retrieve vaccines",
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    Snackbar.make(
                        binding.root, "Cannot retrieve vaccines", Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Vaccines>>, t: Throwable) {
                Log.e("WARSANAPIERROR", "Failed because of: ${t.message}")
            }
        })
    }

    private fun convertVaccinatedChildToImmunizationDetails(
        vaccinatedChild: VaccinatedChild,
    ): List<ImmunizationDetails> {
        val immunizationDetailsList = mutableListOf<ImmunizationDetails>()
        val status = getDrawableForStatus(vaccinatedChild.status)

        vaccinatedChild.vaccineAdministrationSet.forEach {
            val vaccine = it.vaccine
            val dateOfAdministration = it.dateOfAdministration
            val vaccineChoice =
                vaccinesList.find { child ->
                    child.id.toString() == vaccine.toString()
                }?.vaccineChoice ?: "Unknown Vaccine"
            val immunizationDetails =
                ImmunizationDetails(vaccineChoice, status, dateOfAdministration)
            immunizationDetailsList.add(immunizationDetails)
        }

        Log.d("Immunization details list", immunizationDetailsList.toString())

        return immunizationDetailsList
    }

    private fun getDrawableForStatus(vaccinatedChildStatus: String): Int {
        return when (vaccinatedChildStatus) {
            "Taken" -> R.drawable.tick
            else -> R.drawable.cross
        }
    }

    private fun isUpdateButtonEnabled(nextDueDate: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val nextDueDateParsed = dateFormat.parse(nextDueDate) ?: return false

        val currentDate = Date()
        return currentDate.after(nextDueDateParsed)
    }


    companion object {
        // Create a companion object with a newInstance method that accepts arguments
        fun newInstance(childObject: AddChildResponseParcelable): TwelveToEighteenMonthsFragment {
            val fragment = TwelveToEighteenMonthsFragment()
            val args = Bundle()
            args.putParcelable("childObject", childObject)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}