package com.example.warsan.children.immunization

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.warsan.R
import com.example.warsan.children.ChildrenListAdapter
import com.example.warsan.databinding.FragmentSixToTwelveMonthsBinding
import com.example.warsan.databinding.FragmentZeroToSixMonthsBinding
import com.example.warsan.models.AddChildResponseParcelable
import com.example.warsan.models.Child
import com.example.warsan.models.ImmunizationDetails
import com.example.warsan.models.VaccinatedChild
import com.example.warsan.models.Vaccines
import com.example.warsan.network.RetrofitClient
import com.example.warsan.network.WarsanAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ZeroToSixMonthsFragment : Fragment() {
    private var _binding: FragmentZeroToSixMonthsBinding? = null
    private val binding get() = _binding!!

    private val args: ImmunizationRecordsFragmentArgs by navArgs()

    private lateinit var immunizationDetailsAdapter: ImmunizationDetailsAdapter
    private var immunizationDetailsList = ArrayList<ImmunizationDetails>()

    private val vaccinesList = mutableListOf<Vaccines>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentZeroToSixMonthsBinding.inflate(inflater, container, false)

        immunizationDetailsList.clear()
        setUpRecyclerViewAdapter()
        getVaccines()
        fetchImmunizationRecords()




        return binding.root
    }

    private fun setUpRecyclerViewAdapter() {
                immunizationDetailsAdapter = ImmunizationDetailsAdapter(immunizationDetailsList)
        binding.rvZeroToSixMonths.layoutManager = LinearLayoutManager(requireContext())
        binding.rvZeroToSixMonths.setHasFixedSize(true)
        binding.rvZeroToSixMonths.adapter = immunizationDetailsAdapter
    }

    private fun fetchImmunizationRecords() {
        val childID = 1

        if(childID != null) {
            val warsanAPI = RetrofitClient.instance.create(WarsanAPI::class.java)
            val call = warsanAPI.getImmunizationRecords(childID)

            call.enqueue(object : Callback<List<VaccinatedChild>> {
                override fun onResponse(
                    call: Call<List<VaccinatedChild>>,
                    response: Response<List<VaccinatedChild>>
                ) {
                    if (response.isSuccessful) {
                        val vaccinatedChild = response.body()
                        // Handle the data here
                        val updatedImmunizationDetailsList = ArrayList<ImmunizationDetails>()


                        vaccinatedChild?.forEach { vaccinatedChild ->
                            binding.tvNextDueDate.text = vaccinatedChild.nextDateOfAdministration

                            // Create a list for each VaccinatedChild
                            val immunizationDetailsListForChild = ArrayList<ImmunizationDetails>()
                            immunizationDetailsListForChild.add(
                                convertVaccinatedChildToImmunizationDetails(vaccinatedChild)
                            )

                            updatedImmunizationDetailsList.addAll(immunizationDetailsListForChild)
                        }
                        immunizationDetailsList.clear()
                        immunizationDetailsList.addAll(updatedImmunizationDetailsList)

                        immunizationDetailsAdapter.notifyDataSetChanged()


                    } else {
                        // Handle the error
                        Toast.makeText(
                            requireContext(),
                            "Failed to fetch immunization record, please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("WARSANAPIERROR", "Failed to fetch immunization record")
                    }
                }

                override fun onFailure(call: Call<List<VaccinatedChild>>, t: Throwable) {
                    // Handle network or other errors
                    Log.e("WARSANAPIERROR", "Failed because of: ${t.message}")
                }
            })
        }else {
            Toast.makeText(requireContext(),"No child ID", Toast.LENGTH_SHORT).show()
        }
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

    private fun getDrawableForStatus(status: String): Int {
        return when (status) {
            "Taken" -> R.drawable.tick
            else -> R.drawable.cross
        }

    }

    private fun convertVaccinatedChildToImmunizationDetails(
        vaccinatedChild: VaccinatedChild,
    ): ImmunizationDetails {
        val statusDrawable = getDrawableForStatus(vaccinatedChild.status)

        val vaccineId = vaccinatedChild.vaccineAdministrationSet
            .firstOrNull() // You might need to adjust this based on your logic to select the correct vaccine
            ?.vaccine?.toString() ?: "Unknown Vaccine"

        val vaccineChoice =
            vaccinesList.find { it.id.toString() == vaccineId }?.vaccineChoice ?: "Unknown Vaccine"

        val dateOfAdministration = vaccinatedChild.vaccineAdministrationSet
            .firstOrNull() // You might need to adjust this based on your logic to select the correct date
            ?.dateOfAdministration ?: "Unknown Date"



        return ImmunizationDetails(vaccineChoice, statusDrawable, dateOfAdministration)
    }
    companion object {
        // Create a companion object with a newInstance method that accepts arguments
        fun newInstance(childObject: AddChildResponseParcelable): ZeroToSixMonthsFragment {
            val fragment = ZeroToSixMonthsFragment()
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