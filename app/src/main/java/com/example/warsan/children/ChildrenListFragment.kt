package com.example.warsan.children

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.warsan.R
import com.example.warsan.databinding.FragmentChildrenListBinding
import com.example.warsan.models.AddChildResponseParcelable
import com.example.warsan.models.Child
import com.example.warsan.models.ChildDetails
import com.example.warsan.models.GuardianResponse
import com.example.warsan.network.RetrofitClient
import com.example.warsan.network.WarsanAPI
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ChildrenListFragment : Fragment(), OnItemClickListener {

    private var _binding: FragmentChildrenListBinding? = null
    private val binding get() = _binding!!

    private lateinit var childrenListAdapter: ChildrenListAdapter
    private var childrenList = ArrayList<Child>()

    private val args: ChildrenListFragmentArgs by navArgs()
    private lateinit var navController: NavController

    private var childrenListFromAPI: List<ChildDetails>? = null

    private lateinit var progressIndicator: CircularProgressIndicator


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentChildrenListBinding.inflate(inflater, container, false)

        navController = findNavController()
        progressIndicator = binding.childListCircularProgressIndicator
        progressIndicator.hide()
        childrenList.clear()
        retrieveGuardianFromAPI()


        binding.tabRegisterGuardian.setNavigationOnClickListener {
            navController.popBackStack()
        }

        return binding.root
    }

    private fun addChildrenToRecyclerView(childrenListFromAPI: List<ChildDetails>?) {


        childrenListFromAPI?.forEach {
            val ageInMonths = calculateAgeInMonths(it.dateOfBirth)
            childrenList.add(Child("${it.firstName} ${it.lastName}", "$ageInMonths months"))
            Log.d("ChildrenList", "$childrenList")
        }

        if (isAdded) {
            context?.let {
                childrenListAdapter = ChildrenListAdapter(childrenList, this)
                binding.rvAddChild.layoutManager = LinearLayoutManager(it)
                binding.rvAddChild.setHasFixedSize(true)
                binding.rvAddChild.adapter = childrenListAdapter
            }
        }

    }

    private fun getGuardianPhoneNumber(): String? {

        return args.guardianPhoneNumber
    }

    private fun retrieveGuardianFromAPI() {
        val guardianPhoneNumber = getGuardianPhoneNumber()
        progressIndicator.show()
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
                    Log.d("WARSANAPIRESPONSECHILDLIST", "${data?.children}")

                    childrenListFromAPI = data?.children
                    addChildrenToRecyclerView(childrenListFromAPI)

                    binding.tabRegisterGuardian.title =
                        "${data?.guardian?.firstName} ${data?.guardian?.lastName}"
                    //pass guardian ID
                    val guardianID = data?.guardian?.id
                    binding.fabAddChild.setOnClickListener {
                        guardianID?.let {
                            navigateToAddChildFragment(guardianID)
                        }
                    }


                } else if (response.body() == null) {
                    progressIndicator.hide()
                    binding.fabAddChild.isEnabled = true
                    binding.rvAddChild.visibility = View.GONE
                    binding.childrenNonexistent.visibility = View.VISIBLE

                } else {
                    progressIndicator.hide()
                    binding.fabAddChild.isEnabled = true
                    Snackbar.make(
                        binding.root,
                        "Something went wrong, please try again",
                        Snackbar.LENGTH_SHORT
                    )
                        .show()

                }
            }

            override fun onFailure(call: Call<GuardianResponse>, t: Throwable) {
                // Handle network or other errors
                Snackbar.make(binding.root, "${t.message}", Snackbar.LENGTH_SHORT).show()
                Log.e("WARSANAPIERROR", "Failed because of: ${t.message}")
            }
        })
    }

    private fun navigateToAddChildFragment(guardianID: Int) {
        val navigate =
            ChildrenListFragmentDirections.actionChildrenListFragmentToRegisterChildFragment(
                guardianID.toString()
            )
        navController.navigate(navigate)

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


    override fun onItemClick(item: Child) {
        childrenListFromAPI?.find { childDetails ->
            (childDetails.firstName + " " + childDetails.lastName) == item.name
        }?.let { childDetails ->
            val childObject = AddChildResponseParcelable(
                childDetails.id,
                childDetails.firstName,
                childDetails.lastName,
                childDetails.dateOfBirth
            )
            val action =
                ChildrenListFragmentDirections.actionChildrenListFragmentToImmunizationRecordsFragment(
                    childObject = childObject
                )
            navController.navigate(action)
        }


    }

}