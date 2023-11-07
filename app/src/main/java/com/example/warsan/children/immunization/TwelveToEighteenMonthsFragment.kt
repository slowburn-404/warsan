package com.example.warsan.children.immunization

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.warsan.R
import com.example.warsan.databinding.FragmentTwelveToEighteenMonthsBinding
import com.example.warsan.models.AddChildResponseParcelable


class TwelveToEighteenMonthsFragment : Fragment() {
    private var _binding: FragmentTwelveToEighteenMonthsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTwelveToEighteenMonthsBinding.inflate(inflater, container, false)
        return inflater.inflate(R.layout.fragment_twelve_to_eighteen_months, container, false)
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