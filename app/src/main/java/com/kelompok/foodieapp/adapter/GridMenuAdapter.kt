package com.kelompok.foodieapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kelompok.foodieapp.FoodDetailActivity
import com.kelompok.foodieapp.R
import com.kelompok.foodieapp.databinding.ItemMenuGridBinding

class GridMenuAdapter(
    private var items: MutableList<Map<String, String>> 
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
        val intent = Intent(holder.itemView.context, FoodDetailActivity::class.java)
        val item = items[position]
        holder.binding.tvGridMenuName.text = item["name"] ?: ""

        val imageUrl = item["image_url"] ?: ""
        
        if (imageUrl.startsWith("http")) {
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.loading_images)
                .error(R.drawable.ic_launcher_background)
                .centerCrop()
                .into(holder.binding.imgGridMenu)
        } else {
            // Local resource from SQLite
            val resId = holder.itemView.context.resources.getIdentifier(
                imageUrl, "drawable", holder.itemView.context.packageName
            )
            holder.binding.imgGridMenu.setImageResource(
                if (resId != 0) resId else R.drawable.ic_launcher_background
            )
        }

        holder.itemView.setOnClickListener {
            val apiIdString = item["id"] ?: "0"
            val menuId = apiIdString.toIntOrNull() ?: (10000..99999).random()
            val priceString = item["price"] ?: "0"
            val menuPrice = priceString.toIntOrNull() ?: 0

            intent.putExtra("MENU_ID", menuId)
            intent.putExtra("MENU_NAME", item["name"])
            
            // Check if it's a local image name or a URL
            val imageValue = item["image_url"] ?: ""
            if (imageValue.startsWith("http")) {
                intent.putExtra("MENU_IMAGE", imageValue)
            } else {
                val resId = holder.itemView.context.resources.getIdentifier(
                    imageValue, "drawable", holder.itemView.context.packageName
                )
                intent.putExtra("MENU_IMAGE", resId)
            }

            intent.putExtra("MENU_PRICE", menuPrice)
            intent.putExtra("MENU_DESC", item["description"])
            intent.putExtra("MENU_CATEGORY", item["category"])
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = items.size

    // Fungsi untuk memuat ulang dari awal (jika ganti kategori di atas)
    fun resetData(newItems: List<Map<String, String>>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    // Fungsi untuk menambah data di bawah (Infinite Scroll)
    fun appendData(newItems: List<Map<String, String>>) {
        val startPosition = items.size
        items.addAll(newItems)
        // Animasi halus saat data bertambah di bawah
        notifyItemRangeInserted(startPosition, newItems.size)
    }
}