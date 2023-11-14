package com.example.warsan.children.immunization

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.warsan.databinding.ItemImmunizationDetailsBinding
import com.example.warsan.models.ImmunizationDetails

class ImmunizationDetailsAdapter(
    private var immunizationDetailsList: MutableList<ImmunizationDetails>,
) : RecyclerView.Adapter<ImmunizationDetailsAdapter.ImmunizationDetailsViewHolder>() {

    inner class ImmunizationDetailsViewHolder(private val binding: ItemImmunizationDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(vaccine: ImmunizationDetails) {
            binding.tvVaccine.text = vaccine.name
            vaccine.status?.let { binding.ivStatus.setImageResource(it) }
            binding.tvNextDate.text = vaccine.dateTaken
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ImmunizationDetailsAdapter.ImmunizationDetailsViewHolder {
        val binding = ItemImmunizationDetailsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImmunizationDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ImmunizationDetailsAdapter.ImmunizationDetailsViewHolder, position: Int
    ) {
        val item = immunizationDetailsList[position]
        holder.bind(item)

    }

    override fun getItemCount() = immunizationDetailsList.size
}