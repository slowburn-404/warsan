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
import com.example.warsan.databinding.FragmentChildrenListBinding
import com.example.warsan.models.Child
import com.example.warsan.models.ChildDetails
import com.example.warsan.models.GuardianResponse
import com.example.warsan.network.RetrofitClient
import com.example.warsan.network.WarsanAPI
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentChildrenListBinding.inflate(inflater, container, false)

        navController = findNavController()
        childrenList.clear()
        retrieveGuardianFromAPI()

        binding.fabAddChild.setOnClickListener {
            navController.navigate(R.id.action_childrenListFragment_to_registerChildFragment)
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
        }/* for (i in 1..1) {

                 childrenList.add(Child("Clever Kaitaramirwa", "1 month old"))
                 childrenList.add(Child("Pauline Ochieng'", "16 months old"))
                 childrenList.add(Child("Anfar BashirMohamud", "24 month old"))
                 childrenList.add(Child("Anfar BashirMohamud", "36 month old"))

             }*/



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
        //Create api service
        val warsanAPI = RetrofitClient.instance.create(WarsanAPI::class.java)

        //Make a get request
        val call: Call<GuardianResponse> = warsanAPI.retrieveGuardian(guardianPhoneNumber)

        call.enqueue(object : Callback<GuardianResponse> {
            override fun onResponse(
                call: Call<GuardianResponse>, response: Response<GuardianResponse>
            ) {
                if (response.isSuccessful) {
                    //progressIndicator.hide()
                    val data: GuardianResponse? = response.body()
                    Log.d("WARSANAPIRESPONSECHILDLIST", "${data?.children}")

                    childrenListFromAPI = data?.children
                    addChildrenToRecyclerView(childrenListFromAPI)

                } else if (response.body() == null) {
                    // progressIndicator.hide()
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.guardian_children_nonexistent),
                        Toast.LENGTH_LONG
                    ).show()
                    // btRetrieveGuardian.isEnabled = true

                } else {
                    //progressIndicator.hide()
                    Toast.makeText(requireContext(), R.string.login_failed, Toast.LENGTH_SHORT)
                        .show()
                    // btRetrieveGuardian.isEnabled = true
                }
            }

            override fun onFailure(call: Call<GuardianResponse>, t: Throwable) {
                // Handle network or other errors
                //progressIndicator.hide()
                Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("WARSANAPIERROR", "Failed because of: ${t.message}")
            }
        })
    }


    override fun onItemClick(item: Child) {
        findNavController().navigate(R.id.action_childrenListFragment_to_immunizationRecordsFragment)
    }

}