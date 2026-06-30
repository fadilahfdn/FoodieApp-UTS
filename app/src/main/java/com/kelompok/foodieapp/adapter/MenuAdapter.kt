package com.kelompok.foodieapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kelompok.foodieapp.R
import com.kelompok.foodieapp.data.MenuItem
import com.kelompok.foodieapp.databinding.ItemMenuCardBinding

class MenuAdapter(
    private var items: List<MenuItem>,
    private val onItemClick: (MenuItem) -> Unit
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    inner class MenuViewHolder(val binding: ItemMenuCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = ItemMenuCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            imgMenu.setImageResource(item.imageRes)
            tvMenuName.text   = item.name
            tvMenuDesc.text   = item.description
            tvMenuPrice.text  = "Rp ${String.format("%,d", item.price).replace(',', '.')}"
            tvMenuRating.text = "⭐ ${item.rating}"
            root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<MenuItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}