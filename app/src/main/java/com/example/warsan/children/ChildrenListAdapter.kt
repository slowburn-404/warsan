package com.example.warsan.children

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.warsan.databinding.ItemChildBinding
import com.example.warsan.databinding.ItemChildBinding.*
import com.example.warsan.models.Child

class ChildrenListAdapter(

    private var childrenList: List<Child>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<ChildrenListAdapter.ChildrenListViewHolder>() {

    inner class ChildrenListViewHolder(private val binding: ItemChildBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(child: Child) {
            binding.tvChildName.text = child.name
            binding.tvAge.text = child.age
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ChildrenListAdapter.ChildrenListViewHolder {
        val binding = inflate(LayoutInflater.from(parent.context), parent, false)
        return ChildrenListViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ChildrenListAdapter.ChildrenListViewHolder, position: Int
    ) {
        val item = childrenList[position]
        holder.bind(item)
        //Handle item click
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(item)
        }
    }

    override fun getItemCount() = childrenList.size
}