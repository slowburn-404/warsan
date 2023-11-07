package com.example.warsan.children

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.warsan.R
import com.example.warsan.children.immunization.ImmunizationRecordsFragmentArgs
import com.example.warsan.databinding.FragmentChildrenListBinding
import com.example.warsan.models.AddChildResponseParcelable
import com.example.warsan.models.Child
import com.example.warsan.models.ChildDetails
import com.example.warsan.models.GuardianResponse
import com.example.warsan.network.RetrofitClient
import com.example.warsan.network.WarsanAPI
import com.google.android.material.progressindicator.CircularProgressIndicator
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

    private lateinit var guardianID: String


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

        binding.fabAddChild.setOnClickListener {
            if (guardianID != null) {
                navigateToAddChildFragment(guardianID)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Something went wrong while getting guardian details",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.tabRegisterGuardian.setNavigationOnClickListener {
            navController.popBackStack()
        }

        return binding.root
    }

    private fun addChildrenToRecyclerView(childrenListFromAPI: List<ChildDetails>?) {


        childrenListFromAPI?.forEach {
            childrenList.add(Child("${it.firstName} ${it.lastName}", it.dateOfBirth))
            Log.d("ChildrenList", "$childrenList")
        }

        childrenListAdapter = ChildrenListAdapter(childrenList, this)
        binding.rvAddChild.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAddChild.setHasFixedSize(true)
        binding.rvAddChild.adapter = childrenListAdapter

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
                    guardianID = data?.guardian?.id.toString()


                } else if (response.body() == null) {
                    progressIndicator.hide()
                    binding.fabAddChild.isEnabled = true
                    binding.rvAddChild.visibility = View.GONE
                    binding.childrenNonexistent.visibility = View.VISIBLE

                } else {
                    progressIndicator.hide()
                    binding.fabAddChild.isEnabled = true
                    Toast.makeText(requireContext(), R.string.login_failed, Toast.LENGTH_SHORT)
                        .show()

                }
            }

            override fun onFailure(call: Call<GuardianResponse>, t: Throwable) {
                // Handle network or other errors
                Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("WARSANAPIERROR", "Failed because of: ${t.message}")
            }
        })
    }

    private fun navigateToAddChildFragment(guardianID: String) {
        val navigate =
            ChildrenListFragmentDirections.actionChildrenListFragmentToRegisterChildFragment(
                guardianID
            )
        navController.navigate(navigate)

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