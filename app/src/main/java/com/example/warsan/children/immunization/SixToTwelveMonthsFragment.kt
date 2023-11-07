package com.example.warsan.children.immunization

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.warsan.R
import com.example.warsan.databinding.FragmentSixToTwelveMonthsBinding
import com.example.warsan.models.AddChildResponseParcelable
import com.example.warsan.models.VaccinatedChild
import com.example.warsan.network.RetrofitClient
import com.example.warsan.network.WarsanAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SixToTwelveMonthsFragment : Fragment() {

    private var _binding: FragmentSixToTwelveMonthsBinding? = null
    private val binding get() = _binding!!

    val args: ImmunizationRecordsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentSixToTwelveMonthsBinding.inflate(inflater, container, false)





        return binding.root
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