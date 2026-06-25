package com.kelompok.foodieapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kelompok.foodieapp.R
import com.kelompok.foodieapp.databinding.ItemMenuGridBinding

class GridMenuAdapter(
    private var items: List<Map<String, String>>
) : RecyclerView.Adapter<GridMenuAdapter.GridViewHolder>() {

    inner class GridViewHolder(val binding: ItemMenuGridBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val binding = ItemMenuGridBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return GridViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvGridMenuName.text = item["name"] ?: ""
        val imageUrl = item["image_url"]
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.loading_images)   // Munculkan warna ini saat gambar sedang loading
            .error(R.color.brand_secondary)         // Munculkan warna ini jika gambar gagal dimuat
            .centerCrop()                                       // Agar gambar penuh dan proporsional di dalam kotak
            .into(holder.binding.imgGridMenu)
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<Map<String, String>>) {
        items = newItems
        notifyDataSetChanged()
    }
}