package com.example.warsan.children

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.warsan.R
import com.example.warsan.databinding.FragmentChildrenListBinding
import com.example.warsan.models.Child


class ChildrenListFragment : Fragment(), OnItemClickListener {

    private var _binding: FragmentChildrenListBinding? = null
    private val binding get() = _binding!!

    private lateinit var childrenListAdapter: ChildrenListAdapter
    private var childrenList = ArrayList<Child>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentChildrenListBinding.inflate(inflater, container, false)

        val navController = findNavController()
        childrenList.clear()
        addChildren()

        binding.fabAddChild.setOnClickListener {
            navController.navigate(R.id.action_childrenListFragment_to_registerChildFragment)
        }
        binding.tabRegisterGuardian.setNavigationOnClickListener {
            navController.popBackStack()
        }

        return binding.root
    }

    private fun addChildren(){
        for(i in 1..1 ){

            childrenList.add(Child("Clever Kaitaramirwa", "1 month old"))
            childrenList.add(Child("Pauline Ochieng'", "16 month old"))
            childrenList.add(Child("Anfar Bashir Mohamud", "24 month old"))
            childrenList.add(Child("Anfar Bashir Mohamud", "36 month old"))

        }

        childrenListAdapter = ChildrenListAdapter(childrenList, this)
        binding.rvAddChild.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAddChild.setHasFixedSize(true)
        binding.rvAddChild.adapter = childrenListAdapter
    }

    override fun onItemClick(item: Child) {
        findNavController().navigate(R.id.action_childrenListFragment_to_immunizationRecordsFragment)
    }

}