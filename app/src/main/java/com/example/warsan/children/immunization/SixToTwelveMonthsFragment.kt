package com.example.warsan.children.immunization

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.warsan.R
import com.example.warsan.databinding.FragmentSixToTwelveMonthsBinding
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

class SixToTwelveMonthsFragment : Fragment() {

    private var _binding: FragmentSixToTwelveMonthsBinding? = null
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
        _binding = FragmentSixToTwelveMonthsBinding.inflate(inflater, container, false)

        val args: ImmunizationRecordsFragmentArgs by navArgs()
        val childObject = args.childObject

        binding.btLogin.isEnabled = false
        progressIndicator = binding.sixToTwelveCircularProgressIndicator
        progressIndicator.hide()


        binding.btLogin.setOnClickListener {
            val childObjectFromSafeArgs = AddChildResponseParcelable(
                childObject.id,
                childObject.firstName,
                childObject.lastName,
                childObject.dateOfBirth
            )
            val action =
                ImmunizationRecordsFragmentDirections.actionImmunizationRecordsFragmentToUpdateRecordsFragment(
                    childObjectFromSafeArgs
                )
            findNavController().navigate(action)
        }
        immunizationDetailsListForRecyclerView.clear()
        getVaccines()
        fetchImmunizationRecords(childObject.id)
        setUpRecyclerViewAdapter()

        return binding.root
    }

    private fun setUpRecyclerViewAdapter() {
        immunizationDetailsAdapter =
            ImmunizationDetailsAdapter(immunizationDetailsListForRecyclerView)
        binding.rvSixToTwelveMonths.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSixToTwelveMonths.adapter = immunizationDetailsAdapter
    }

    private fun fetchImmunizationRecords(childID: Int) {
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

        // Calculate the end date (6 months from the start date)
        val endDate = Calendar.getInstance().apply {
            time = startDate.time
            add(Calendar.MONTH, 12)
        }
        Log.d("End date", endDate.time.toString())

        // Filter the data based on the date range
        val filteredData = immunizationDetailsListFromAPI?.filter { vaccinatedChild ->
            vaccinatedChild.vaccineAdministrationSet.any { administration ->
                val vaccineDate = dateFormat.parse(administration.dateOfAdministration)
                vaccineDate in startDate.time..endDate.time
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
        fun newInstance(childObject: AddChildResponseParcelable): SixToTwelveMonthsFragment {
            val fragment = SixToTwelveMonthsFragment()
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
